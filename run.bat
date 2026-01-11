@echo off
REM Product Recommendation System - Run Script
REM Windows Batch File

echo ===============================================
echo   PRODUCT RECOMMENDATION SYSTEM
echo ===============================================
echo.
echo Starting application...
echo.

cd "%~dp0\05_user_interface"
java -cp "bin;..\03_preprocessing\bin;..\04_recommendation_model\bin" com.recommendation.ui.MainFrame

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Application failed to start
    echo.
    echo Make sure you have compiled the project first using compile.bat
    echo.
    pause
)
