--@weapon missile range=900 reload=2.5 damage=120
--@invulnerable
-- Gentleman: dances on the spot and unleashes a never-ending apocalypse. Rockets rain on the whole
-- map without a pause (about a dozen per second) and every few seconds a salvo of big missiles
-- goes off: how many depends on the power of the weapon (2-3 nukes, or more tactical missiles).
-- On top of that, the "--@weapon" line above gives it a homing missile launcher for close defence.
-- It cannot be destroyed: the "--@invulnerable" line makes the map cancel every damage (D-gun
-- included), and the script also heals the unit and cancels the hits it takes.
-- The pieces are the objects of the OBJ file: body (root), left_arm, right_arm, left_foot, right_foot.

local body = piece "body"
local left_arm = piece "left_arm"
local right_arm = piece "right_arm"
local left_foot = piece "left_foot"
local right_foot = piece "right_foot"

-- ====================== SETTINGS ======================
local RANGE = 900             -- close defence: same as "range" in the --@weapon line
local BEAT = 400              -- length of one beat of the dance, in milliseconds
local START_DELAY = 15000     -- truce at the start of the match (ms)
local ROCKETS_PER_SECOND = 12 -- continuous rain of small rockets on the whole map
local SALVO_EVERY = 10000     -- how often a salvo of big missiles goes off (ms)
local SALVO_POWER = 25000     -- total damage of a salvo: number of missiles = power / damage of the missile
local SALVO_MIN = 2           -- fewest big missiles in a salvo
local SALVO_MAX = 8           -- most big missiles in a salvo
local ON_ENEMIES = 0.5        -- 0..1: share of the shots aimed near enemy units (the rest land at random)
local SPREAD = 350            -- how far from the enemy unit an aimed shot can land (elmos)
local LAUNCH_FROM = "sky"     -- "sky": the missiles rain from above onto the target (safer and denser)
                              -- "arms": they leave from the arms of the gentleman and fly to the target
local SKY_HEIGHT = 1400       -- height the missiles rain from in "sky" mode (elmos)
local EDGE_MARGIN = 200       -- targets stay at least this far from the map edge (elmos)

-- BAR units the missiles are taken from. A name that does not exist in this version
-- of the game is simply skipped.
local BIG_UNITS = { "armsilo", "corsilo", "legsilo", "cortron", "armemp" }
local SMALL_UNITS = { "armrock", "corstorm", "armmerl", "corvroc", "armmh", "cormh" }
-- =======================================================

local CMD_ATTACK = 20
local rad = math.rad
local beatSpeed = 1000 / BEAT   -- "angle per beat" -> "angle per second"
local bounce = 4                -- height of the hop, recomputed in Create from the size of the unit
local nextArm = left_arm        -- arm the next missile leaves from

local mapX = (Game and Game.mapSizeX) or 8192
local mapZ = (Game and Game.mapSizeZ) or 8192

-- Collects the missile launchers (MissileLauncher / StarburstLauncher) of the listed units.
local function CollectMissiles(unitNames)
	local found = { n = 0 }   -- n = how many missiles it holds
	if not (UnitDefNames and WeaponDefs) then
		return found
	end
	for _, name in ipairs(unitNames) do
		local ud = UnitDefNames[name]
		if ud and ud.weapons then
			for _, w in ipairs(ud.weapons) do
				local wd = WeaponDefs[w.weaponDef]
				if wd and (wd.type == "MissileLauncher" or wd.type == "StarburstLauncher") then
					found.n = found.n + 1
					found[found.n] = w.weaponDef
				end
			end
		end
	end
	return found
end

local bigMissiles = { n = 0 }
local smallMissiles = { n = 0 }

-- The arms of the model point diagonally: to raise and lower them
-- they are turned on X and on Z together.
local function Flap(arm, angle, speed)
	Turn(arm, x_axis, angle, speed)
	Turn(arm, z_axis, angle, speed)
end

