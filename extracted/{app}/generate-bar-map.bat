@echo off
rem Neroxis Map Generator for Beyond All Reason
rem
rem Without arguments a menu asks for the game format, map size and style, generates the map into the BAR maps
rem folder and offers to start a skirmish against AIs on it.
rem With arguments they are passed to the generator:
rem   generate-bar-map.bat --spawn-count 4 --num-teams 2 --map-size 512 --style VALLEY --seed 42
rem   generate-bar-map.bat --help
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
if not defined BAR_MAPS set "BAR_MAPS=%BAR_DATA%\maps"
set "SCRIPT=%BAR_DATA%\_neroxis_skirmish.txt"

rem the release zip ships its own java runtime, the source tree uses the built jar
if exist "%ROOT%app\launcher.jar" (
    set GENERATE="%ROOT%runtime\bin\java.exe" -Xmx6g -cp "%ROOT%app\launcher.jar" com.faforever.neroxis.generator.MapGeneratorCommand
) else if exist "%ROOT%runtime\bin\java.exe" (
    set GENERATE="%ROOT%runtime\bin\java.exe" -Xmx6g -m com.faforever.neroxis.generator/com.faforever.neroxis.generator.MapGeneratorCommand
) else (
    set "JAVA_EXE=%USERPROFILE%\.gradle\jdks\eclipse_adoptium-25-amd64-windows.2\bin\java.exe"
    if not exist "!JAVA_EXE!" set "JAVA_EXE=java"
    if not exist "%ROOT%generator\build\libs\NeroxisGen_snapshot.jar" (
        echo The generator is not built yet, run build-bar-generator.bat first.
        pause
        exit /b 1
    )
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

if not exist "%BAR_MAPS%" (
    echo BAR maps folder not found: %BAR_MAPS%
    echo Set the BAR_DATA environment variable to the data folder of your BAR install.
    pause
    exit /b 1
)

if not "%~1"=="" (
    %GENERATE% --bar %FEATARG% --out-path "%BAR_MAPS%" --bar-skirmish-script "%SCRIPT%" %*
    exit /b !ERRORLEVEL!
)

echo.
echo  Neroxis Map Generator for Beyond All Reason
echo  ===========================================
echo.
echo  Game format
echo    1) 1v1        5) 5v5          9) 3 teams of 2
echo    2) 2v2        6) 6v6         10) 4 teams of 2
echo    3) 3v3        7) 8v8         11) FFA 4 players
echo    4) 4v4        8) 2v2v2v2     12) FFA 8 players
echo.
set "FORMAT=1"
set /p "FORMAT=  Choice [1]: "
set "SPAWNS=2" & set "TEAMS=2" & set "SIZE=384"
if "%FORMAT%"=="2" set "SPAWNS=4" & set "TEAMS=2" & set "SIZE=512"
if "%FORMAT%"=="3" set "SPAWNS=6" & set "TEAMS=2" & set "SIZE=512"
if "%FORMAT%"=="4" set "SPAWNS=8" & set "TEAMS=2" & set "SIZE=640"
if "%FORMAT%"=="5" set "SPAWNS=10" & set "TEAMS=2" & set "SIZE=640"
if "%FORMAT%"=="6" set "SPAWNS=12" & set "TEAMS=2" & set "SIZE=768"
if "%FORMAT%"=="7" set "SPAWNS=16" & set "TEAMS=2" & set "SIZE=768"
if "%FORMAT%"=="8" set "SPAWNS=8" & set "TEAMS=4" & set "SIZE=640"
if "%FORMAT%"=="9" set "SPAWNS=6" & set "TEAMS=3" & set "SIZE=640"
if "%FORMAT%"=="10" set "SPAWNS=8" & set "TEAMS=4" & set "SIZE=640"
if "%FORMAT%"=="11" set "SPAWNS=4" & set "TEAMS=4" & set "SIZE=512"
if "%FORMAT%"=="12" set "SPAWNS=8" & set "TEAMS=8" & set "SIZE=768"

