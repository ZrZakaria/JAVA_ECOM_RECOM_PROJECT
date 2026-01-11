# Product Recommendation System

## 📋 Project Overview

A complete **Java-based Product Recommendation System** that uses scraped product data from Cdiscount to provide intelligent, ranked product recommendations based on user queries and preferences.

### Key Features
- 🧠 **Machine Learning**: **TF-IDF Vectorization** for content-based filtering
- 🔍 **Smart Search**: **Cosine Similarity** matching for query-product relevance
- 📊 **Multi-factor Ranking**: Combines similarity, ratings, reviews, and price
- 🎨 **Modern UI**: Java Swing interface with Teal/Slate dark theme
- 🏷️ **Category Filtering**: Filter by product categories
- 💰 **Price Range Filtering**: Set min/max price limits
- 🥇 **Ranked Results**: Clear ranking indicators (🥇 best, 🥈, 🥉, etc.)
- 🔗 **Direct Links**: Open products on Cdiscount with one click

---

## 🏗️ Project Structure

```
c:\Studies\S7\JAVA\scrapmaven\
│
├── scrap_no_maven/              # Original scraper module
│   ├── src/com/example/scrapper/
│   ├── lib/                     # Selenium, JSoup dependencies
│   ├── *.csv                    # Generated CSV data
│   └── *.md                     # Original documentation
│
├── 01_scrapping/                # Scraper documentation
│   └── README.md
│
├── 02_data_collection/          # Collected CSV data
│   ├── README.md
│   └── raw/                     # CSV files (7 files, ~5000+ rows)
│
├── 03_preprocessing/            # Data preprocessing module
│   ├── README.md
│   ├── src/com/recommendation/preprocessing/
│   │   ├── Review.java          # Review model
│   │   ├── Product.java         # Product model with reviews
│   │   ├── DataLoader.java      # CSV parsing utilities
│   │   ├── DataCleaner.java     # Aggregation logic
│   │   └── PreprocessingMain.java
│   └── output/                  # Preprocessed data output
│
├── 04_recommendation_model/     # Recommendation engine
│   ├── README.md
│   └── src/com/recommendation/model/
│       ├── RecommendationEngine.java     # Main engine
│       ├── SimilarityCalculator.java     # Text similarity
│       ├── RankingAlgorithm.java         # Scoring & ranking
│       └── RecommendationResult.java     # Result model
│
├── 05_user_interface/           # Java Swing GUI (Material Design)
│   ├── src/com/recommendation/ui/
│   └── README.md
│
├── 06_tests/                    # Automated Testing Module [NEW]
│   ├── src/com/recommendation/test/
│   ├── lib/                     # Embedded JUnit Framework
│   ├── reports/                 # Auto-generated Test Results
│   └── README.md
│
└── docs/                        # Project Documentation
    ├── PROJECT_README.md        # This file
    ├── project_log.md           # Complete file creation log
    ├── verification_report.md   # Project criteria evaluation
    ├── project_metrics_report.md # LOC and Data Row counts
    └── official_evaluation_sheet.md # Academic grading sheet
```

---

## 💡 How It Works

### 1. Data Collection (Scraping)
The scraper (`scrap_no_maven/`) uses **Selenium + JSoup** to:
- Navigate Cdiscount category pages
- Extract product listings
- Visit each product page for details and reviews
- Output to CSV format (one row per product-review pair)

**CSV Columns:**
`Title, Price, Link, Image, Description, ReviewAuthor, ReviewRating, ReviewTitle, ReviewBody, ReviewDate`

### 2. Data Preprocessing
The preprocessing module (`03_preprocessing/`) handles:
- **CSV Parsing**: Handles European formats ("98,99 €" → 98.99)
- **Product Aggregation**: Combines multiple review rows into single products
- **Rating Calculation**: Computes average ratings
- **Data Cleaning**: Normalizes text and handles missing values

**Output:** List of `Product` objects with aggregated review data

### 3. Recommendation Algorithm
The engine (`04_recommendation_model/`) implements a **Machine Learning Content-Based Filtering** system:

**Training Phase:**
- Learns a vocabulary of ~12,000 words from product descriptions using **TF-IDF**.
- Calculates IDF weights to determine word importance.

**Inference Phase:**
- Converts user queries into numerical vectors.
- Calculates **Cosine Similarity** between query vector and product vectors.

```
Final Score = (Cosine Similarity × 0.40) + (Rating × 0.30) + (Reviews × 0.15) + (Price × 0.15)
```

