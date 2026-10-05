--@weapon missile range=900 reload=2.5 damage=120
--@invulnerable
-- Gentleman: balla sul posto e scatena un'apocalisse continua. Senza pause piovono razzi su tutta
-- la mappa (una decina al secondo) e ogni pochi secondi parte una salva di missili grossi:
-- il loro numero dipende dalla potenza dell'arma (2-3 nucleari, oppure piu' missili tattici).
-- In piu' la riga "--@weapon" qui sopra gli da' un lanciamissili a ricerca per la difesa vicina.
-- E' indistruttibile: la riga "--@invulnerable" fa azzerare alla mappa ogni danno (D-gun compreso),
-- e lo script in piu' si cura da solo e annulla i colpi che riceve.
-- I pezzi sono gli oggetti dell'OBJ: body (radice), left_arm, right_arm, left_foot, right_foot.

local body = piece "body"
local left_arm = piece "left_arm"
local right_arm = piece "right_arm"
local left_foot = piece "left_foot"
local right_foot = piece "right_foot"

-- ===================== REGOLAZIONI =====================
local RANGE = 900            -- difesa vicina: come "range" nella riga --@weapon
local BEAT = 400             -- durata di un tempo del ballo, in millisecondi
local ATTESA_INIZIALE = 15000 -- secondi di tregua a inizio partita (ms)
local RAZZI_AL_SECONDO = 12  -- pioggia continua di razzi piccoli su tutta la mappa
local SALVA_OGNI = 10000     -- ogni quanto parte una salva di missili grossi (ms)
local POTENZA_SALVA = 25000  -- danno totale di una salva: numero di missili = potenza / danno del missile
local SALVA_MIN = 2          -- minimo di missili grossi per salva
local SALVA_MAX = 8          -- massimo di missili grossi per salva
local SUI_NEMICI = 0.5       -- 0..1: parte dei colpi mirata vicino a unita' nemiche (il resto e' a caso sulla mappa)
local DISPERSIONE = 350      -- quanto lontano dall'unita' nemica puo' cadere un colpo mirato (elmi)
local PARTENZA = "cielo"     -- "cielo": i missili piovono dall'alto sul bersaglio (piu' sicuro e piu' fitto)
                             -- "braccia": partono dalle braccia del gentleman e volano fino al bersaglio
local QUOTA_CIELO = 1400     -- altezza da cui piovono i missili in modalita' "cielo" (elmi)
local MARGINE_BORDO = 200    -- i bersagli stanno almeno a questa distanza dal bordo mappa (elmi)

-- Unita' di BAR da cui prendere i missili. Se un nome non esiste in questa versione
-- del gioco viene semplicemente saltato.
local UNITA_GROSSE = { "armsilo", "corsilo", "legsilo", "cortron", "armemp" }
local UNITA_PICCOLE = { "armrock", "corstorm", "armmerl", "corvroc", "armmh", "cormh" }
-- =======================================================

local CMD_ATTACK = 20
local rad = math.rad
local beatSpeed = 1000 / BEAT   -- "angolo al tempo" -> "angolo al secondo"
local bounce = 4                -- altezza del saltello, ricalcolata in Create dalla taglia dell'unita'
local nextArm = left_arm        -- braccio da cui parte il prossimo missile

local mapX = (Game and Game.mapSizeX) or 8192
local mapZ = (Game and Game.mapSizeZ) or 8192

-- Raccoglie i lanciamissili (MissileLauncher / StarburstLauncher) delle unita' elencate.
local function RaccogliMissili(nomiUnita)
	local trovati = { n = 0 }   -- n = quanti missili contiene
	if not (UnitDefNames and WeaponDefs) then
		return trovati
	end
	for _, nome in ipairs(nomiUnita) do
		local ud = UnitDefNames[nome]
		if ud and ud.weapons then
			for _, w in ipairs(ud.weapons) do
				local wd = WeaponDefs[w.weaponDef]
				if wd and (wd.type == "MissileLauncher" or wd.type == "StarburstLauncher") then
					trovati.n = trovati.n + 1
					trovati[trovati.n] = w.weaponDef
				end
			end
		end
	end
	return trovati
end

local missiliGrossi = { n = 0 }
local missiliPiccoli = { n = 0 }

