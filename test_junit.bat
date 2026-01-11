@echo off
REM Product Recommendation System - JUnit Test Script
REM Windows Batch File

echo ===============================================
echo   RUNNING PRODUCT RECOMMENDATION UNIT TESTS
echo ===============================================
echo.

set JUNIT_JAR=06_tests\lib\junit-platform-console-standalone-1.10.2.jar

REM 1. Compile Modules if needed
echo [1/3] Compiling Project Modules...
if not exist "03_preprocessing\bin" mkdir "03_preprocessing\bin"
javac -d 03_preprocessing\bin 03_preprocessing\src\com\recommendation\preprocessing\*.java

if not exist "04_recommendation_model\bin" mkdir "04_recommendation_model\bin"
javac -d 04_recommendation_model\bin -cp "03_preprocessing\bin" 04_recommendation_model\src\com\recommendation\model\*.java

echo   ✓ Modules compiled.
echo.

REM 2. Compile Tests
echo [2/3] Compiling JUnit Tests...
if not exist "06_tests\bin" mkdir "06_tests\bin"
set TEST_CP="06_tests\bin;03_preprocessing\bin;04_recommendation_model\bin;%JUNIT_JAR%"
javac -cp %TEST_CP% -d 06_tests\bin 06_tests\src\com\recommendation\test\*.java

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to compile tests.
    pause
    exit /b 1
)
echo   ✓ Tests compiled.
echo.

REM 3. Run Tests
echo [3/3] Executing Tests...
if not exist "06_tests\reports" mkdir "06_tests\reports"
java -jar %JUNIT_JAR% --class-path %TEST_CP% --scan-class-path --reports-dir 06_tests\reports

echo.
echo ===============================================
echo   TESTING COMPLETE!
echo ===============================================
echo.
pause
