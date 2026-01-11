# Java E-Commerce Recommendation Project

## Overview
This project is a comprehensive E-Commerce Recommendation System built in Java. It encompasses the entire pipeline from web scraping and data collection to data preprocessing, model training, and a user-friendly Swing-based interface for product browsing and recommendations.

## Features
-   **Web Scraping**: Automated collection of product data (Cdiscount).
-   **Data Preprocessing**: Cleaning and vectorization of product text data using TF-IDF.
-   **Recommendation Engine**: Content-based filtering using Cosine Similarity to find related products.
-   **Interactive UI**: A modern Java Swing application to browse products, view details, and see recommendations.
-   **Efficient Data Handling**: Optimized data structures for fast retrieval and display.

## Project Structure
The project is organized into modular components:

-   `01_scrapping/`: Python/Java scripts for scraping product data.
-   `02_data_collection/`: Raw data storage and collection logic.
-   `03_preprocessing/`: Java modules for cleaning and preparing data.
-   `04_recommendation_model/`: The core logic for the recommendation algorithms.
-   `05_user_interface/`: The main GUI application (Java Swing).
-   `06_tests/`: Unit tests and verification scripts.
-   `compile.bat`: Windows batch script to compile all modules in order.
-   `run.bat`: Windows batch script to launch the application.

## Prerequisites
-   **Java Development Kit (JDK)**: Ensure JDK 8 or higher is installed and configured in your system PATH.
-   **Internet Connection**: Required for fetching product images and scraping data.

## Installation & Compilation
This project uses a custom batch script to handle module dependencies and compilation order.

1.  Open a terminal (Command Prompt or PowerShell).
2.  Navigate to the project root directory.
3.  Run the compilation script:
    ```cmd
    compile.bat
    ```
    This script will:
    -   Compile `03_preprocessing`
    -   Compile `04_recommendation_model` (depends on preprocessing)
    -   Compile `05_user_interface` (depends on model and preprocessing)

    *Note: Ensure all steps show "compiled successfully".*

## Usage Guide
Once compiled, you can launch the application easily.

### Running via Batch Script (Recommended)
Simply run the `run.bat` file from the project root:
```cmd
run.bat
```

### Running Manually
Alternatively, you can navigate to the UI directory and run the Java command:
```cmd
cd 05_user_interface
java -cp "bin;..\03_preprocessing\bin;..\04_recommendation_model\bin" com.recommendation.ui.MainFrame
```

## Module Details

### Scrapping (`01_scrapping`)
Contains the logic to scrape product details from e-commerce sites. Ensure you have necessary drivers (e.g., ChromeDriver) if using Selenium-based scrapers.

### Preprocessing (`03_preprocessing`)
Handles text cleaning, tokenization, and vectorization (TF-IDF). It transforms raw product descriptions into mathematical vectors suitable for the recommendation engine.

### Recommendation Model (`04_recommendation_model`)
Implements the core logic. It compares the vector of a selected product against others to find the most similar items using Cosine Similarity.

### User Interface (`05_user_interface`)
A rich desktop application providing:
-   **Product Grid**: Browse available products.
-   **Search**: Find products by keywords.
-   **Product Details**: View full specs and "Recommended Products" side-by-side.

## Troubleshooting
-   **"Application failed to start"**: Ensure you ran `compile.bat` first.
-   **Missing Images**: Check your internet connection; images are fetched dynamically.
-   **Compilation Errors**: Verify your JDK installation (`java -version` and `javac -version`).
