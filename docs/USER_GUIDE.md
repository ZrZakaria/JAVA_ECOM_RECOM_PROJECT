# Scraper User Guide

This guide explains how to set up and run the Cdiscount Scraper in the `scrap_no_maven` project.

## 1. Prerequisites
- **Java JDK 17+**: Ensure `java` and `javac` are in your command line PATH.
- **Google Chrome**: The scraper uses Chrome for dynamic page rendering.
- **Chromedriver**: Must match your Chrome version and be available in your system PATH.

## 2. Setup (Dependencies)
You must have the following JAR files in the `lib/` folder:
1. `jsoup-1.17.2.jar`
2. `selenium-java-4.39.0/` folder (containing all Selenium JARs).

*Refer to [DEPENDENCY_SETUP.md](DEPENDENCY_SETUP.md) for download instructions if missing.*

## 3. How to Build
Double-click **`compile.bat`**. This will compile the source code and place the classes in the `bin/` folder.

## 4. How to Run

### Option A: Windows Explorer (Recommended)
Simply **double-click** `compile.bat` and then `run.bat`.

### Option B: Command Prompt (CMD)
1. Open CMD and navigate to this folder.
2. Type `compile.bat` and press Enter.
3. Type `run.bat` and press Enter.

### Option C: PowerShell (VS Code Terminal)
PowerShell requires you to use the `.\` prefix for local scripts:
1. Type `.\compile.bat` and press Enter.
2. Type `.\run.bat` and press Enter.

### Run with Custom Categories (Terminal)
```bash
.\run.bat "https://www.cdiscount.com/your-category-url"
```

## 5. View Results
After the scraper finish, check for a file named **`cdiscount_claviers.csv`** in the project root.
- The CSV contains: Title, Price, Link, Image, Description, and Review details.
- Each row represents one review. If a product has no reviews, it will have one row with empty review fields.

## 6. Modifying the Scraper
- To change the default URLs or the output filename, edit `src/com/example/scrapper/Main.java`.
- **Note**: You must run `compile.bat` after any changes to the `.java` files.
