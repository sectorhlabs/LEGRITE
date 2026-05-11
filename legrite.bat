@echo off
setlocal
set DIR=%~dp0
set JAR=%DIR%LEGRITE-cli.jar

if not exist "%JAR%" (
    echo Error: no se encuentra %JAR%
    exit /b 1
)

if "%~1"=="" (
    java -cp "%JAR%" cli.LegriteCLI --help
    echo.
    pause
    exit /b 0
)

java -cp "%JAR%" cli.LegriteCLI %*
