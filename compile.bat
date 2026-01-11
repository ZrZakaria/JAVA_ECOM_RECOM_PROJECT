@echo off
REM Product Recommendation System - Compilation Script
REM Windows Batch File

echo ===============================================
echo   COMPILING PRODUCT RECOMMENDATION SYSTEM
echo ===============================================
echo.

REM Step 1: Compile Preprocessing Module
echo [1/3] Compiling Preprocessing Module...
cd "%~dp0\03_preprocessing"
if not exist "bin" mkdir bin
javac -d bin src\com\recommendation\preprocessing\*.java
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to compile preprocessing module
    pause
    exit /b 1
)
echo   ✓ Preprocessing compiled successfully
echo.

REM Step 2: Compile Recommendation Model
echo [2/3] Compiling Recommendation Model...
cd "%~dp0\04_recommendation_model"
if not exist "bin" mkdir bin
javac -d bin -cp "..\03_preprocessing\bin" src\com\recommendation\model\*.java
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to compile recommendation model
    pause
    exit /b 1
)
echo   ✓ Recommendation Model compiled successfully
echo.

REM Step 3: Compile User Interface
echo [3/3] Compiling User Interface...
cd "%~dp0\05_user_interface"
if not exist "bin" mkdir bin
javac -d bin -cp "..\03_preprocessing\bin;..\04_recommendation_model\bin" src\com\recommendation\ui\*.java src\com\recommendation\ui\components\*.java src\com\recommendation\ui\core\*.java
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to compile user interface
    pause
    exit /b 1
)
echo   ✓ User Interface compiled successfully
echo.

echo ===============================================
echo   COMPILATION COMPLETE!
echo ===============================================
echo.
echo To run the application:
echo   cd 05_user_interface
echo   java -cp "bin;..\03_preprocessing\bin;..\04_recommendation_model\bin" com.recommendation.ui.MainFrame
echo.
echo Or simply run: run.bat
echo.
pause
