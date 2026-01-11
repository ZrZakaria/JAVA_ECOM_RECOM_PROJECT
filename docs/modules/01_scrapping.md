# Scraping Module

## Overview
This folder contains the web scraping module that collects product data from Cdiscount using Selenium and JSoup.

## Purpose
- Scrape product listings from Cdiscount categories
- Extract product details (title, price, image, description)
- Collect customer reviews for each product
- Output data in CSV format for further processing

## Original Location
The scraper code is maintained in `../scrap_no_maven/` directory.

## Key Sub-Modules

### 1. Data Scrapper (`01_scrapping/data_scraper/`)
- **ProductScrapper.java**: Main scraper using Selenium WebDriver.
- **ScrapingConfig.java**: Configuration for Cdiscount scraping.
- **RichProductRecord.java**: Data model for product + review records.
- **CSVWriterRich.java**: CSV output writer.

### 2. Image Scrapper (`01_scrapping/image_scraper/`)
- **ImageScraper.java**: Downloads and caches product images locally.
- **ImageCache.java**: Manages the local image repository in `02_data_collection/images/`.

## How It Works
1. **Data Phase**: The `ProductScrapper` opens category pages, extracts listings, and visits product pages for detail/reviews.
2. **Image Phase**: The `ImageScraper` iterates over CSV data and downloads images for offline viewing in the GUI.
3. **Storage**: All raw data is stored in `02_data_collection/raw/`.

## Output Format
Each CSV file contains 10 columns:
- Title, Price, Link, Image, Description
- ReviewAuthor, ReviewRating, ReviewTitle, ReviewBody, ReviewDate

## Usage
See `../scrap_no_maven/USER_GUIDE.md` for detailed setup and usage instructions.