**Ranking:**
- Products sorted by final score (descending)
- Top result marked as 🥇 "BEST"
- Ranks assigned (#1, #2, #3, ...)

### 4. User Interface
The Java Swing UI (`05_user_interface/`) provides:
- **Search Panel**: Query input, category dropdown, price range sliders
- **Results Panel**: Scrollable list of ranked product cards
- **Product Cards**: Title, rating, price, description, link to Cdiscount
- **Modern Dark Theme**: Professional appearance with green accents

---

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- All CSV data files in `02_data_collection/raw/`

### Compile & Run

```powershell
# Navigate to project root
cd c:\Studies\S7\JAVA\scrapmaven

# Compile preprocessing module
cd 03_preprocessing
javac -d bin src/com/recommendation/preprocessing/*.java

# Compile recommendation model
cd ..\04_recommendation_model
javac -d bin -cp ../03_preprocessing/bin src/com/recommendation/model/*.java

# Compile UI
cd ..\05_user_interface
javac -d bin -cp ../03_preprocessing/bin;../04_recommendation_model/bin src/com/recommendation/ui/*.java

# Run All Unit Tests
cd ..
.\test_junit.bat

# Run the application
java -cp 05_user_interface\bin;03_preprocessing\bin;04_recommendation_model\bin com.recommendation.ui.MainFrame
```

### First Run
1. Application launches and loads CSV data
2. Success dialog shows total products loaded
3. Use search panel to enter query (e.g., "samsung")
4. Click "Search Products"
5. View ranked recommendations
6. Click "View on Cdiscount" to open product page

---

## 📊 Dataset Statistics

| Category | File | Approximate Rows |
|----------|------|------------------|
| Smartphones | `cdiscount_smartphones.csv` | 2,074 |
| Keyboards | `cdiscount_claviers.csv` | 1,024 |
| Headphones | `cdiscount_casques_bluetooth.csv` | ~800 |
| Computers | `cdiscount_ordinateurs.csv` | ~1,200 |
| Mixed | `cdiscount_products*.csv` | ~1,500 |

**Total:** **5,307** Unique Product Entries across all files.

---

## 🧪 Testing & Verification

The project includes a robust verification suite to ensure code quality and mathematical correctness.

- **Automated Tests**: 10 JUnit tests covering TF-IDF Vectorization, Logic, and Data Parsing.
- **Reporting**: Automated `test_results.csv` export for audit purposes.
- **Batched Runner**: `test_junit.bat` for one-click project-wide verification.

---

## 🎯 Usage Examples

### Example 1: Search for Smartphones
```
Query: "samsung galaxy"
Category: smartphones
Price Range: 0 - 300€
Results: Top 10 Samsung Galaxy products ranked by score
```

### Example 2: Find Budget Keyboards
```
Query: "clavier"
Category: claviers
Price Range: 0 - 50€
Results: Affordable keyboards with best ratings
```

### Example 3: Browse All Products
```
Query: (empty)
Category: All
Price Range: 0 - 999999€
Results: All products ranked by rating and reviews
```

---

## 🔧 Technical Details

### Technologies Used
- **Java 17**: Core programming language
- **Java Swing**: GUI framework
- **Selenium WebDriver**: Web scraping (in scraper module)
- **JSoup**: HTML parsing (in scraper module)

### Design Patterns
- **MVC Architecture**: Model (Product, Review), View (UI), Controller (Engine)
- **Builder Pattern**: Used in `Product` class
- **Strategy Pattern**: Scoring algorithm can be swapped

### Algorithms
- **TF-IDF Vectorization**: Machine Learning for text feature extraction
- **Cosine Similarity**: Vector-based relevance scoring
- **Weighted Scoring**: Multi-factor recommendation
- **Normalization**: Price and review counts normalized to [0, 1]

---

## 📖 Module Documentation

Each module has detailed README:
- [`01_scrapping/README.md`](file:///c:/Studies/S7/JAVA/scrapmaven/01_scrapping/README.md) - Scraper overview
- [`02_data_collection/README.md`](file:///c:/Studies/S7/JAVA/scrapmaven/02_data_collection/README.md) - Dataset description
- [`03_preprocessing/README.md`](file:///c:/Studies/S7/JAVA/scrapmaven/03_preprocessing/README.md) - Preprocessing details
- [`04_recommendation_model/README.md`](file:///c:/Studies/S7/JAVA/scrapmaven/04_recommendation_model/README.md) - Algorithm explanation
- [`05_user_interface/README.md`](file:///c:/Studies/S7/JAVA/scrapmaven/05_user_interface/README.md) - UI usage guide

---

## 📝 Project Log

For a complete, detailed log of every file and folder created with explanations, see:
[`docs/project_log.md`](file:///c:/Studies/S7/JAVA/scrapmaven/docs/project_log.md)

---

## 👨‍💻 Development

### Adding New Features

**To adjust scoring weights:**
Edit `RankingAlgorithm.java`:
```java
private static final double WEIGHT_SIMILARITY = 0.40;
private static final double WEIGHT_RATING = 0.30;
private static final double WEIGHT_REVIEWS = 0.15;
private static final double WEIGHT_PRICE = 0.15;
```

**To add new categories:**
Edit `SearchPanel.java`:
```java
String[] categories = {"All", "smartphones", "new_category", ...};
```

**To change UI theme:**
Edit `UIStyles.java` color constants and fonts.

---

## ⚡ Performance

- **Data Loading**: ~2-5 seconds for 5000+ rows
- **Search**: <1 second for typical queries
- **Ranking**: O(n log n) where n = number of matching products
- **UI Rendering**: Asynchronous, non-blocking

---

## 🐛 Troubleshooting

### Issue: "Failed to load data"
**Solution:** Ensure CSV files exist in `02_data_collection/raw/`

### Issue: Compile errors
**Solution:** Check classpath includes all module bin directories

### Issue: UI doesn't display results
**Solution:** Wait for initial data load to complete (check success dialog)

---

## 📄 License

This project was created as part of a JAVA S7 academic assignment.

---

## 🙏 Acknowledgments

- **Cdiscount**: Product data source
- **Selenium**: Web automation framework
- **JSoup**: HTML parsing library
