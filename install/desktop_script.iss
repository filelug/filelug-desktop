#define MyAppName "##PRODUCT_NAME##"
#define MyAppVersion "##VERSION_NO##"
#define MyCompany "##PRODUCT_BUILDER##"
#define MyCopyright "##PRODUCT_COPYRIGHT##"
#define MyDescription "##INSTALLER_DESCRIPTION##"
#define MyURL "##URL##"
#define MyJreSource "##JRE_SOURCE##"
#define MyArchitecturesAllowed "##ARCH_ALLOWED##"
#define MyArchitecturesInstallIn64BitMode "##ARCH_ALLOWED_IN_64##"

[Languages]

Name: en; MessagesFile: install\SetupMessage_en.isl
Name: zh_CN; MessagesFile: install\SetupMessage_zh_CN.isl
Name: zh_TW; MessagesFile: install\SetupMessage_zh_TW.isl

[InstallDelete]

Type: files; Name: {app}\*.exe
Type: files; Name: {app}\*.bat
Type: files; Name: {app}\*.dat
Type: files; Name: {app}\VERSION
Type: filesandordirs; Name: {app}\flib
Type: filesandordirs; Name: {app}\jre

[Setup]

AllowNoIcons=no
AllowRootDirectory=yes
AppCopyright={#MyCopyright}
AppName={#MyAppName}
AppPublisher={#MyCompany}
AppPublisherURL={#MyCompany}
AppSupportURL={#MyCompany}
AppVerName={#MyAppName} {#MyAppVersion}
AppVersion={#MyAppVersion}
ArchitecturesAllowed={#MyArchitecturesAllowed}
ArchitecturesInstallIn64BitMode={#MyArchitecturesInstallIn64BitMode}
Compression=lzma/ultra
DefaultDirName={sd}\Filelug
DefaultGroupName=Filelug
DisableStartupPrompt=yes
EnableDirDoesntExistWarning=yes
ExtraDiskSpaceRequired=100
InternalCompressLevel=ultra
LanguageDetectionMethod=locale
MinVersion=5.0.2195
;PrivilegesRequired=admin
PrivilegesRequired=lowest
ShowLanguageDialog=yes
ShowTasksTreeLines=yes
SolidCompression=yes
UninstallDisplayIcon={app}\My.exe
UsePreviousAppDir=no
VersionInfoDescription={#MyDescription}
VersionInfoProductTextVersion={#MyAppVersion}
VersionInfoVersion={#MyAppVersion}
WindowVisible=no
WizardImageBackColor=$800000
WizardImageFile=install\Filelug_Desktop_Installer_WizardImageFile.bmp
WizardSmallImageFile=install\Filelug_Desktop_Installer_WizardSmallImageFile.bmp

[Tasks]
Name: desktopicon; Description: {cm:CreateDesktopIcon}; GroupDescription: {cm:AdditionalIcons}; Flags: unchecked
Name: quicklaunchicon; Description: {cm:CreateQuickLaunchIcon}; GroupDescription: {cm:AdditionalIcons}; Flags: unchecked; OnlyBelowVersion: 0,6.1

[Files]

Source: compiler:WizModernSmallImage.bmp; Flags: dontcopy

;============= VCL Styles for Inno Setup==============
Source: install\vclstylesinno\VclStylesinno.dll; DestDir: {app}; Flags: dontcopy
Source: install\vclstylesinno\TurquoiseGray.vsf; DestDir: {app}; Flags: dontcopy

;=============== Desktop files ===============
Source: target\apprun.bat; DestDir: {app}
Source: target\flib\*.jar; DestDir: {app}\flib

;=============== JRE files ===============
Source: {#MyJreSource}\*.*; DestDir: {app}\jre; Flags:recursesubdirs createallsubdirs

;=============== VERSION files ===============
Source: target\VERSION; DestDir: {app}

[Icons]
Name: {group}\{cm:StartFilelugDesktop}; Filename: {app}\apprun.bat; WorkingDir: {app}
Name: {group}\{cm:UninstallFilelugDesktop}; Filename: {uninstallexe}
Name: {commondesktop}\{cm:StartFilelugDesktop}; Filename: {app}\apprun.bat; Tasks: desktopicon
Name: {userappdata}\Microsoft\Internet Explorer\Quick Launch\{cm:StartFilelugDesktop}; Filename: {app}\apprun.bat; Tasks: quicklaunchicon

[CustomMessages]

[Run]
Filename: {app}\apprun.bat; Description: {cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}; Flags: nowait postinstall skipifsilent

[Code]

// Import the LoadVCLStyle function from VclStylesInno.DLL
procedure LoadVCLStyle(VClStyleFile: String); external 'LoadVCLStyleW@files:VclStylesInno.dll stdcall';
// Import the UnLoadVCLStyles function from VclStylesInno.DLL
procedure UnLoadVCLStyles; external 'UnLoadVCLStyles@files:VclStylesInno.dll stdcall';

function InitializeSetup(): Boolean;
begin
	ExtractTemporaryFile('TurquoiseGray.vsf');
	LoadVCLStyle(ExpandConstant('{tmp}\TurquoiseGray.vsf'));
	Result := True;
end;
 
procedure DeinitializeSetup();
begin
	UnLoadVCLStyles;
end;
