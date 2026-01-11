# Recommendation Model

## Overview
This module implements the recommendation engine that scores and ranks products based on search queries and user preferences.

## Purpose
- Calculate text similarity between queries and products
- Score products using multiple factors (similarity, rating, reviews, price)
- Rank products from best to least recommended
- Filter products by price range and category

## Components

### Core Classes

#### `TFIDFVectorizer.java` (Machine Learning)
Implements text vectorization using Term Frequency-Inverse Document Frequency.
- **Training**: Learns vocabulary and IDF weights from 1,700+ products on startup.
- **Inference**: Converts text into numerical feature vectors.

#### `RecommendationEngine.java`
Main entry point for recommendations.

**Methods:**
- `getRecommendations(query, minPrice, maxPrice, category, maxResults)`: Get ranked recommendations
- `trainModel()`: Initializes and fits the TF-IDF vectorizer
- `getAvailableCategories()`: Get all product categories

**Algorithm:**
1. Train ML model on startup (Vocabulary building)
2. Filter products by price range and category
3. **Evidence**: Vectorize query and calculate **Cosine Similarity**
4. Apply bonuses (substring match, category match)
5. Calculate composite score
6. Rank and return top N results

#### `SimilarityCalculator.java`
Mathematical similarity logic.

**Methods:**
- `calculateCosineSimilarity(vectorA, vectorB)`: Computes angle between two vectors
- `substringBonus(query, title)`: Bonus if query is substring of title
- `categoryBonus(query, category)`: Bonus if query matches category

**Techniques:**
- **Cosine Similarity**: Measures semantic distance between query and product
- **Text Normalization**: Removing stop words and special characters
- **Legacy**: Jaccard similarity (kept as fallback)

#### `RankingAlgorithm.java`
Composite scoring and ranking logic.

**Scoring Weights:**
- Text similarity: 40%
- Average rating: 30%
- Number of reviews: 15%
- Price (inverse): 15%

**Methods:**
- `calculateScore(product, similarityScore, minPrice, maxPrice, maxReviews)`: Calculate composite score
- `rankResults(results)`: Sort and assign rank numbers
- `DatasetStats`: Statistics for normalization

**Score Calculation:**
```
finalScore = (similarity × 0.40) +
             (rating/5 × 0.30) +
             (reviews/max × 0.15) +
             ((1 - normalizedPrice) × 0.15)
```

#### `RecommendationResult.java`
Data model for a ranked recommendation.

**Fields:**
- Product info: id, title, price, imageUrl, link, description
- Metrics: avgRating, reviewCount, category
- Ranking: score, rank
- UI helpers: `getRankBadge()` (🥇, 🥈, 🥉, #N)

## Usage Example

```java
// Load products from preprocessing
List<Product> products = DataCleaner.processMultipleCSVs(...);

// Create engine
RecommendationEngine engine = new RecommendationEngine(products);

// Get recommendations
List<RecommendationResult> results = engine.getRecommendations(
    "smartphone samsung",  // query
    50.0,                  // minPrice
    200.0,                 // maxPrice
    "smartphones",         // category
    10                     // maxResults
);

// Display results
for (RecommendationResult r : results) {
    System.out.println(r.getRankBadge() + " " + r.getTitle());
    System.out.println("  Price: " + r.getPrice() + "€");
    System.out.println("  Rating: " + r.getAvgRating() + "/5 (" + r.getReviewCount() + " reviews)");
    System.out.println("  Score: " + r.getScore());
}
```

## Recommendation Quality

### Best Recommendations (High Score)
Products that:
- Match search query closely
- Have high average ratings
- Have many customer reviews
- Offer good price value

### Least Recommendations (Low Score)
Products that:
- Don't match query well
- Have low ratings
- Have few/no reviews
- Are expensive relative to others

## Customization

To adjust recommendation behavior, modify weights in `RankingAlgorithm.java`:

```java
private static final double WEIGHT_SIMILARITY = 0.40;  // Text match importance
private static final double WEIGHT_RATING = 0.30;      // Rating importance
private static final double WEIGHT_REVIEWS = 0.15;     // Review count importance
private static final double WEIGHT_PRICE = 0.15;       // Price importance
```

## Next Step
This recommendation engine is integrated into the Java Swing UI (`05_user_interface`) for interactive product search and browsing.
