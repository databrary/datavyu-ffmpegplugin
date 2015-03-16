;This file will be executed next to the application bundle image
;I.e. current directory will contain folder datavyu-1.3 with application files
[Setup]
AppId={{org.datavyu}}
AppName=datavyu-1.3
AppVersion=1.0
AppVerName=datavyu-1.3
AppPublisher=Datavyu Foundation
AppComments=datavyu-1.3
AppCopyright=Copyright (C) 2015
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
DefaultDirName={code:DefDirRoot}\datavyu-1.3
DisableStartupPrompt=Yes
DisableDirPage=No
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=Yes
DisableWelcomePage=Yes
DefaultGroupName=Datavyu Foundation
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename=datavyu-1.3-1.0
Compression=lzma
SolidCompression=yes
PrivilegesRequired=lowest
SetupIconFile=datavyu-1.3\datavyu-1.3.ico
UninstallDisplayIcon={app}\datavyu-1.3.ico
UninstallDisplayName=datavyu-1.3
WizardImageStretch=No
WizardSmallImageFile=datavyu-1.3-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=
UsePreviousAppDir=No

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "datavyu-1.3\datavyu-1.3.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "datavyu-1.3\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\datavyu-1.3"; Filename: "{app}\datavyu-1.3.exe"; IconFilename: "{app}\datavyu-1.3.ico"; Check: returnTrue()
Name: "{group}\Uninstall Datavyu"; Filename: "{uninstallexe}"
Name: "{userdesktop}\Datavyu-1.3"; Filename: "{app}\datavyu-1.3.exe";  IconFilename: "{app}\datavyu-1.3.ico"; Check: returnTrue()

[Run]
Filename: "{app}\datavyu-1.3.exe"; Description: "{cm:LaunchProgram,datavyu-1.3}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\datavyu-1.3.exe"; Parameters: "-install -svcName ""datavyu-1.3"" -svcDesc ""datavyu-1.3"" -mainExe ""datavyu-1.3.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\datavyu-1.3.exe "; Parameters: "-uninstall -svcName datavyu-1.3 -stopOnUninstall"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support? 
  Result := True;
end;

function IsRegularUser(): Boolean;
begin
Result := not (IsAdminLoggedOn or IsPowerUserLoggedOn);
end;

function DefDirRoot(Param: String): String;
begin
if IsRegularUser then
Result := ExpandConstant('{localappdata}')
else
Result := ExpandConstant('{pf}')
end;
