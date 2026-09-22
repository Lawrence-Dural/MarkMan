@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup script, version 3.3.4
@REM ----------------------------------------------------------------------------
@echo off

set BASE_DIR=%~dp0
set WRAPPER_JAR=%BASE_DIR%.mvn\wrapper\maven-wrapper.jar
set WRAPPER_PROPERTIES=%BASE_DIR%.mvn\wrapper\maven-wrapper.properties
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

if not exist "%WRAPPER_JAR%" (
  echo Downloading Maven Wrapper...
  for /f "usebackq tokens=1,* delims==" %%A in ("%WRAPPER_PROPERTIES%") do (
    if "%%A"=="wrapperUrl" set WRAPPER_URL=%%B
  )
  powershell -Command "Invoke-WebRequest -Uri '%WRAPPER_URL%' -OutFile '%WRAPPER_JAR%'"
)

if "%JAVA_HOME%"=="" (
  set JAVA_EXE=java
) else (
  set JAVA_EXE=%JAVA_HOME%\bin\java.exe
)

"%JAVA_EXE%" -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%BASE_DIR%" %WRAPPER_LAUNCHER% %*
