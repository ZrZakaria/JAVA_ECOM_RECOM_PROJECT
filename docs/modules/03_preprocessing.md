# Preprocessing Module

## Overview
This module processes raw CSV data from the scraper, aggregates products with their reviews, and prepares clean data for the recommendation engine.

## Purpose
- Load raw CSV files
- Parse European price formats and French decimal notation
- Aggregate multiple review rows into single product entries
- Calculate average ratings and review counts
- Clean and normalize text data
- Output preprocessed data for the recommendation model

## Components

### Data Models
- **`Review.java`**: Represents a single customer review
  - Fields: author, rating (1.0-5.0), title, body, date
  
- **`Product.java`**: Represents a product with aggregated reviews
  - Fields: id, title, price, link, imageUrl, description, category
  - Aggregated: reviews list, avgRating, reviewCount

### Utilities
- **`DataLoader.java`**: CSV parsing and format conversion
  - `loadCSV()`: Load CSV with quoted field handling
  - `parsePrice()`: Convert "98,99 €" → 98.99
  - `parseRating()`: Convert "5,0" → 5.0
  - `parseDate()`: Convert "28/10/2024" → LocalDate
  - `generateId()`: Create unique product IDs
  - `extractCategory()`: Extract category from filename

- **`DataCleaner.java`**: Data aggregation and cleaning
  - `processCSV()`: Process single CSV file
  - `processMultipleCSVs()`: Combine multiple files
  - `cleanText()`: Normalize text fields
  - `saveToTextFile()`: Export for inspection

### Main Program
- **`PreprocessingMain.java`**: Entry point
  - Loads all CSV files from `02_data_collection/raw/`
  - Aggregates products across all categories
  - Calculates statistics
  - Saves output to `output/preprocessed_products.txt`

## How to Run

### Compile
```bash
cd 03_preprocessing
javac -d bin src/com/recommendation/preprocessing/*.java
```

### Execute
```bash
java -cp bin com.recommendation.preprocessing.PreprocessingMain
```

## Output
Creates `output/preprocessed_products.txt` containing:
- Total product count and statistics
- Each product with:
  - ID, title, price, category
  - Review count and average rating
  - Description (truncated)
  - Sample reviews

## Data Transformations

### Input (Raw CSV)
```
Title,Price,Link,Image,Description,ReviewAuthor,ReviewRating,ReviewTitle,ReviewBody,ReviewDate
"Samsung Galaxy A15","98,99 €","https://...","https://...","Description","User",,"5,0","Great","Good phone","28/10/2024"
"Samsung Galaxy A15","98,99 €","https://...","https://...","Description","User2","4,0","OK","Average","29/10/2024"
```

### Output (Aggregated Product)
```
Product[
  id=prod_123456789
  title=Samsung Galaxy A15
  price=98.99€
  reviews=2
  avgRating=4.5
  category=smartphones
]
```

## Next Step
The preprocessed product data will be used by the `04_recommendation_model` module to generate recommendations.