set /a "SMU=SIZE/32"
echo.
echo  Map size (the recommended size for this format is %SMU%x%SMU%)
echo    1) 8x8    2) 12x12    3) 16x16    4) 20x20    5) 24x24    6) 32x32
echo.
set "SIZECHOICE="
set /p "SIZECHOICE=  Choice [recommended]: "
if "%SIZECHOICE%"=="1" set "SIZE=256"
if "%SIZECHOICE%"=="2" set "SIZE=384"
if "%SIZECHOICE%"=="3" set "SIZE=512"
if "%SIZECHOICE%"=="4" set "SIZE=640"
if "%SIZECHOICE%"=="5" set "SIZE=768"
if "%SIZECHOICE%"=="6" set "SIZE=1024"

echo.
echo  Map style
echo    1) Random land map       6) Little mountain    11) Big islands (navy)
echo    2) Anything              7) Mountain range     12) Small islands (navy)
echo    3) Basic                 8) Multilevel         13) Center lake
echo    4) Valley                9) Drop plateau       14) Rivers
echo    5) Land bridge          10) High reclaim       15) Flooded
echo.
set "STYLECHOICE=1"
set /p "STYLECHOICE=  Choice [1]: "
set "STYLE="
if "%STYLECHOICE%"=="1" (
    set /a "PICK=!RANDOM! %% 7"
    for %%S in (0:BASIC 1:VALLEY 2:LAND_BRIDGE 3:LITTLE_MOUNTAIN 4:MOUNTAIN_RANGE 5:MULTILEVEL 6:FRACTAL_LAND) do (
        for /f "tokens=1,2 delims=:" %%A in ("%%S") do if "%%A"=="!PICK!" set "STYLE=%%B"
    )
)
if "%STYLECHOICE%"=="3" set "STYLE=BASIC"
if "%STYLECHOICE%"=="4" set "STYLE=VALLEY"
if "%STYLECHOICE%"=="5" set "STYLE=LAND_BRIDGE"
if "%STYLECHOICE%"=="6" set "STYLE=LITTLE_MOUNTAIN"
if "%STYLECHOICE%"=="7" set "STYLE=MOUNTAIN_RANGE"
if "%STYLECHOICE%"=="8" set "STYLE=MULTILEVEL"
if "%STYLECHOICE%"=="9" set "STYLE=DROP_PLATEAU"
if "%STYLECHOICE%"=="10" set "STYLE=HIGH_RECLAIM"
if "%STYLECHOICE%"=="11" set "STYLE=BIG_ISLANDS"
if "%STYLECHOICE%"=="12" set "STYLE=SMALL_ISLANDS"
if "%STYLECHOICE%"=="13" set "STYLE=CENTER_LAKE"
if "%STYLECHOICE%"=="14" set "STYLE=RIVERS"
if "%STYLECHOICE%"=="15" set "STYLE=FLOODED"
set "STYLEARG="
if defined STYLE set "STYLEARG=--style %STYLE%"

echo.
echo  Metal spots
echo    1) BAR style, 2.0 next to the start and up to 3.5 for contested spots
echo    2) All spots 2.0
echo    3) Rich, 2.0 up to 5.0
echo.
set "METALCHOICE=1"
set /p "METALCHOICE=  Choice [1]: "
set "METALARG="
if "%METALCHOICE%"=="2" set "METALARG=--bar-metal-uniform"
if "%METALCHOICE%"=="3" set "METALARG=--bar-metal-max 5"

echo.
echo  Generating...
echo.
%GENERATE% --bar %FEATARG% --spawn-count %SPAWNS% --num-teams %TEAMS% --map-size %SIZE% %STYLEARG% %METALARG% --out-path "%BAR_MAPS%" --bar-skirmish-script "%SCRIPT%"
if errorlevel 1 (
    echo.
    echo  This combination is not supported by the chosen style, try another style or size.
    pause
    exit /b 1
)

echo.
echo  The map is in your BAR maps folder. Restart BAR to see it in the map list of skirmish and hosted games,
echo  other players need the same file in their maps folder to join.
echo.
set "PLAY=n"
set /p "PLAY=  Start a skirmish against AIs on it now? (y/n) [n]: "
if /i "%PLAY%"=="y" (
    for /d %%E in ("%BAR_DATA%\engine\*") do set "ENGINE=%%E"
    start "" "!ENGINE!\spring.exe" --write-dir "%BAR_DATA%" --isolation "%SCRIPT%"
)
