@echo off
rem Opens the map generator window: pick the game format, size and style, generate, preview and play.
setlocal
set "ROOT=%~dp0"
rem updatable package: plain Java runtime + app\launcher.jar. A downloaded update waits as launcher-new.jar,
rem the old launcher may need a moment to exit before the file can be replaced.
if exist "%ROOT%app\launcher-new.jar" (
    for /l %%i in (1,1,10) do (
        if exist "%ROOT%app\launcher-new.jar" (
            move /y "%ROOT%app\launcher-new.jar" "%ROOT%app\launcher.jar" >nul 2>&1
            if exist "%ROOT%app\launcher-new.jar" timeout /t 1 /nobreak >nul
        )
    )
)
rem Start local offline mock server for lobby (8200) and map service (8090) if not running
if exist "%ROOT%app\mock-server.jar" (
    powershell -NoProfile -Command "if (-not (Get-NetTCPConnection -LocalPort 8200 -State Listen -ErrorAction SilentlyContinue)) { Start-Process -WindowStyle Hidden -FilePath '%ROOT%runtime\bin\javaw.exe' -ArgumentList '-jar \"%ROOT%app\mock-server.jar\"' }"
)
if exist "%ROOT%app\launcher.jar" if exist "%ROOT%runtime\bin\javaw.exe" (
    start "" "%ROOT%runtime\bin\javaw.exe" "-splash:%ROOT%cache\splash.png" -Xms48m -Xmx1g -XX:+UseG1GC -XX:G1PeriodicGCInterval=20000 -XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=25 -XX:TieredStopAtLevel=1 -XX:+AutoCreateSharedArchive "-XX:SharedArchiveFile=%ROOT%cache\eclipse.jsa" --enable-native-access=ALL-UNNAMED "-Dneroxis.home=%ROOT%." -cp "%ROOT%app\launcher.jar" com.faforever.neroxis.generator.bar.BarLauncher
    exit /b 0
)
if exist "%ROOT%runtime\bin\javaw.exe" (
    start "" "%ROOT%runtime\bin\javaw.exe" "-splash:%ROOT%cache\splash.png" -Xms48m -Xmx1g -XX:+UseG1GC -XX:G1PeriodicGCInterval=20000 -XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=25 -XX:TieredStopAtLevel=1 -XX:+AutoCreateSharedArchive "-XX:SharedArchiveFile=%ROOT%cache\eclipse.jsa" --enable-native-access=com.formdev.flatlaf "-Dneroxis.home=%ROOT%." -m com.faforever.neroxis.generator/com.faforever.neroxis.generator.bar.BarLauncher
    exit /b 0
)
set "JAVAW=%USERPROFILE%\.gradle\jdks\eclipse_adoptium-25-amd64-windows.2\bin\javaw.exe"
if not exist "%JAVAW%" set "JAVAW=javaw"
if not exist "%ROOT%generator\build\libs\NeroxisGen_snapshot.jar" (
    echo The generator is not built yet, run build-bar-generator.bat first.
    pause
    exit /b 1
)
start "" "%JAVAW%" "-splash:%ROOT%cache\splash.png" -Xms48m -Xmx1g -XX:+UseG1GC -XX:G1PeriodicGCInterval=20000 -XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=25 -XX:TieredStopAtLevel=1 -XX:+AutoCreateSharedArchive "-XX:SharedArchiveFile=%ROOT%cache\eclipse.jsa" --enable-native-access=ALL-UNNAMED "-Dneroxis.home=%ROOT%." -cp "%ROOT%generator\build\libs\NeroxisGen_snapshot.jar" com.faforever.neroxis.generator.bar.BarLauncher