-- Le braccia del modello puntano in diagonale: per alzarle e abbassarle
-- le ruoto insieme su X e su Z.
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

		-- corpo: saltello, ancheggiamento e torsione a destra e a sinistra
		Move(body, y_axis, bounce, bounce * 2 * beatSpeed)
		Turn(body, z_axis, rad(7) * side, rad(14) * beatSpeed)
		Turn(body, y_axis, twist + rad(22) * side, rad(44) * beatSpeed)

		-- braccia: una su e una giu', alternate
		Flap(left_arm, rad(38) * side, rad(76) * beatSpeed)
		Flap(right_arm, rad(-38) * side, rad(76) * beatSpeed)
		Turn(left_arm, y_axis, rad(25) * side, rad(50) * beatSpeed)
		Turn(right_arm, y_axis, rad(25) * side, rad(50) * beatSpeed)

		-- piedi: calcio alternato
		Turn(left_foot, x_axis, rad(30) * math.max(0, side), rad(60) * beatSpeed)
		Turn(left_foot, z_axis, rad(-30) * math.max(0, side), rad(60) * beatSpeed)
		Turn(right_foot, x_axis, rad(30) * math.max(0, -side), rad(60) * beatSpeed)
		Turn(right_foot, z_axis, rad(-30) * math.max(0, -side), rad(60) * beatSpeed)

		Sleep(BEAT / 2)
		Move(body, y_axis, 0, bounce * 2 * beatSpeed)
		Sleep(BEAT / 2)

		-- ogni 8 tempi una piroetta completa
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

-- Difesa vicina: cerca il nemico piu' vicino e ordina di attaccarlo (vale anche se l'unita' e' di Gaia).
local function Hunt()
	while true do
		local target = Spring.GetUnitNearestEnemy(unitID, RANGE, false)
		if target then
			Spring.GiveOrderToUnit(unitID, CMD_ATTACK, { target }, {})
		end
		Sleep(1500)
	end
end

local lanciati = 0
local falliti = 0

-- Lancia un missile di tipo weaponDefID verso il punto (tx, tz) della mappa.
local function Lancia(weaponDefID, tx, tz)
	local wd = WeaponDefs[weaponDefID]
	if not wd then
		return
	end
	local arm = nextArm
	nextArm = (nextArm == left_arm) and right_arm or left_arm
	local ty = math.max(0, Spring.GetGroundHeight(tx, tz) or 0)
	local speed = math.max(4, wd.projectilespeed or 10) -- elmi per frame
	local x, y, z
	if PARTENZA == "cielo" then
		-- nasce in alto, un po' spostato, e scende in picchiata sul bersaglio
		x = tx + (math.random() - 0.5) * 500
		z = tz + (math.random() - 0.5) * 500
		y = ty + QUOTA_CIELO
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
	if wd.type == "StarburstLauncher" and PARTENZA ~= "cielo" then
		-- dalle braccia: sale in verticale, poi vira verso il bersaglio (nuke, missili tattici, EMP)
		params.speed = { 0, speed, 0 }
		params.upTime = (wd.uptime or 3) * 30
		params.ttl = params.ttl + params.upTime + 600
	else
		-- gia' puntato sul bersaglio; dalle braccia un po' verso l'alto per scavalcare i rilievi
		local lift = (PARTENZA == "cielo") and 0 or dist * 0.15
		local len = math.sqrt(dx * dx + (dy + lift) * (dy + lift) + dz * dz)
		params.speed = { dx / len * speed, (dy + lift) / len * speed, dz / len * speed }
		params.upTime = 0
		params.tracking = true
	end
	if Spring.SpawnProjectile(weaponDefID, params) then
		lanciati = lanciati + 1
	else
		falliti = falliti + 1
	end
	-- gesto di lancio sul braccio
	Turn(arm, y_axis, rad(-60), rad(900))
end

-- Danno del missile contro un bersaglio normale (serve a decidere quanti lanciarne).
local function Danno(weaponDefID)
	local wd = WeaponDefs[weaponDefID]
	local danno = wd and wd.damages and (wd.damages[0] or wd.damages[1])
	if not danno or danno <= 0 then
		danno = 200
	end
	return danno
end

