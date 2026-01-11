package com.recommendation.ui.core;

import com.recommendation.model.RecommendationResult;
import java.util.*;

/**
 * Singleton manager to track products selected for comparison.
 * Moved to core package.
 */
public class ComparisonManager {

    private static ComparisonManager instance;
    private Map<String, RecommendationResult> selectedProducts;
    private List<Runnable> listeners;
    public static final int MAX_SELECTION = 3;

    private ComparisonManager() {
        selectedProducts = new HashMap<>();
        listeners = new ArrayList<>();
    }

    public static synchronized ComparisonManager getInstance() {
        if (instance == null)
            instance = new ComparisonManager();
        return instance;
    }

    public boolean add(RecommendationResult product) {
        if (selectedProducts.size() >= MAX_SELECTION)
            return false;
        selectedProducts.put(product.getProductId(), product);
        notifyListeners();
        return true;
    }

    public void remove(String productId) {
        selectedProducts.remove(productId);
        notifyListeners();
    }

    public boolean contains(String productId) {
        return selectedProducts.containsKey(productId);
    }

    public List<RecommendationResult> getSelected() {
        return new ArrayList<>(selectedProducts.values());
    }

    public void clear() {
        selectedProducts.clear();
        notifyListeners();
    }

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (Runnable l : listeners)
            l.run();
    }
}
