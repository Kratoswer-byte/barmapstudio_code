-- BAR Map Studio: keeps the colour picked in the room on the player's own team after they resign

function widget:GetInfo()
    return {
        name = "BAR Map Studio: Own colour",
        desc = "Keeps the colour picked in the BAR Map Studio room on your team after you resign",
        author = "BAR Map Studio",
        date = "2026",
        license = "GNU GPL, v2 or later",
        layer = 0,
        enabled = true,
    }
end

local wanted
local myTeam
local sinceCheck = 0

function widget:Initialize()
    if Spring.GetConfigInt("StudioOwnColour", 0) ~= 1 or Spring.GetConfigInt("SimpleTeamColors", 0) ~= 1 then
        widgetHandler:RemoveWidget(self)
        return
    end
    wanted = {
        Spring.GetConfigInt("SimpleTeamColorsPlayerR", 0) / 255,
        Spring.GetConfigInt("SimpleTeamColorsPlayerG", 77) / 255,
        Spring.GetConfigInt("SimpleTeamColorsPlayerB", 255) / 255,
    }
    if not Spring.GetSpectatingState() then
        myTeam = Spring.GetMyTeamID()
    end
end

function widget:Update(dt)
    sinceCheck = sinceCheck + dt
    if sinceCheck < 0.5 then
        return
    end
    sinceCheck = 0
    if not Spring.GetSpectatingState() then
        myTeam = Spring.GetMyTeamID()
        return
    end
    if not myTeam then
        return
    end
    local r, g, b = Spring.GetTeamColor(myTeam)
    if r and (math.abs(r - wanted[1]) > 0.01 or math.abs(g - wanted[2]) > 0.01 or math.abs(b - wanted[3]) > 0.01) then
        Spring.SetTeamColor(myTeam, wanted[1], wanted[2], wanted[3])
    end
end