-- Sceglie il bersaglio: vicino a un'unita' nemica a caso, oppure un punto a caso della mappa.
local function Bersaglio()
	if math.random() < SUI_NEMICI then
		local mioAlleato = Spring.GetUnitAllyTeam(unitID)
		local tutte = Spring.GetAllUnits()
		local quante = table.getn(tutte)
		for tentativo = 1, 6 do
			local altra = quante > 0 and tutte[math.random(quante)]
			if altra and altra ~= unitID and Spring.GetUnitAllyTeam(altra) ~= mioAlleato then
				local ux, _, uz = Spring.GetUnitPosition(altra)
				if ux then
					local tx = ux + (math.random() - 0.5) * 2 * DISPERSIONE
					local tz = uz + (math.random() - 0.5) * 2 * DISPERSIONE
					return math.max(0, math.min(mapX, tx)), math.max(0, math.min(mapZ, tz))
				end
			end
		end
	end
	return MARGINE_BORDO + math.random() * math.max(1, mapX - 2 * MARGINE_BORDO),
		MARGINE_BORDO + math.random() * math.max(1, mapZ - 2 * MARGINE_BORDO)
end

-- Pioggia continua: senza pause, RAZZI_AL_SECONDO razzi piccoli su tutta la mappa.
local function Pioggia()
	Sleep(ATTESA_INIZIALE)
	local perColpo = math.max(1, math.floor(RAZZI_AL_SECONDO / 4 + 0.5))
	while true do
		local lista = (missiliPiccoli.n > 0) and missiliPiccoli or missiliGrossi
		if lista.n > 0 then
			for i = 1, perColpo do
				local tx, tz = Bersaglio()
				Lancia(lista[math.random(lista.n)], tx, tz)
			end
		end
		Sleep(250)
	end
end

-- Salve grosse: ogni SALVA_OGNI un tipo di missile grosso a caso, tanti quanti ne servono
-- per POTENZA_SALVA (2-3 nucleari, oppure piu' missili tattici o EMP).
local function Salve()
	Sleep(ATTESA_INIZIALE)
	while true do
		if missiliGrossi.n > 0 then
			local weaponDefID = missiliGrossi[math.random(missiliGrossi.n)]
			local quanti = math.floor(POTENZA_SALVA / Danno(weaponDefID) + 0.5)
			quanti = math.max(SALVA_MIN, math.min(SALVA_MAX, quanti))
			for i = 1, quanti do
				local tx, tz = Bersaglio()
				Lancia(weaponDefID, tx, tz)
				Sleep(400)
			end
		end
		Sleep(SALVA_OGNI)
	end
end

-- Diagnostica: ogni 30 secondi scrive nel log di BAR (infolog.txt) quanti missili sono partiti.
local function Diario()
	while true do
		Sleep(30000)
		Spring.Echo("[gentleman] missili lanciati: " .. lanciati .. ", falliti: " .. falliti)
	end
end

-- Indistruttibile: ogni decimo di secondo riporta la vita al massimo e toglie paralisi e cattura.
local function Immortale()
	Spring.SetUnitArmored(unitID, true, 0)
	while true do
		local _, maxHealth = Spring.GetUnitHealth(unitID)
		if maxHealth then
			Spring.SetUnitHealth(unitID, { health = maxHealth, paralyze = 0, capture = 0 })
		end
		Sleep(100)
	end
end

-- Qualunque colpo ricevuto fa zero danni.
function script.HitByWeapon(x, z, weaponDefID, damage)
	return 0
end

function script.Create()
	local height = Spring.GetUnitHeight(unitID) or 40
	bounce = math.max(2, height * 0.06)
	missiliGrossi = RaccogliMissili(UNITA_GROSSE)
	missiliPiccoli = RaccogliMissili(UNITA_PICCOLE)
	Spring.Echo("[gentleman] pronto: " .. missiliPiccoli.n .. " tipi di razzi, " .. missiliGrossi.n .. " tipi di missili grossi, partenza " .. PARTENZA)
	StartThread(Immortale)
	StartThread(Dance)
	StartThread(Hunt)
	StartThread(Pioggia)
	StartThread(Salve)
	StartThread(Diario)
end

-- Arma 1 (difesa vicina): il missile parte da un braccio, alternando sinistro e destro.
function script.AimFromWeapon(num)
	return body
end

function script.QueryWeapon(num)
	return nextArm
end

function script.AimWeapon(num, heading, pitch)
	return true   -- nessuna torretta da orientare: il missile cerca da solo il bersaglio
end

function script.FireWeapon(num)
	-- contraccolpo sul braccio che ha sparato, poi tocca all'altro
	local arm = nextArm
	Turn(arm, y_axis, rad(-60), rad(900))
	nextArm = (nextArm == left_arm) and right_arm or left_arm
end

function script.Killed(recentDamage, maxHealth)
	return 1
end
