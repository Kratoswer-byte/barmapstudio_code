@echo off
rem Starts a skirmish against the BARb AI with the start script data\_neroxis_skirmish.txt of the BAR install
set "BAR=%LOCALAPPDATA%\Programs\Beyond-All-Reason\data"
for /d %%E in ("%BAR%\engine\*") do set "ENGINE=%%E"
start "" "%ENGINE%\spring.exe" --write-dir "%BAR%" --isolation "%BAR%\_neroxis_skirmish.txt"
