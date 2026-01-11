# Data Collection

## Overview
This folder contains the collected product data scraped from Cdiscount.

## Structure
```
02_data_collection/
├── README.md           # This file
└── raw/                # Raw CSV files from scraper
    ├── cdiscount_smartphones.csv
    ├── cdiscount_claviers.csv
    ├── cdiscount_casques_bluetooth.csv
    ├── cdiscount_ordinateurs.csv
    └── *.csv
```

## Dataset Statistics

### Available Categories
| File | Category | Rows | Description |
|------|----------|------|-------------|
| `cdiscount_smartphones.csv` | Smartphones | 2,074 | Mobile phones |
| `cdiscount_claviers.csv` | Keyboards | 1,024 | PC keyboards |
| `cdiscount_casques_bluetooth.csv` | Headphones | ~800 | Bluetooth headsets |
| `cdiscount_ordinateurs.csv` | Computers | ~1,200 | Laptops & desktops |
| `cdiscount_products*.csv` | Mixed | Various | General products |

### CSV Column Descriptions
| Column | Type | Example | Description |
|--------|------|---------|-------------|
| `Title` | String | "Samsung Galaxy A15 4G..." | Product name |
| `Price` | String | "98,99 €" | Price (European format) |
| `Link` | String | "https://www.cdiscount.com/..." | Original product URL |
| `Image` | String | "https://www.cdiscount.com/pdt2/..." | Product image URL |
| `Description` | String | Long text | Full product description |
| `ReviewAuthor` | String | "Youssef • publié le" | Reviewer name |
| `ReviewRating` | String | "5,0" | Rating 1.0-5.0 (French decimal) |
| `ReviewTitle` | String | "Avis samsung A15" | Review headline |
| `ReviewBody` | String | "Téléphone avec un..." | Review content |
| `ReviewDate` | String | "28/10/2024" | Review date |

## Data Characteristics
- **One product = Multiple rows**: Each row represents one review for a product
- **Price format**: European (comma as decimal separator, € symbol)
- **Rating format**: French decimal notation (e.g., "5,0" instead of "5.0")
- **Empty fields**: Some review fields may be empty if no reviews exist

## Usage
This raw data is processed by the `03_preprocessing` module to create an aggregated, cleaned dataset suitable for the recommendation engine.
