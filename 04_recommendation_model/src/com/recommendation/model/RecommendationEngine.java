package com.recommendation.model;

import com.recommendation.preprocessing.Product;
import java.util.*;

/**
 * Main recommendation engine.
 * Processes search queries and returns ranked product recommendations.
 * 
 * UPGRADE: Now uses TF-IDF Machine Learning for text similarity.
 * CONSOLIDATED: Now includes Similarity and Ranking logic directly.
 */
public class RecommendationEngine {

    private List<Product> allProducts;
    private DatasetStats stats;

    // ML Components
    private TFIDFVectorizer vectorizer;
    private Map<String, double[]> productVectors;
    private boolean modelReady = false;

    // Scoring weights (must sum to 1.0)
    private static final double WEIGHT_SIMILARITY = 0.40;
    private static final double WEIGHT_RATING = 0.30;
    private static final double WEIGHT_REVIEWS = 0.15;
    private static final double WEIGHT_PRICE = 0.15;

    public RecommendationEngine(List<Product> products) {
        this.allProducts = products;
        this.stats = new DatasetStats(products);
        trainModel();
    }

    private void trainModel() {
        System.out.println("Initializing Machine Learning Model...");
        this.vectorizer = new TFIDFVectorizer();
        this.productVectors = new HashMap<>();

        List<String> corpus = new ArrayList<>();
        for (Product p : allProducts) {
            corpus.add(p.getTitle() + " " + p.getDescription());
        }

        vectorizer.fit(corpus);

        for (Product p : allProducts) {
            productVectors.put(p.getId(), vectorizer.transform(p.getTitle() + " " + p.getDescription()));
        }

        this.modelReady = true;
        System.out.println("Model trained and products vectorized.");
    }

    public List<RecommendationResult> getRecommendations(String query, double minPrice, double maxPrice,
            String category, int maxResults) {
        List<RecommendationResult> results = new ArrayList<>();
        if (!modelReady)
            return results;

        List<Product> filtered = filterProducts(minPrice, maxPrice, category);
        if (filtered.isEmpty())
            return results;

        double[] queryVector = vectorizer.transform(query);
        String[] queryKeywords = query.toLowerCase().split("\\s+");

        for (Product product : filtered) {
            double[] pVector = productVectors.get(product.getId());
            double similarityScore = calculateCosineSimilarity(queryVector, pVector);

            // Domain bonuses
            similarityScore += substringBonus(query, product.getTitle());
            similarityScore += categoryBonus(query, product.getCategory());

            // Fuzzy keyword matching
            String productText = (product.getTitle() + " " + product.getDescription()).toLowerCase();
            int matchCount = 0;
            boolean hasValidKeywords = false;
            for (String keyword : queryKeywords) {
                if (keyword.length() > 2) {
                    hasValidKeywords = true;
                    int maxEdits = keyword.length() <= 6 ? 1 : 2;
                    if (fuzzyContains(productText, keyword, maxEdits)) {
                        matchCount++;
                    }
                }
            }

            if (hasValidKeywords && matchCount == 0)
                continue;

            if (queryKeywords.length > 0 && matchCount > 0) {
                similarityScore += (double) matchCount / queryKeywords.length * 0.15;
            }

            similarityScore = Math.min(1.0, similarityScore);

            double finalScore = calculateCompositeScore(product, similarityScore);

            results.add(new RecommendationResult(
                    product.getId(), product.getTitle(), product.getPrice(),
                    product.getImageUrl(), product.getLink(), product.getDescription(),
                    product.getAvgRating(), product.getReviewCount(), finalScore, product.getCategory()));
        }

        // Rank and Filter
        results.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        for (int i = 0; i < results.size(); i++)
            results.get(i).setRank(i + 1);

        results.removeIf(r -> r.getScore() < 0.15); // MIN_SCORE_THRESHOLD

        return results.size() > maxResults ? results.subList(0, maxResults) : results;
    }

    private double calculateCompositeScore(Product p, double simScore) {
        double ratingScore = p.getAvgRating() / 5.0;

        // Fixed scale normalization (consistent results)
        double reviewScore = Math.min(p.getReviewCount() / 100.0, 1.0);
        double priceScore = 1.0 - Math.min(p.getPrice() / 1000.0, 1.0);

        return (simScore * WEIGHT_SIMILARITY) + (ratingScore * WEIGHT_RATING) +
                (reviewScore * WEIGHT_REVIEWS) + (priceScore * WEIGHT_PRICE);
    }

    private List<Product> filterProducts(double minPrice, double maxPrice, String category) {
        List<Product> filtered = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.getPrice() < minPrice || p.getPrice() > maxPrice)
                continue;
            if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("All Categories")) {
                if (!p.getCategory().equalsIgnoreCase(category))
                    continue;
            }
            filtered.add(p);
        }
        return filtered;
    }

    // --- Similarity Helpers ---

    private double calculateCosineSimilarity(double[] v1, double[] v2) {
        if (v1.length != v2.length)
            return 0.0;
        double dot = 0.0, n1 = 0.0, n2 = 0.0;
        for (int i = 0; i < v1.length; i++) {
            dot += v1[i] * v2[i];
            n1 += v1[i] * v1[i];
            n2 += v2[i] * v2[i];
        }
        return (n1 == 0 || n2 == 0) ? 0.0 : dot / (Math.sqrt(n1) * Math.sqrt(n2));
    }

    private double substringBonus(String query, String title) {
        return normalize(title).contains(normalize(query)) ? 0.2 : 0.0;
    }

    private double categoryBonus(String query, String cat) {
        String nQ = normalize(query), nC = normalize(cat);
        return (nQ.contains(nC) || nC.contains(nQ)) ? 0.15 : 0.0;
    }

    private boolean fuzzyContains(String text, String keyword, int maxEdits) {
        if (text.contains(keyword))
            return true;
        for (String word : text.split("\\s+")) {
            if (Math.abs(word.length() - keyword.length()) <= maxEdits) {
                if (levenshtein(word, keyword) <= maxEdits)
                    return true;
            }
        }
        return false;
    }

    private int levenshtein(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++)
            dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++)
            dp[0][j] = j;
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[s1.length()][s2.length()];
    }

    private String normalize(String t) {
        return t == null ? "" : t.toLowerCase().replaceAll("[^a-z0-9\\s]", "").trim();
    }

    // --- Statistics ---

    private static class DatasetStats {
        double minPrice = Double.MAX_VALUE, maxPrice = Double.MIN_VALUE;
        int maxReviews = 0;

        DatasetStats(List<Product> products) {
            for (Product p : products) {
                if (p.getPrice() > 0) {
                    minPrice = Math.min(minPrice, p.getPrice());
                    maxPrice = Math.max(maxPrice, p.getPrice());
                }
                maxReviews = Math.max(maxReviews, p.getReviewCount());
            }
            if (minPrice == Double.MAX_VALUE)
                minPrice = 0;
            if (maxPrice == Double.MIN_VALUE)
                maxPrice = 0;
        }
    }

    public Map<String, Integer> getCategoryStats() {
        Map<String, Integer> statsMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Product p : allProducts) {
            String cat = p.getCategory();
            if (cat != null && !cat.isEmpty()) {
                statsMap.put(cat, statsMap.getOrDefault(cat, 0) + 1);
            }
        }
        return statsMap;
    }

    public double[] getPriceRange() {
        return new double[] { stats.minPrice, stats.maxPrice };
    }

    public int getTotalProducts() {
        return allProducts.size();
    }
}
