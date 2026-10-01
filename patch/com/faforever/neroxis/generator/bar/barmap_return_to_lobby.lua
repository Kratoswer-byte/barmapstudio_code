-- BAR Map Studio: once the game is over, a "Return to lobby" button at the bottom of the screen
-- closes the game and returns to BAR Map Studio.

function widget:GetInfo()
    return {
        name = "BAR Map Studio: Return to lobby",
        desc = "After the game: a button that closes the game and goes back to the room in BAR Map Studio",
        author = "BAR Map Studio",
        date = "2026",
        license = "GNU GPL, v2 or later",
        layer = -100000,
        enabled = true,
    }
end

local shown = false
local button = { x1 = 0, y1 = 0, x2 = 0, y2 = 0 }
local textSize = 20

local function place()
    local vsx, vsy = Spring.GetViewGeometry()
    local scale = vsy / 1080
    local width, height = 300 * scale, 54 * scale
    local centreY = vsy * 0.155
    button.x1 = vsx / 2 - width / 2
    button.x2 = vsx / 2 + width / 2
    button.y1 = centreY - height / 2
    button.y2 = centreY + height / 2
    textSize = 21 * scale
end

local function inside(x, y)
    return x >= button.x1 and x <= button.x2 and y >= button.y1 and y <= button.y2
end

function widget:Initialize()
    place()
    if Spring.IsGameOver and Spring.IsGameOver() then
        shown = true
    end
end

function widget:ViewResize()
    place()
end

function widget:GameOver()
    shown = true
end

function widget:DrawScreen()
    if not shown then
        return
    end
    local mx, my = Spring.GetMouseState()
    local hover = inside(mx, my)
    local border = 2
    -- neon cyan accent of BAR Map Studio
    gl.Color(0.00, 0.90, 1.00, hover and 1 or 0.85)
    gl.Rect(button.x1 - border, button.y1 - border, button.x2 + border, button.y2 + border)
    if hover then
        gl.Color(0.00, 0.90, 1.00, 1)
    else
        gl.Color(0.04, 0.05, 0.08, 0.94)
    end
    gl.Rect(button.x1, button.y1, button.x2, button.y2)
    if hover then
        gl.Color(0.04, 0.05, 0.08, 1)
    else
        gl.Color(1, 1, 1, 1)
    end
    gl.Text("Return to lobby", (button.x1 + button.x2) / 2, (button.y1 + button.y2) / 2, textSize, "cv")
    gl.Color(1, 1, 1, 1)
end

function widget:IsAbove(x, y)
    return shown and inside(x, y)
end

function widget:GetTooltip(x, y)
    if shown and inside(x, y) then
        return "Closes the game and goes back to BAR Map Studio"
    end
end

function widget:MousePress(x, y, mouseButton)
    if shown and mouseButton == 1 and inside(x, y) then
        Spring.SendCommands("quitforce")
        return true
    end
    return false
end
