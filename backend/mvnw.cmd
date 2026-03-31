@REM Maven Wrapper startup batch script
@REM See https://maven.apache.org/wrapper/

@IF "%__MVNW_ARG0_S%"=="" (SET __MVNW_ARG0_S=%0)
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_PSMODULEP_SAVE=%PSModulePath%
@SET PSModulePath=

@FOR /F "usebackq tokens=1* delims==" %%A IN (`powershell -noprofile "& {$scriptDir='%~dp0teleports'; Get-ChildItem Env: | ForEach-Object {'{0}={1}' -f $_.Key,$_.Value}}"`) DO @(
    @IF "%%A"=="MVNW_USERNAME" SET MVNW_USERNAME=%%B
    @IF "%%A"=="MVNW_PASSWORD" SET MVNW_PASSWORD=%%B
)

@SET PSModulePath=%__MVNW_PSMODULEP_SAVE%

@SET WRAPPER_JAR="%~dp0.mvn\wrapper\maven-wrapper.jar"
@SET WRAPPER_PROPERTIES="%~dp0.mvn\wrapper\maven-wrapper.properties"

@IF NOT EXIST %WRAPPER_JAR% (
    @ECHO Downloading Maven Wrapper...
)

@SET JAVA_EXE=java.exe
@IF NOT "%JAVA_HOME%"=="" SET JAVA_EXE="%JAVA_HOME%\bin\java.exe"

%JAVA_EXE% ^
  -jar %WRAPPER_JAR% ^
  %*
@IF ERRORLEVEL 1 goto error
goto end

:error
@SET ERROR_CODE=1
:end
@EXIT /B %ERROR_CODE%