local function Dance()
	local twist = 0
	local step = 0
	while true do
		step = step + 1
		local side = (step % 2 == 0) and 1 or -1

		-- body: hop, hip sway and twist to the right and to the left
		Move(body, y_axis, bounce, bounce * 2 * beatSpeed)
		Turn(body, z_axis, rad(7) * side, rad(14) * beatSpeed)
		Turn(body, y_axis, twist + rad(22) * side, rad(44) * beatSpeed)

		-- arms: one up and one down, in turn
		Flap(left_arm, rad(38) * side, rad(76) * beatSpeed)
		Flap(right_arm, rad(-38) * side, rad(76) * beatSpeed)
		Turn(left_arm, y_axis, rad(25) * side, rad(50) * beatSpeed)
		Turn(right_arm, y_axis, rad(25) * side, rad(50) * beatSpeed)

		-- feet: alternate kick, forwards (the model faces +Z like every BAR unit)
		Turn(left_foot, x_axis, rad(-30) * math.max(0, side), rad(60) * beatSpeed)
		Turn(left_foot, z_axis, rad(30) * math.max(0, side), rad(60) * beatSpeed)
		Turn(right_foot, x_axis, rad(-30) * math.max(0, -side), rad(60) * beatSpeed)
		Turn(right_foot, z_axis, rad(30) * math.max(0, -side), rad(60) * beatSpeed)

		Sleep(BEAT / 2)
		Move(body, y_axis, 0, bounce * 2 * beatSpeed)
		Sleep(BEAT / 2)

		-- every 8 beats a full pirouette
		if step % 8 == 0 then
			twist = twist + rad(360)
			Turn(body, y_axis, twist, rad(360) * beatSpeed / 2)
			Move(body, y_axis, bounce * 2, bounce * 2 * beatSpeed)
			Sleep(BEAT)
			Move(body, y_axis, 0, bounce * 2 * beatSpeed)
			Sleep(BEAT)
		end
	end
end

-- Close defence: looks for the nearest enemy and orders an attack on it (works for a Gaia unit too).
local function Hunt()
	while true do
		local target = Spring.GetUnitNearestEnemy(unitID, RANGE, false)
		if target then
			Spring.GiveOrderToUnit(unitID, CMD_ATTACK, { target }, {})
		end
		Sleep(1500)
	end
end

local launched = 0
local failed = 0

-- Launches a missile of type weaponDefID towards the point (tx, tz) of the map.
local function Launch(weaponDefID, tx, tz)
	local wd = WeaponDefs[weaponDefID]
	if not wd then
		return
	end
	local arm = nextArm
	nextArm = (nextArm == left_arm) and right_arm or left_arm
	local ty = math.max(0, Spring.GetGroundHeight(tx, tz) or 0)
	local speed = math.max(4, wd.projectilespeed or 10) -- elmos per frame
	local x, y, z
	if LAUNCH_FROM == "sky" then
		-- it appears high up, slightly off to one side, and dives onto the target
		x = tx + (math.random() - 0.5) * 500
		z = tz + (math.random() - 0.5) * 500
		y = ty + SKY_HEIGHT
	else
		x, y, z = Spring.GetUnitPiecePosDir(unitID, arm)
		if not x then
			x, y, z = Spring.GetUnitPosition(unitID)
		end
		y = y + 10
	end
	local dx, dy, dz = tx - x, ty - y, tz - z
	local dist = math.sqrt(dx * dx + dy * dy + dz * dz)
	local params = {
		pos = { x, y, z },
		["end"] = { tx, ty, tz },
		owner = unitID,
		team = Spring.GetUnitTeam(unitID),
		maxRange = dist + 500,
		ttl = math.floor(dist / speed) + 300,
	}
	if wd.type == "StarburstLauncher" and LAUNCH_FROM ~= "sky" then
		-- from the arms: it climbs straight up, then turns towards the target (nuke, tactical missiles, EMP)
		params.speed = { 0, speed, 0 }
		params.upTime = (wd.uptime or 3) * 30
		params.ttl = params.ttl + params.upTime + 600
	else
		-- already aimed at the target; from the arms slightly upwards, to clear the hills
		local lift = (LAUNCH_FROM == "sky") and 0 or dist * 0.15
		local len = math.sqrt(dx * dx + (dy + lift) * (dy + lift) + dz * dz)
		params.speed = { dx / len * speed, (dy + lift) / len * speed, dz / len * speed }
		params.upTime = 0
		params.tracking = true
	end
	if Spring.SpawnProjectile(weaponDefID, params) then
		launched = launched + 1
	else
		failed = failed + 1
	end
	-- throwing gesture of the arm
	Turn(arm, y_axis, rad(-60), rad(900))
end

-- Damage of the missile against a normal target (used to decide how many to launch).
local function DamageOf(weaponDefID)
	local wd = WeaponDefs[weaponDefID]
	local damage = wd and wd.damages and (wd.damages[0] or wd.damages[1])
	if not damage or damage <= 0 then
		damage = 200
	end
	return damage
