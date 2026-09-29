@echo off
rem Joins a BAR game that a friend hosts with host-game.bat.
rem   join-game.bat <map name> <host ip> <your player name> [password or -] [generator options of the host]
rem Without arguments it asks for them. The map is generated on this computer from its name, it is identical to the
rem map of the host as long as both use the same version of this generator.
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
set "SCRIPT=%BAR_DATA%\_neroxis_join.txt"

if exist "%ROOT%runtime\bin\java.exe" (
    set GENERATE="%ROOT%runtime\bin\java.exe" -Xmx6g -m com.faforever.neroxis.generator/com.faforever.neroxis.generator.MapGeneratorCommand
) else (
    set "JAVA_EXE=%USERPROFILE%\.gradle\jdks\eclipse_adoptium-25-amd64-windows.2\bin\java.exe"
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

set "MAPNAME=%~1"
set "HOSTIP=%~2"
set "PLAYER=%~3"
set "PASSWORD=%~4"
if "%PASSWORD%"=="-" set "PASSWORD="
set "EXTRA=%5 %6 %7 %8 %9"
if not defined MAPNAME set /p "MAPNAME=Map name (neroxis_map_generator_...): "
if not defined HOSTIP set /p "HOSTIP=IP address of the host: "
if not defined PLAYER set /p "PLAYER=Your player name (as the host entered it): "
if "%~1"=="" if not defined PASSWORD set /p "PASSWORD=Password (empty if none): "

echo.
echo Generating the map...
%GENERATE% --bar %FEATARG% --map-name %MAPNAME% %EXTRA% --out-path "%BAR_DATA%\maps" > "%TEMP%\neroxis_join_log.txt" 2>&1
if errorlevel 1 (
    type "%TEMP%\neroxis_join_log.txt"
    echo.
    echo The map could not be generated. Check the map name and that you use the same generator version as the host.
    pause
    exit /b 1
)

> "%SCRIPT%" (
    echo [game]
    echo {
    echo 	hostip=%HOSTIP%;
    echo 	hostport=8452;
    echo 	ishost=0;
    echo 	myplayername=%PLAYER%;
    if defined PASSWORD echo 	mypasswd=%PASSWORD%;
    echo }
)

echo Connecting to %HOSTIP% as %PLAYER%...
if defined NEROXIS_NO_LAUNCH exit /b 0
for /d %%E in ("%BAR_DATA%\engine\*") do set "ENGINE=%%E"
start "" "!ENGINE!\spring.exe" --write-dir "%BAR_DATA%" --isolation "%SCRIPT%"
