@echo off
REM Image Downloader - Run Script

echo ===============================================
echo   PRODUCT IMAGE DOWNLOADER
echo ===============================================
echo.
echo This will download all product images from the CSV files.
echo Images will be saved to: 02_data_collection\images\
echo.
echo This may take 10-30 minutes depending on the number of products.
echo.
pause

cd "%~dp0"

REM Run with Selenium dependencies
java -cp "bin;..\01_scrapping\lib\selenium-java-4.39.0\*;..\01_scrapping\lib\*" com.recommendation.tools.ImageDownloader

echo.
pause