end

-- Picks the target: near a random enemy unit, or a random point of the map.
local function PickTarget()
	if math.random() < ON_ENEMIES then
		local myAllyTeam = Spring.GetUnitAllyTeam(unitID)
		local all = Spring.GetAllUnits()
		local count = table.getn(all)
		for attempt = 1, 6 do
			local other = count > 0 and all[math.random(count)]
			if other and other ~= unitID and Spring.GetUnitAllyTeam(other) ~= myAllyTeam then
				local ux, _, uz = Spring.GetUnitPosition(other)
				if ux then
					local tx = ux + (math.random() - 0.5) * 2 * SPREAD
					local tz = uz + (math.random() - 0.5) * 2 * SPREAD
					return math.max(0, math.min(mapX, tx)), math.max(0, math.min(mapZ, tz))
				end
			end
		end
	end
	return EDGE_MARGIN + math.random() * math.max(1, mapX - 2 * EDGE_MARGIN),
		EDGE_MARGIN + math.random() * math.max(1, mapZ - 2 * EDGE_MARGIN)
end

-- Continuous rain: without a pause, ROCKETS_PER_SECOND small rockets on the whole map.
local function Rain()
	Sleep(START_DELAY)
	local perShot = math.max(1, math.floor(ROCKETS_PER_SECOND / 4 + 0.5))
	while true do
		local list = (smallMissiles.n > 0) and smallMissiles or bigMissiles
		if list.n > 0 then
			for i = 1, perShot do
				local tx, tz = PickTarget()
				Launch(list[math.random(list.n)], tx, tz)
			end
		end
		Sleep(250)
	end
end

-- Big salvos: every SALVO_EVERY one random kind of big missile, as many as it takes
-- to reach SALVO_POWER (2-3 nukes, or more tactical or EMP missiles).
local function Salvos()
	Sleep(START_DELAY)
	while true do
		if bigMissiles.n > 0 then
			local weaponDefID = bigMissiles[math.random(bigMissiles.n)]
			local count = math.floor(SALVO_POWER / DamageOf(weaponDefID) + 0.5)
			count = math.max(SALVO_MIN, math.min(SALVO_MAX, count))
			for i = 1, count do
				local tx, tz = PickTarget()
				Launch(weaponDefID, tx, tz)
				Sleep(400)
			end
		end
		Sleep(SALVO_EVERY)
	end
end

-- Diagnostics: every 30 seconds writes into the BAR log (infolog.txt) how many missiles went off.
local function Diary()
	while true do
		Sleep(30000)
		Spring.Echo("[gentleman] missiles launched: " .. launched .. ", failed: " .. failed)
	end
end

-- Indestructible: every tenth of a second restores full health and removes paralysis and capture.
local function Immortal()
	Spring.SetUnitArmored(unitID, true, 0)
	while true do
		local _, maxHealth = Spring.GetUnitHealth(unitID)
		if maxHealth then
			Spring.SetUnitHealth(unitID, { health = maxHealth, paralyze = 0, capture = 0 })
		end
		Sleep(100)
	end
end

-- Every hit taken does zero damage.
function script.HitByWeapon(x, z, weaponDefID, damage)
	return 0
end

function script.Create()
	local height = Spring.GetUnitHeight(unitID) or 40
	bounce = math.max(2, height * 0.06)
	bigMissiles = CollectMissiles(BIG_UNITS)
	smallMissiles = CollectMissiles(SMALL_UNITS)
	Spring.Echo("[gentleman] ready: " .. smallMissiles.n .. " kinds of rockets, " .. bigMissiles.n .. " kinds of big missiles, launching from " .. LAUNCH_FROM)
	StartThread(Immortal)
	StartThread(Dance)
	StartThread(Hunt)
	StartThread(Rain)
	StartThread(Salvos)
	StartThread(Diary)
end

-- Weapon 1 (close defence): the missile leaves from an arm, left and right in turn.
function script.AimFromWeapon(num)
	return body
end

function script.QueryWeapon(num)
	return nextArm
end

function script.AimWeapon(num, heading, pitch)
	return true   -- no turret to aim: the missile finds the target by itself
end

function script.FireWeapon(num)
	-- recoil of the arm that fired, then it is the turn of the other one
	local arm = nextArm
	Turn(arm, y_axis, rad(-60), rad(900))
	nextArm = (nextArm == left_arm) and right_arm or left_arm
end

function script.Killed(recentDamage, maxHealth)
	return 1
end
