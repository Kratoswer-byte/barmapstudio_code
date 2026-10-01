; Created by "innounp" version 2.70.1
; Setup file: BARMapStudioSetup.exe
; Inno Setup Version: 6.7.0 (Unicode)

[Setup]
AppName=BAR Map Studio
AppVerName=BAR Map Studio 1.0.0
AppId={{7C1E2A9B-5D0E-4F5B-9B7A-0EC11A5E0001}
AppVersion=1.0.0
AppPublisher=BAR Map Studio
AppPublisherURL=https://www.beyondallreason.info
AppSupportURL=https://www.beyondallreason.info
AppUpdatesURL=https://www.beyondallreason.info/download
DefaultDirName={localappdata}\NeroxisLauncher
OutputBaseFilename=BARMapStudioSetup
UninstallDisplayIcon={app}\neroxis.ico
UninstallDisplayName=BAR Map Studio
Compression=lzma2
ArchitecturesAllowed=x64compatible
ArchitecturesInstallIn64BitMode=x64compatible
PrivilegesRequired=lowest
PrivilegesRequiredOverridesAllowed=dialog
DisableDirPage=yes
DisableProgramGroupPage=yes
ChangesAssociations=no
ShowLanguageDialog=no
WizardStyle=modern light
WizardImageFile=embedded\WizardImage0.png
WizardSmallImageFile=embedded\WizardSmallImage0.png

[Files]
Source: "{app}\BAR-INTEGRATION.md"; DestDir: "{app}"; Flags: ignoreversion 
Source: "{app}\bar-map-launcher.bat"; DestDir: "{app}"; Flags: ignoreversion 
Source: "{app}\generate-bar-map.bat"; DestDir: "{app}"; Flags: ignoreversion 
Source: "{app}\host-game.bat"; DestDir: "{app}"; Flags: ignoreversion 
Source: "{app}\join-game.bat"; DestDir: "{app}"; Flags: ignoreversion 
Source: "{app}\LICENSE.txt"; DestDir: "{app}"; Flags: ignoreversion 
Source: "{app}\neroxis.ico"; DestDir: "{app}"; Flags: ignoreversion 
Source: "{app}\neroxis.png"; DestDir: "{app}"; Flags: ignoreversion 
Source: "{app}\play-last-map.bat"; DestDir: "{app}"; Flags: ignoreversion 
Source: "{app}\README-FIRST.txt"; DestDir: "{app}"; Flags: ignoreversion 
Source: "{app}\README.md"; DestDir: "{app}"; Flags: ignoreversion 
Source: "{app}\app\launcher.jar"; DestDir: "{app}\app"; Flags: ignoreversion 
Source: "{app}\runtime\.plain-runtime"; DestDir: "{app}\runtime"; Flags: ignoreversion 
Source: "{app}\runtime\release"; DestDir: "{app}\runtime"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-console-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-console-l1-2-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-datetime-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-debug-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-errorhandling-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-fibers-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-fibers-l1-1-1.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-file-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-file-l1-2-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-file-l2-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-handle-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-heap-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-interlocked-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-kernel32-legacy-l1-1-1.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-libraryloader-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-localization-l1-2-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-memory-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-namedpipe-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-processenvironment-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-processthreads-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-processthreads-l1-1-1.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-profile-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-rtlsupport-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-string-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-synch-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-synch-l1-2-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-sysinfo-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-sysinfo-l1-2-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-timezone-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-core-util-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-crt-conio-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-crt-convert-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-crt-environment-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-crt-filesystem-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-crt-heap-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-crt-locale-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-crt-math-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-crt-multibyte-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-crt-private-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-crt-process-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-crt-runtime-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-crt-stdio-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-crt-string-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-crt-time-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\api-ms-win-crt-utility-l1-1-0.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\awt.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\fontmanager.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\freetype.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\java.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\java.exe"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\javajpeg.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\javaw.exe"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\jawt.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\jimage.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\jli.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\jsound.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\keytool.exe"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\lcms.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\management.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\mlib_image.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\msvcp140.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\net.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\nio.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\prefs.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\splashscreen.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\syslookup.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\ucrtbase.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\vcruntime140.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\vcruntime140_1.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\verify.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\zip.dll"; DestDir: "{app}\runtime\bin"; Flags: ignoreversion 
Source: "{app}\runtime\bin\server\jvm.dll"; DestDir: "{app}\runtime\bin\server"; Flags: ignoreversion 
Source: "{app}\runtime\conf\jaxp-strict.properties.template"; DestDir: "{app}\runtime\conf"; Flags: ignoreversion 
Source: "{app}\runtime\conf\jaxp.properties"; DestDir: "{app}\runtime\conf"; Flags: ignoreversion 
Source: "{app}\runtime\conf\logging.properties"; DestDir: "{app}\runtime\conf"; Flags: ignoreversion 
Source: "{app}\runtime\conf\net.properties"; DestDir: "{app}\runtime\conf"; Flags: ignoreversion 
Source: "{app}\runtime\conf\sound.properties"; DestDir: "{app}\runtime\conf"; Flags: ignoreversion 
Source: "{app}\runtime\conf\security\java.security"; DestDir: "{app}\runtime\conf\security"; Flags: ignoreversion 
Source: "{app}\runtime\conf\security\policy\README.txt"; DestDir: "{app}\runtime\conf\security\policy"; Flags: ignoreversion 
Source: "{app}\runtime\conf\security\policy\limited\default_local.policy"; DestDir: "{app}\runtime\conf\security\policy\limited"; Flags: ignoreversion 
Source: "{app}\runtime\conf\security\policy\limited\default_US_export.policy"; DestDir: "{app}\runtime\conf\security\policy\limited"; Flags: ignoreversion 
Source: "{app}\runtime\conf\security\policy\limited\exempt_local.policy"; DestDir: "{app}\runtime\conf\security\policy\limited"; Flags: ignoreversion 
Source: "{app}\runtime\conf\security\policy\unlimited\default_local.policy"; DestDir: "{app}\runtime\conf\security\policy\unlimited"; Flags: ignoreversion 
Source: "{app}\runtime\conf\security\policy\unlimited\default_US_export.policy"; DestDir: "{app}\runtime\conf\security\policy\unlimited"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.base\ADDITIONAL_LICENSE_INFO"; DestDir: "{app}\runtime\legal\java.base"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.base\aes.md"; DestDir: "{app}\runtime\legal\java.base"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.base\ASSEMBLY_EXCEPTION"; DestDir: "{app}\runtime\legal\java.base"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.base\c-libutl.md"; DestDir: "{app}\runtime\legal\java.base"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.base\cldr.md"; DestDir: "{app}\runtime\legal\java.base"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.base\icu.md"; DestDir: "{app}\runtime\legal\java.base"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.base\LICENSE"; DestDir: "{app}\runtime\legal\java.base"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.base\public_suffix.md"; DestDir: "{app}\runtime\legal\java.base"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.base\siphash.md"; DestDir: "{app}\runtime\legal\java.base"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.base\unicode.md"; DestDir: "{app}\runtime\legal\java.base"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.base\wepoll.md"; DestDir: "{app}\runtime\legal\java.base"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.base\zlib.md"; DestDir: "{app}\runtime\legal\java.base"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.datatransfer\ADDITIONAL_LICENSE_INFO"; DestDir: "{app}\runtime\legal\java.datatransfer"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.datatransfer\ASSEMBLY_EXCEPTION"; DestDir: "{app}\runtime\legal\java.datatransfer"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.datatransfer\LICENSE"; DestDir: "{app}\runtime\legal\java.datatransfer"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.desktop\ADDITIONAL_LICENSE_INFO"; DestDir: "{app}\runtime\legal\java.desktop"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.desktop\ASSEMBLY_EXCEPTION"; DestDir: "{app}\runtime\legal\java.desktop"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.desktop\colorimaging.md"; DestDir: "{app}\runtime\legal\java.desktop"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.desktop\freetype.md"; DestDir: "{app}\runtime\legal\java.desktop"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.desktop\giflib.md"; DestDir: "{app}\runtime\legal\java.desktop"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.desktop\harfbuzz.md"; DestDir: "{app}\runtime\legal\java.desktop"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.desktop\jpeg.md"; DestDir: "{app}\runtime\legal\java.desktop"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.desktop\lcms.md"; DestDir: "{app}\runtime\legal\java.desktop"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.desktop\libpng.md"; DestDir: "{app}\runtime\legal\java.desktop"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.desktop\LICENSE"; DestDir: "{app}\runtime\legal\java.desktop"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.desktop\mesa3d.md"; DestDir: "{app}\runtime\legal\java.desktop"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.logging\ADDITIONAL_LICENSE_INFO"; DestDir: "{app}\runtime\legal\java.logging"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.logging\ASSEMBLY_EXCEPTION"; DestDir: "{app}\runtime\legal\java.logging"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.logging\LICENSE"; DestDir: "{app}\runtime\legal\java.logging"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.management\ADDITIONAL_LICENSE_INFO"; DestDir: "{app}\runtime\legal\java.management"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.management\ASSEMBLY_EXCEPTION"; DestDir: "{app}\runtime\legal\java.management"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.management\LICENSE"; DestDir: "{app}\runtime\legal\java.management"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.naming\ADDITIONAL_LICENSE_INFO"; DestDir: "{app}\runtime\legal\java.naming"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.naming\ASSEMBLY_EXCEPTION"; DestDir: "{app}\runtime\legal\java.naming"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.naming\LICENSE"; DestDir: "{app}\runtime\legal\java.naming"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.net.http\ADDITIONAL_LICENSE_INFO"; DestDir: "{app}\runtime\legal\java.net.http"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.net.http\ASSEMBLY_EXCEPTION"; DestDir: "{app}\runtime\legal\java.net.http"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.net.http\LICENSE"; DestDir: "{app}\runtime\legal\java.net.http"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.prefs\ADDITIONAL_LICENSE_INFO"; DestDir: "{app}\runtime\legal\java.prefs"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.prefs\ASSEMBLY_EXCEPTION"; DestDir: "{app}\runtime\legal\java.prefs"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.prefs\LICENSE"; DestDir: "{app}\runtime\legal\java.prefs"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.security.sasl\ADDITIONAL_LICENSE_INFO"; DestDir: "{app}\runtime\legal\java.security.sasl"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.security.sasl\ASSEMBLY_EXCEPTION"; DestDir: "{app}\runtime\legal\java.security.sasl"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.security.sasl\LICENSE"; DestDir: "{app}\runtime\legal\java.security.sasl"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.sql\ADDITIONAL_LICENSE_INFO"; DestDir: "{app}\runtime\legal\java.sql"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.sql\ASSEMBLY_EXCEPTION"; DestDir: "{app}\runtime\legal\java.sql"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.sql\LICENSE"; DestDir: "{app}\runtime\legal\java.sql"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.transaction.xa\ADDITIONAL_LICENSE_INFO"; DestDir: "{app}\runtime\legal\java.transaction.xa"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.transaction.xa\ASSEMBLY_EXCEPTION"; DestDir: "{app}\runtime\legal\java.transaction.xa"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.transaction.xa\LICENSE"; DestDir: "{app}\runtime\legal\java.transaction.xa"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.xml\ADDITIONAL_LICENSE_INFO"; DestDir: "{app}\runtime\legal\java.xml"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.xml\ASSEMBLY_EXCEPTION"; DestDir: "{app}\runtime\legal\java.xml"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.xml\bcel.md"; DestDir: "{app}\runtime\legal\java.xml"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.xml\dom.md"; DestDir: "{app}\runtime\legal\java.xml"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.xml\jcup.md"; DestDir: "{app}\runtime\legal\java.xml"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.xml\LICENSE"; DestDir: "{app}\runtime\legal\java.xml"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.xml\schema10part1.md"; DestDir: "{app}\runtime\legal\java.xml"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.xml\schema10part2.md"; DestDir: "{app}\runtime\legal\java.xml"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.xml\xalan.md"; DestDir: "{app}\runtime\legal\java.xml"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.xml\xerces.md"; DestDir: "{app}\runtime\legal\java.xml"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.xml\xhtml10.md"; DestDir: "{app}\runtime\legal\java.xml"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.xml\xhtml10schema.md"; DestDir: "{app}\runtime\legal\java.xml"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.xml\xhtml11.md"; DestDir: "{app}\runtime\legal\java.xml"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.xml\xhtml11schema.md"; DestDir: "{app}\runtime\legal\java.xml"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.xml\xmlspec.md"; DestDir: "{app}\runtime\legal\java.xml"; Flags: ignoreversion 
Source: "{app}\runtime\legal\java.xml\xmlxsd.md"; DestDir: "{app}\runtime\legal\java.xml"; Flags: ignoreversion 
Source: "{app}\runtime\legal\jdk.unsupported\ADDITIONAL_LICENSE_INFO"; DestDir: "{app}\runtime\legal\jdk.unsupported"; Flags: ignoreversion 
Source: "{app}\runtime\legal\jdk.unsupported\ASSEMBLY_EXCEPTION"; DestDir: "{app}\runtime\legal\jdk.unsupported"; Flags: ignoreversion 
Source: "{app}\runtime\legal\jdk.unsupported\LICENSE"; DestDir: "{app}\runtime\legal\jdk.unsupported"; Flags: ignoreversion 
Source: "{app}\runtime\legal\jdk.zipfs\ADDITIONAL_LICENSE_INFO"; DestDir: "{app}\runtime\legal\jdk.zipfs"; Flags: ignoreversion 
Source: "{app}\runtime\legal\jdk.zipfs\ASSEMBLY_EXCEPTION"; DestDir: "{app}\runtime\legal\jdk.zipfs"; Flags: ignoreversion 
Source: "{app}\runtime\legal\jdk.zipfs\LICENSE"; DestDir: "{app}\runtime\legal\jdk.zipfs"; Flags: ignoreversion 
Source: "{app}\runtime\lib\classlist"; DestDir: "{app}\runtime\lib"; Flags: ignoreversion 
Source: "{app}\runtime\lib\fontconfig.bfc"; DestDir: "{app}\runtime\lib"; Flags: ignoreversion 
Source: "{app}\runtime\lib\fontconfig.properties.src"; DestDir: "{app}\runtime\lib"; Flags: ignoreversion 
Source: "{app}\runtime\lib\jawt.lib"; DestDir: "{app}\runtime\lib"; Flags: ignoreversion 
Source: "{app}\runtime\lib\jrt-fs.jar"; DestDir: "{app}\runtime\lib"; Flags: ignoreversion 
Source: "{app}\runtime\lib\jvm.cfg"; DestDir: "{app}\runtime\lib"; Flags: ignoreversion 
Source: "{app}\runtime\lib\jvm.lib"; DestDir: "{app}\runtime\lib"; Flags: ignoreversion 
Source: "{app}\runtime\lib\modules"; DestDir: "{app}\runtime\lib"; Flags: ignoreversion 
Source: "{app}\runtime\lib\psfont.properties.ja"; DestDir: "{app}\runtime\lib"; Flags: ignoreversion 
Source: "{app}\runtime\lib\psfontj2d.properties"; DestDir: "{app}\runtime\lib"; Flags: ignoreversion 
Source: "{app}\runtime\lib\tzdb.dat"; DestDir: "{app}\runtime\lib"; Flags: ignoreversion 
Source: "{app}\runtime\lib\tzmappings"; DestDir: "{app}\runtime\lib"; Flags: ignoreversion 
Source: "{app}\runtime\lib\security\blocked.certs"; DestDir: "{app}\runtime\lib\security"; Flags: ignoreversion 
Source: "{app}\runtime\lib\security\cacerts"; DestDir: "{app}\runtime\lib\security"; Flags: ignoreversion 
Source: "{app}\runtime\lib\security\public_suffix_list.dat"; DestDir: "{app}\runtime\lib\security"; Flags: ignoreversion 

[Registry]
Root: HKCU; Subkey: "Software\Microsoft\Windows\CurrentVersion\Uninstall\BARMapStudio"; Flags: uninsdeletekey deletekey 

[Run]
Filename: "{app}\runtime\bin\javaw.exe"; Parameters: """-splash:{app}\cache\splash.png"" -Xms48m -Xmx1g -XX:+UseG1GC -XX:G1PeriodicGCInterval=20000 -XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=25 -XX:TieredStopAtLevel=1 -XX:+AutoCreateSharedArchive ""-XX:SharedArchiveFile={app}\cache\barmapstudio.jsa"" --enable-native-access=ALL-UNNAMED ""-Dneroxis.home={app}"" -cp ""{app}\app\launcher.jar"" com.faforever.neroxis.generator.bar.BarLauncher"; WorkingDir: "{app}"; Description: "Start BAR Map Studio"; Flags: postinstall skipifsilent nowait

[Icons]
Name: "{userprograms}\BAR Map Studio"; Filename: "{app}\runtime\bin\javaw.exe"; Parameters: """-splash:{app}\cache\splash.png"" -Xms48m -Xmx1g -XX:+UseG1GC -XX:G1PeriodicGCInterval=20000 -XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=25 -XX:TieredStopAtLevel=1 -XX:+AutoCreateSharedArchive ""-XX:SharedArchiveFile={app}\cache\barmapstudio.jsa"" --enable-native-access=ALL-UNNAMED ""-Dneroxis.home={app}"" -cp ""{app}\app\launcher.jar"" com.faforever.neroxis.generator.bar.BarLauncher"; WorkingDir: "{app}"; IconFilename: "{app}\neroxis.ico"; Comment: "BAR Map Studio - map creator and lobby for Beyond All Reason"; 
Name: "{userdesktop}\BAR Map Studio"; Filename: "{app}\runtime\bin\javaw.exe"; Parameters: """-splash:{app}\cache\splash.png"" -Xms48m -Xmx1g -XX:+UseG1GC -XX:G1PeriodicGCInterval=20000 -XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=25 -XX:TieredStopAtLevel=1 -XX:+AutoCreateSharedArchive ""-XX:SharedArchiveFile={app}\cache\barmapstudio.jsa"" --enable-native-access=ALL-UNNAMED ""-Dneroxis.home={app}"" -cp ""{app}\app\launcher.jar"" com.faforever.neroxis.generator.bar.BarLauncher"; WorkingDir: "{app}"; IconFilename: "{app}\neroxis.ico"; Tasks: desktopicon; 

[Tasks]
Name: "desktopicon"; Description: "Create a desktop shortcut"; GroupDescription: "Shortcuts:"; 

[InstallDelete]
Type: files; Name: "{app}\BARMapStudioSetup.exe"; 
Type: files; Name: "{app}\NeroxisLauncherSetup.exe"; 

[UninstallDelete]
Type: filesandordirs; Name: "{app}\cache"; 
Type: filesandordirs; Name: "{app}\app"; 
Type: filesandordirs; Name: "{app}\runtime"; 
Type: files; Name: "{app}\lobby.log"; 
Type: files; Name: "{app}\update.log"; 

[CustomMessages]
english.NameAndVersion=%1 version %2
english.AdditionalIcons=Additional shortcuts:
english.CreateDesktopIcon=Create a &desktop shortcut
english.CreateQuickLaunchIcon=Create a &Quick Launch shortcut
english.ProgramOnTheWeb=%1 on the Web
english.UninstallProgram=Uninstall %1
english.LaunchProgram=Launch %1
english.AssocFileExtension=&Associate %1 with the %2 file extension
english.AssocingFileExtension=Associating %1 with the %2 file extension...
english.AutoStartProgramGroupDescription=Startup:
english.AutoStartProgram=Automatically start %1
english.AddonHostProgramNotFound=%1 could not be located in the folder you selected.%n%nDo you want to continue anyway?

[Languages]
; These files are stubs
; To achieve better results after recompilation, use the real language files
Name: "english"; MessagesFile: "embedded\english.isl"; 
