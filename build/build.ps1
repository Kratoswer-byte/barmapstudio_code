# Costruisce il pacchetto di BAR Map Studio da distribuire.
#
#   powershell -ExecutionPolicy Bypass -File build\build.ps1 [-Version 1.1.0] [-NoObfuscate] [-FetchTools]
#
# Produce in dist\ :
#   BARMapStudio\                    cartella del programma (portabile, Windows)
#   BARMapStudio-<versione>-win.zip  la stessa cartella compressa
#   BARMapStudio-<versione>-unix.tar.gz   Linux e macOS, senza Java incluso (NON PROVATO)
#   BARMapStudioSetup-<versione>.exe      installer, se Inno Setup e' installato
#   mapping-<versione>.txt           corrispondenza dei nomi offuscati: serve a leggere gli errori, NON va distribuita
#
# -NoObfuscate  salta l'offuscamento (pacchetto di prova)
# -FetchTools   scarica ProGuard con Maven in build\tools\proguard se manca

param(
    [string]$Version = "1.1.0",
    [switch]$NoObfuscate,
    [switch]$FetchTools
)

$ErrorActionPreference = "Stop"
Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem

$root = Split-Path -Parent $PSScriptRoot
$app = Join-Path $root "extracted\{app}"
$distRoot = Join-Path $root "dist"
$dist = Join-Path $distRoot "BARMapStudio"
$unix = Join-Path $distRoot "BARMapStudio-unix"
$work = Join-Path $distRoot "work"
$proguardDir = Join-Path $PSScriptRoot "tools\proguard"

function Step($text) { Write-Host "== $text" -ForegroundColor Cyan }

function Find-Jdk {
    $candidates = @($env:JAVA_HOME, (Join-Path $env:USERPROFILE "scoop\apps\temurin21-jdk\current"))
    foreach ($candidate in $candidates) {
        if ($candidate -and (Test-Path -LiteralPath (Join-Path $candidate "bin\java.exe")) -and (Test-Path -LiteralPath (Join-Path $candidate "jmods"))) {
            return $candidate
        }
    }
    throw "JDK non trovato: imposta JAVA_HOME su un JDK (serve la cartella jmods)."
}

# Copia in un nuovo archivio le voci scelte da $keep (riceve il nome della voce, torna vero o falso).
function Copy-Entries($sourceZip, $targetArchive, [scriptblock]$keep) {
    $source = [System.IO.Compression.ZipFile]::OpenRead($sourceZip)
    try {
        foreach ($entry in $source.Entries) {
            if (-not (& $keep $entry.FullName)) { continue }
            $copy = $targetArchive.CreateEntry($entry.FullName, [System.IO.Compression.CompressionLevel]::Optimal)
            $copy.LastWriteTime = $entry.LastWriteTime
            if ($entry.FullName.EndsWith("/")) { continue }
            $in = $entry.Open()
            $out = $copy.Open()
            try { $in.CopyTo($out) } finally { $out.Dispose(); $in.Dispose() }
        }
    } finally { $source.Dispose() }
}

function New-Archive($path) {
    if (Test-Path -LiteralPath $path) { Remove-Item -LiteralPath $path -Force }
    return [System.IO.Compression.ZipFile]::Open($path, [System.IO.Compression.ZipArchiveMode]::Create)
}

$studio = "com/faforever/neroxis/"

Step "Pulizia"
foreach ($folder in @($dist, $unix, $work)) {
    if (Test-Path -LiteralPath $folder) { Remove-Item -LiteralPath $folder -Recurse -Force }
    New-Item -ItemType Directory -Force -Path $folder | Out-Null
}
$sourceJar = Join-Path $app "app\launcher.jar"
if (-not (Test-Path -LiteralPath $sourceJar)) { throw "Manca $sourceJar" }
$finalJar = Join-Path $work "launcher.jar"

if ($NoObfuscate) {
    Step "Offuscamento saltato (-NoObfuscate)"
    Copy-Item -LiteralPath $sourceJar -Destination $finalJar
} else {
    $jdk = Find-Jdk
    if (-not (Get-ChildItem -Path $proguardDir -Filter *.jar -ErrorAction SilentlyContinue)) {
        if (-not $FetchTools) {
            throw "ProGuard non trovato in $proguardDir. Rilancia con -FetchTools per scaricarlo con Maven, oppure con -NoObfuscate."
        }
        Step "Scarico ProGuard con Maven"
        & mvn -q -f (Join-Path $PSScriptRoot "tools-pom.xml") dependency:copy-dependencies "-DoutputDirectory=$proguardDir"
        if ($LASTEXITCODE -ne 0) { throw "Maven non e' riuscito a scaricare ProGuard." }
    }

    Step "Separo lo Studio dalle librerie"
    $inJar = Join-Path $work "studio-in.jar"
    $outJar = Join-Path $work "studio-out.jar"
    $restJar = Join-Path $work "libraries.jar"
    $archive = New-Archive $inJar
    try { Copy-Entries $sourceJar $archive { param($name) $name.StartsWith($studio) } } finally { $archive.Dispose() }
    $archive = New-Archive $restJar
    try { Copy-Entries $sourceJar $archive { param($name) -not $name.StartsWith($studio) } } finally { $archive.Dispose() }

    Step "Offuscamento (ProGuard)"
    $mapping = Join-Path $distRoot "mapping-$Version.txt"
    $arguments = @(
        "-cp", (Join-Path $proguardDir "*"), "proguard.ProGuard",
        "-include", ("'" + (Join-Path $PSScriptRoot "proguard.pro") + "'"),
        "-injars", "'$inJar'",
        "-outjars", "'$outJar'",
        "-libraryjars", "'$restJar'(!META-INF/versions/**)",
        "-printmapping", "'$mapping'"
    )
    foreach ($module in @("java.base", "java.desktop", "java.prefs", "java.net.http", "java.logging", "java.xml", "java.sql", "java.datatransfer", "java.management", "java.naming")) {
        $arguments += "-libraryjars"
        $arguments += "'" + (Join-Path $jdk "jmods\$module.jmod") + "'(!**.jar,!module-info.class)"
    }
    & (Join-Path $jdk "bin\java.exe") @arguments
    if ($LASTEXITCODE -ne 0) { throw "ProGuard e' terminato con errore $LASTEXITCODE." }

    Step "Ricompongo launcher.jar"
    $archive = New-Archive $finalJar
    try {
        Copy-Entries $restJar $archive { param($name) $true }
        Copy-Entries $outJar $archive { param($name) $true }
    } finally { $archive.Dispose() }
}

