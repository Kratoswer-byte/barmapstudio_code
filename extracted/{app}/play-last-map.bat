@echo off
rem Starts a skirmish against the BARb AI with the start script data\_neroxis_skirmish.txt of the BAR install
if not defined BAR (
    if exist "%ProgramFiles%\Beyond-All-Reason\data\maps" (
        set "BAR=%ProgramFiles%\Beyond-All-Reason\data"
    ) else if exist "%LOCALAPPDATA%\Programs\Beyond-All-Reason\data\maps" (
        set "BAR=%LOCALAPPDATA%\Programs\Beyond-All-Reason\data"
    ) else if exist "C:\Program Files\Beyond-All-Reason\data\maps" (
        set "BAR=C:\Program Files\Beyond-All-Reason\data"
    ) else (
        set "BAR=%LOCALAPPDATA%\Programs\Beyond-All-Reason\data"
    )
)
for /d %%E in ("%BAR%\engine\*") do set "ENGINE=%%E"
start "" "%ENGINE%\spring.exe" --write-dir "%BAR%" --isolation "%BAR%\_neroxis_skirmish.txt"
