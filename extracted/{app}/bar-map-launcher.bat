@echo off
rem Starts BAR Map Studio with the Java runtime shipped in the "runtime" folder.
setlocal
set "ROOT=%~dp0"
rem Where the program writes its settings, cache and logs. Normally next to the program; when that folder
rem is read-only (for example an install under Program Files) the user data folder is used instead and the
rem real program folder is passed on, so the bundled files are still found.
set "HOME_DIR=%ROOT%."
set "APPARG="
( >"%ROOT%.write-test" echo.) 2>nul
if exist "%ROOT%.write-test" (
    del "%ROOT%.write-test" >nul 2>&1
) else (
    set "HOME_DIR=%APPDATA%\BARMapStudio"
    if not exist "%APPDATA%\BARMapStudio" mkdir "%APPDATA%\BARMapStudio"
    set APPARG="-Dbarstudio.app=%ROOT%."
)
if not exist "%HOME_DIR%\cache" mkdir "%HOME_DIR%\cache" >nul 2>&1
if not exist "%ROOT%app\launcher.jar" goto :broken
if not exist "%ROOT%runtime\bin\javaw.exe" goto :broken
start "" "%ROOT%runtime\bin\javaw.exe" "-splash:%HOME_DIR%\cache\splash.png" -Xms48m -Xmx1g -XX:+UseG1GC -XX:G1PeriodicGCInterval=20000 -XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=25 -XX:TieredStopAtLevel=1 -XX:+AutoCreateSharedArchive "-XX:SharedArchiveFile=%HOME_DIR%\cache\barmap.jsa" --enable-native-access=ALL-UNNAMED "-Dneroxis.home=%HOME_DIR%" %APPARG% -cp "%ROOT%app\launcher.jar" com.faforever.neroxis.generator.bar.BarMapStudio
exit /b 0

:broken
echo BAR Map Studio is incomplete: "app\launcher.jar" or the "runtime" folder is missing.
echo Install it again.
pause
exit /b 1
