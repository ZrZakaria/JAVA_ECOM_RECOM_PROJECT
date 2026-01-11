package com.recommendation.ui.core;

import com.recommendation.model.RecommendationResult;
import java.io.*;
import java.util.*;

/**
 * Singleton manager for wishlist functionality.
 * Moved to core package.
 */
public class WishlistManager {

    private static WishlistManager instance;
    private Set<String> wishlistIds;
    private Map<String, RecommendationResult> wishlistProducts;
    private List<Runnable> listeners;
    private static final String WISHLIST_FILE = "wishlist.dat";

    private WishlistManager() {
        wishlistIds = new HashSet<>();
        wishlistProducts = new HashMap<>();
        listeners = new ArrayList<>();
        load();
    }

    public static synchronized WishlistManager getInstance() {
        if (instance == null)
            instance = new WishlistManager();
        return instance;
    }

    public void add(RecommendationResult product) {
        if (product != null && product.getProductId() != null) {
            wishlistIds.add(product.getProductId());
            wishlistProducts.put(product.getProductId(), product);
            save();
            notifyListeners();
        }
    }

    public void remove(String productId) {
        wishlistIds.remove(productId);
        wishlistProducts.remove(productId);
        save();
        notifyListeners();
    }

    public boolean contains(String productId) {
        return wishlistIds.contains(productId);
    }

    public List<RecommendationResult> getAll() {
        return new ArrayList<>(wishlistProducts.values());
    }

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (Runnable l : listeners)
            l.run();
    }

    @SuppressWarnings("unchecked")
    private void load() {
        File file = new File(WISHLIST_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                wishlistIds = (Set<String>) ois.readObject();
                wishlistProducts = (Map<String, RecommendationResult>) ois.readObject();
            } catch (Exception e) {
                wishlistIds = new HashSet<>();
                wishlistProducts = new HashMap<>();
            }
        }
    }

    private void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(WISHLIST_FILE))) {
            oos.writeObject(wishlistIds);
            oos.writeObject(wishlistProducts);
        } catch (Exception e) {
        }
    }
}
