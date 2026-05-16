@echo off
setlocal

set "BASE_DIR=%~dp0"
set "PROPERTIES_FILE=%BASE_DIR%.mvn\wrapper\maven-wrapper.properties"
if "%MAVEN_USER_HOME%"=="" set "MAVEN_USER_HOME=%USERPROFILE%\.m2"
set "DISTS_DIR=%MAVEN_USER_HOME%\wrapper\dists"

if not exist "%PROPERTIES_FILE%" (
  echo Cannot find %PROPERTIES_FILE% 1>&2
  exit /b 1
)

for /f "tokens=1,* delims==" %%A in (%PROPERTIES_FILE%) do (
  if "%%A"=="distributionUrl" set "DISTRIBUTION_URL=%%B"
)

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$url='%DISTRIBUTION_URL%';" ^
  "$dists='%DISTS_DIR%';" ^
  "$archive=Split-Path $url -Leaf;" ^
  "$name=$archive -replace '-bin\.zip$','';" ^
  "$mavenDir=Join-Path $dists $name;" ^
  "$archivePath=Join-Path $dists $archive;" ^
  "if (!(Test-Path (Join-Path $mavenDir 'bin\mvn.cmd'))) {" ^
  "  New-Item -ItemType Directory -Force -Path $dists | Out-Null;" ^
  "  if (!(Test-Path $archivePath)) { Invoke-WebRequest -Uri $url -OutFile $archivePath; }" ^
  "  Expand-Archive -Path $archivePath -DestinationPath $dists -Force;" ^
  "}" ^
  "& (Join-Path $mavenDir 'bin\mvn.cmd') @args" %*

endlocal
