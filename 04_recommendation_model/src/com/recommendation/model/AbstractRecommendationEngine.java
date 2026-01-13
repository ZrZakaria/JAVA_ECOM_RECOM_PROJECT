package com.recommendation.model;

import com.recommendation.preprocessing.Product;
import java.util.List;

/**
 * Abstract class defining the core structure of a Recommendation Engine.
 * Demonstrates Inheritance and Abstraction in Java.
 */
public abstract class AbstractRecommendationEngine {

    protected List<Product> allProducts;
    protected boolean modelReady = false;

    public AbstractRecommendationEngine(List<Product> products) {
        this.allProducts = products;
    }

    /**
     * Train the underlying model (TF-IDF, Sentiment, etc.)
     */
    protected abstract void trainModel();

    /**
     * Get ranked recommendations for a query.
     */
    public abstract List<RecommendationResult> getRecommendations(
            String query,
            double minPrice,
            double maxPrice,
            String category,
            int maxResults);

    public boolean isModelReady() {
        return modelReady;
    }

    public int getProductCount() {
        return allProducts != null ? allProducts.size() : 0;
    }
}
