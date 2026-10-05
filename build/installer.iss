; Installer di BAR Map Studio (Inno Setup 6). Lo compila build.ps1, che passa:
;   /DAppVersion=1.1.0  /DDistDir=<cartella dist\BARMapStudio>  /DOutDir=<cartella di uscita>  /DIconFile=<BMS.ico>

#ifndef AppVersion
  #define AppVersion "beta-0.1"
#endif
#ifndef AppDisplay
  #define AppDisplay AppVersion
#endif

[Setup]
AppName=BAR Map Studio
AppVersion={#AppVersion}
AppVerName=BAR Map Studio {#AppDisplay}
AppId={{7C1E2A9B-5D0E-4F5B-9B7A-0EC11A5E0001}
AppPublisher=BAR Map Studio
DefaultDirName={autopf}\BARMapStudio
DefaultGroupName=BAR Map Studio
OutputDir={#OutDir}
OutputBaseFilename=BARMapStudioSetup-{#AppVersion}
SetupIconFile={#IconFile}
UninstallDisplayIcon={app}\BMS.ico
UninstallDisplayName=BAR Map Studio {#AppDisplay}
LicenseFile={#DistDir}\LICENSE.txt
Compression=lzma2
SolidCompression=yes
ArchitecturesAllowed=x64compatible
ArchitecturesInstallIn64BitMode=x64compatible
; Di norma si installa in "Program Files" (Windows chiede il permesso di amministratore). Li' la cartella del
; programma e' protetta e il programma tiene i suoi dati in %APPDATA%\BARMapStudio.
; Chi non ha i permessi puo' scegliere "solo per me": allora la cartella proposta e' quella dei programmi dell'utente.
PrivilegesRequired=admin
PrivilegesRequiredOverridesAllowed=dialog
DisableProgramGroupPage=yes
WizardStyle=modern

[Languages]
Name: "en"; MessagesFile: "compiler:Default.isl"
Name: "it"; MessagesFile: "compiler:Languages\Italian.isl"
Name: "fr"; MessagesFile: "compiler:Languages\French.isl"
Name: "de"; MessagesFile: "compiler:Languages\German.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"

[Files]
Source: "{#DistDir}\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{autoprograms}\BAR Map Studio"; Filename: "{app}\bar-map-launcher.bat"; WorkingDir: "{app}"; IconFilename: "{app}\BMS.ico"; Flags: runminimized
Name: "{autodesktop}\BAR Map Studio"; Filename: "{app}\bar-map-launcher.bat"; WorkingDir: "{app}"; IconFilename: "{app}\BMS.ico"; Flags: runminimized; Tasks: desktopicon

[Run]
Filename: "{app}\bar-map-launcher.bat"; Description: "{cm:LaunchProgram,BAR Map Studio}"; Flags: nowait postinstall skipifsilent runminimized runasoriginaluser

[UninstallDelete]
; solo cio' che il programma rigenera da solo; impostazioni e materiali dell'utente restano
Type: filesandordirs; Name: "{app}\cache"
