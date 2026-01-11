         @echo off
REM Image Downloader - Compile Script

echo ===============================================
echo   COMPILING IMAGE DOWNLOADER
echo ===============================================
echo.

cd "%~dp0"

REM Create bin directory
if not exist "bin" mkdir bin

REM Compile with Selenium dependencies
echo Compiling ImageDownloader...
javac -d bin -cp "..\01_scrapping\lib\selenium-java-4.39.0\*;..\01_scrapping\lib\*" src\com\recommendation\tools\ImageDownloader.java

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Compilation failed
    pause
    exit /b 1
)

echo.
echo Compilation successful!
echo.
echo To run the downloader, execute: run_downloader.bat
echo.
pause
