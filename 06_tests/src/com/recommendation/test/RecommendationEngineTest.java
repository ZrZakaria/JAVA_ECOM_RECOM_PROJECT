package com.recommendation.test;

import com.recommendation.model.RecommendationEngine;
import com.recommendation.model.RecommendationResult;
import com.recommendation.preprocessing.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RecommendationEngineTest {

    private RecommendationEngine engine;
    private List<Product> mockProducts;

    @BeforeEach
    public void setUp() {
        mockProducts = Arrays.asList(
                new Product("p1", "Samsung Galaxy S23", 800.0, "link1", "img1", "Smartphone", "Smartphones"),
                new Product("p2", "iPhone 15 Pro", 1200.0, "link2", "img2", "Apple Phone", "Smartphones"),
                new Product("p3", "Dell XPS 13", 1500.0, "link3", "img3", "Laptop", "Laptops"));
        engine = new RecommendationEngine(mockProducts);
    }

    @Test
    public void testPriceRange() {
        double[] range = engine.getPriceRange();
        assertEquals(800.0, range[0], 0.001);
        assertEquals(1500.0, range[1], 0.001);
    }

    @Test
    public void testCategoryStats() {
        Map<String, Integer> stats = engine.getCategoryStats();
        assertEquals(2, stats.get("Smartphones"));
        assertEquals(1, stats.get("Laptops"));
    }

    @Test
    public void testGetRecommendations() {
        // Search for "Samsung"
        List<RecommendationResult> results = engine.getRecommendations("Samsung", 0, 2000, "All Categories", 5);

        assertFalse(results.isEmpty());
        // p1 should be high because of title match
        assertEquals("p1", results.get(0).getProductId());
    }

    @Test
    public void testFilterByCategory() {
        List<RecommendationResult> results = engine.getRecommendations("Phone", 0, 2000, "Laptops", 5);
        // p3 is a laptop but contains no "Phone" keywords normally
        // Depending on TF-IDF, it might be empty if score < 0.15
        assertTrue(results.isEmpty(), "Should not find 'Phone' in 'Laptops' category if score is low");
    }
}
