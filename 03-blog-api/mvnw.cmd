@echo off
REM -----------------------------------------------------------------------------
REM Simple Maven launcher script for Windows.
REM
REM This project does not ship the full Maven Wrapper binary/jar. Instead, this
REM lightweight script simply delegates every argument straight through to a
REM `mvn` installation that must already be available on your PATH. It exists
REM so the README's "mvnw.cmd ..." instructions work the same way on any
REM machine that has Maven installed, without committing a wrapper jar to
REM source control.
REM -----------------------------------------------------------------------------
setlocal

where mvn >nul 2>nul
if errorlevel 1 (
    echo Error: Maven (mvn) was not found on your PATH.
    echo Install Apache Maven 3.9+ and re-run this script.
    exit /b 1
)

mvn %*
