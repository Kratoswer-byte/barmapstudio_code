@echo off
rem Hosts a BAR game on a freshly generated map directly from this computer, without the BAR lobby servers.
rem Your friends join with join-game.bat, they need the same version of this generator.
rem You have to be reachable on UDP port 8452: forward that port on your router to this computer or use a VPN
rem like Tailscale / ZeroTier / Hamachi with everybody in the same network.
setlocal EnableDelayedExpansion

set "ROOT=%~dp0"
if not defined BAR_DATA (
    if exist "%ProgramFiles%\Beyond-All-Reason\data\maps" (
        set "BAR_DATA=%ProgramFiles%\Beyond-All-Reason\data"
    ) else if exist "%LOCALAPPDATA%\Programs\Beyond-All-Reason\data\maps" (
        set "BAR_DATA=%LOCALAPPDATA%\Programs\Beyond-All-Reason\data"
    ) else if exist "C:\Program Files\Beyond-All-Reason\data\maps" (
        set "BAR_DATA=C:\Program Files\Beyond-All-Reason\data"
    ) else (
        set "BAR_DATA=%LOCALAPPDATA%\Programs\Beyond-All-Reason\data"
    )
)
set "SCRIPT=%BAR_DATA%\_neroxis_host.txt"
set "LOG=%TEMP%\neroxis_host_log.txt"

if exist "%ROOT%app\launcher.jar" (
    set GENERATE="%ROOT%runtime\bin\java.exe" -Xmx6g -cp "%ROOT%app\launcher.jar" com.faforever.neroxis.generator.MapGeneratorCommand
) else if exist "%ROOT%runtime\bin\java.exe" (
    set GENERATE="%ROOT%runtime\bin\java.exe" -Xmx6g -m com.faforever.neroxis.generator/com.faforever.neroxis.generator.MapGeneratorCommand
) else (
    set "JAVA_EXE=java"
    if not exist "!JAVA_EXE!" set "JAVA_EXE=java"
    set GENERATE="!JAVA_EXE!" -Xmx6g -jar "%ROOT%generator\build\libs\NeroxisGen_snapshot.jar"
)
rem Trees that fit the biome come from the feature sets the BAR team certified for maps. They are not part of this
rem generator, they are downloaded once from the official repository (37 MB) and copied into the maps that use them.
set "FEATURES=%ROOT%mapfeatures"
if not exist "%FEATURES%\features" (
    echo Downloading the official BAR map features ^(trees, 37 MB, only once^)...
    "%SystemRoot%\System32\curl.exe" -L -s -o "%TEMP%\bar_mapfeatures.zip" https://github.com/beyond-all-reason/mapfeatures/archive/3b791639210dcc4f89875fce712bde8f460d38e1.zip
    "%SystemRoot%\System32\tar.exe" -xf "%TEMP%\bar_mapfeatures.zip" -C "%ROOT%."
    if exist "%ROOT%mapfeatures-3b791639210dcc4f89875fce712bde8f460d38e1" ren "%ROOT%mapfeatures-3b791639210dcc4f89875fce712bde8f460d38e1" mapfeatures
    del "%TEMP%\bar_mapfeatures.zip" 2>nul
)
set "FEATARG="
if exist "%FEATURES%\features" set FEATARG=--bar-features "%FEATURES%"

if not exist "%BAR_DATA%\maps" (
    echo BAR not found at %BAR_DATA%, set the BAR_DATA environment variable to the data folder of your BAR install.
    pause
    exit /b 1
)

echo.
echo  Host a game on a generated map
echo  ==============================
echo.
echo  Player names, yours first, separated by commas (letters, digits and _ only). Example: Rowan,Alex,Sam
set "PLAYERS="
set /p "PLAYERS=  Players: "
if not defined PLAYERS exit /b 1
set "PLAYERS=%PLAYERS: =%"
set "COUNT=0"
for %%P in (%PLAYERS%) do set /a COUNT+=1

echo.
echo  1) Players against each other (split over two teams, AIs fill empty slots)
echo  2) All players together against AIs
set "MODE=1"
set /p "MODE=  Choice [1]: "
set "COOP="
if "%MODE%"=="2" (
    set "COOP=--bar-coop"
    set /a "SPAWNS=COUNT*2"
) else (
    set /a "SPAWNS=(COUNT+1)/2*2"
)
if %SPAWNS% LSS 2 set "SPAWNS=2"
set "SIZE=384"
if %SPAWNS% GEQ 4 set "SIZE=512"
if %SPAWNS% GEQ 8 set "SIZE=640"
if %SPAWNS% GEQ 12 set "SIZE=768"

echo.
set "PASSWORD="
set /p "PASSWORD=  Password for joining (optional, letters and digits): "
set "PASSARG="
if defined PASSWORD set "PASSARG=--bar-password %PASSWORD%"

echo.
echo  Reclaim (rocks and wrecks that give metal)
echo    1) Normal    2) Low    3) Very low    4) None
set "RECLAIMCHOICE=1"
set /p "RECLAIMCHOICE=  Choice [1]: "
set "EXTRA="
if "%RECLAIMCHOICE%"=="2" set "EXTRA=--bar-reclaim 0.4"
if "%RECLAIMCHOICE%"=="3" set "EXTRA=--bar-reclaim 0.15"
if "%RECLAIMCHOICE%"=="4" set "EXTRA=--bar-reclaim 0"

set /a "PICK=%RANDOM% %% 7"
for %%S in (0:BASIC 1:VALLEY 2:LAND_BRIDGE 3:LITTLE_MOUNTAIN 4:MOUNTAIN_RANGE 5:MULTILEVEL 6:FRACTAL_LAND) do (
    for /f "tokens=1,2 delims=:" %%A in ("%%S") do if "%%A"=="%PICK%" set "STYLE=%%B"
)

echo.
echo  Generating a %SPAWNS% player map (%STYLE%)...
%GENERATE% --bar %FEATARG% --spawn-count %SPAWNS% --num-teams 2 --map-size %SIZE% --style %STYLE% --out-path "%BAR_DATA%\maps" --bar-host-script "%SCRIPT%" --bar-players %PLAYERS% %COOP% %PASSARG% %EXTRA% > "%LOG%" 2>&1
if errorlevel 1 (
    type "%LOG%"
    echo.
    echo  Generation failed, see above.
    pause
    exit /b 1
)
set "MAPNAME="
for /f "delims=" %%L in ('findstr /b "neroxis_map_generator_" "%LOG%"') do if not defined MAPNAME set "MAPNAME=%%L"

echo.
echo  ------------------------------------------------------------------------------
echo   Send this to your friends (each one uses their own player name from the list):
echo.
if not defined PASSWORD if defined EXTRA set "PASSWORD=-"
echo     join-game.bat %MAPNAME% YOUR_IP THEIR_NAME %PASSWORD% %EXTRA%
echo.
echo   YOUR_IP is your public IP (search "what is my ip") with UDP port 8452 forwarded
echo   to this computer, or your VPN address. Addresses of this computer:
ipconfig | findstr /c:"IPv4"
echo  ------------------------------------------------------------------------------
echo.
echo  The game starts now and waits for everybody. Allow BAR through the Windows firewall if it asks.
pause
if defined NEROXIS_NO_LAUNCH exit /b 0
for /d %%E in ("%BAR_DATA%\engine\*") do set "ENGINE=%%E"
start "" "!ENGINE!\spring.exe" --write-dir "%BAR_DATA%" --isolation "%SCRIPT%"