Step "Copio i file del programma"
New-Item -ItemType Directory -Force -Path (Join-Path $dist "app") | Out-Null
Copy-Item -LiteralPath $finalJar -Destination (Join-Path $dist "app\launcher.jar")
Copy-Item -LiteralPath (Join-Path $app "runtime") -Destination (Join-Path $dist "runtime") -Recurse
Copy-Item -LiteralPath (Join-Path $app "default_objects") -Destination (Join-Path $dist "default_objects") -Recurse
foreach ($file in @("bar-map-launcher.bat", "README-FIRST.txt", "LICENSE.txt", "donation.properties", "donation_qrcode.png")) {
    Copy-Item -LiteralPath (Join-Path $app $file) -Destination (Join-Path $dist $file)
}
Copy-Item -LiteralPath (Join-Path $root "ico\BMS.ico") -Destination (Join-Path $dist "BMS.ico")
# il vecchio launcher chiedeva al primo avvio se creare un collegamento: ora lo fa l'installer
New-Item -ItemType File -Force -Path (Join-Path $dist ".shortcut-asked") | Out-Null

Step "Pacchetto Windows (.zip)"
$zip = Join-Path $distRoot "BARMapStudio-$Version-win.zip"
if (Test-Path -LiteralPath $zip) { Remove-Item -LiteralPath $zip -Force }
[System.IO.Compression.ZipFile]::CreateFromDirectory($dist, $zip, [System.IO.Compression.CompressionLevel]::Optimal, $true)

Step "Pacchetto Linux e macOS (.tar.gz, non provato)"
New-Item -ItemType Directory -Force -Path (Join-Path $unix "app") | Out-Null
Copy-Item -LiteralPath $finalJar -Destination (Join-Path $unix "app\launcher.jar")
Copy-Item -LiteralPath (Join-Path $app "default_objects") -Destination (Join-Path $unix "default_objects") -Recurse
foreach ($file in @("README-FIRST.txt", "LICENSE.txt", "donation.properties", "donation_qrcode.png")) {
    Copy-Item -LiteralPath (Join-Path $app $file) -Destination (Join-Path $unix $file)
}
Copy-Item -LiteralPath (Join-Path $PSScriptRoot "bar-map-studio.sh") -Destination (Join-Path $unix "bar-map-studio.sh")
Copy-Item -LiteralPath (Join-Path $PSScriptRoot "README-UNIX.txt") -Destination (Join-Path $unix "README-UNIX.txt")
$tarball = Join-Path $distRoot "BARMapStudio-$Version-unix.tar.gz"
if (Test-Path -LiteralPath $tarball) { Remove-Item -LiteralPath $tarball -Force }
& (Join-Path $env:SystemRoot "System32\tar.exe") -czf $tarball -C $distRoot "BARMapStudio-unix"
if ($LASTEXITCODE -ne 0) { throw "tar non e' riuscito a creare $tarball" }

Step "Installer"
$iscc = $null
$isccCandidates = @(
    (Join-Path $env:USERPROFILE "scoop\apps\inno-setup\current\ISCC.exe"),
    (Join-Path ${env:ProgramFiles(x86)} "Inno Setup 6\ISCC.exe"),
    (Join-Path $env:ProgramFiles "Inno Setup 6\ISCC.exe"),
    (Join-Path $env:LOCALAPPDATA "Programs\Inno Setup 6\ISCC.exe")
)
foreach ($candidate in $isccCandidates) {
    if ($candidate -and (Test-Path -LiteralPath $candidate)) { $iscc = $candidate; break }
}
if ($iscc) {
    & $iscc "/Q" "/DAppVersion=$Version" "/DDistDir=$dist" "/DOutDir=$distRoot" ("/DIconFile=" + (Join-Path $root "ico\BMS.ico")) (Join-Path $PSScriptRoot "installer.iss")
    if ($LASTEXITCODE -ne 0) { throw "Inno Setup e' terminato con errore $LASTEXITCODE." }
} else {
    Write-Host "Inno Setup non trovato: installer saltato (il pacchetto .zip e' pronto)." -ForegroundColor Yellow
}

Remove-Item -LiteralPath $work -Recurse -Force
Step "Fatto"
Get-ChildItem -LiteralPath $distRoot -File | ForEach-Object { "{0,-44} {1,8:N1} MB" -f $_.Name, ($_.Length / 1MB) }
