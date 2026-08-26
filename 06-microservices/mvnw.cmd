@ECHO OFF
REM ----------------------------------------------------------------------------
REM Simple Maven Wrapper launcher for Windows.
REM
REM This multi-module project does not vendor the full Maven Wrapper binary
REM jar. To keep the curriculum lightweight, this script simply delegates
REM every argument straight through to a Maven installation already on the
REM PATH. Students who have installed Maven (see README.md) can run
REM ".\mvnw.cmd -pl student-service spring-boot:run" exactly as they would
REM with a real wrapper.
REM ----------------------------------------------------------------------------
SETLOCAL

WHERE mvn >NUL 2>NUL
IF ERRORLEVEL 1 (
    ECHO [ERROR] Apache Maven ^(mvn^) was not found on the PATH.
    ECHO         Install Maven from https://maven.apache.org/download.cgi
    ECHO         or install it via a package manager, then re-run this script.
    EXIT /B 1
)

mvn %*
EXIT /B %ERRORLEVEL%
