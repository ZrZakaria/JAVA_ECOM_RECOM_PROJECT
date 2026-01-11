package com.recommendation.preprocessing;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a product with aggregated information and reviews.
 */
public class Product {
    private String id; // Generated from URL hash
    private String title;
    private double price; // Parsed numeric value in euros
    private String link;
    private String imageUrl;
    private String description;
    private String category; // Extracted from filename/URL
    private List<Review> reviews;
    private double avgRating;
    private int reviewCount;

    public Product() {
        this.reviews = new ArrayList<>();
        this.avgRating = 0.0;
        this.reviewCount = 0;
    }

    public Product(String id, String title, double price, String link,
            String imageUrl, String description, String category) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.link = link;
        this.imageUrl = imageUrl;
        this.description = description != null ? description : "";
        this.category = category;
        this.reviews = new ArrayList<>();
        this.avgRating = 0.0;
        this.reviewCount = 0;
    }

    // Add a review and recalculate average rating
    public void addReview(Review review) {
        reviews.add(review);
        reviewCount = reviews.size();

        // Recalculate average rating
        if (reviewCount > 0) {
            double sum = 0;
            for (Review r : reviews) {
                sum += r.getRating();
            }
            avgRating = sum / reviewCount;
        }
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public String getLink() {
        return link;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return String.format("Product[id=%s, title=%s, price=%.2f€, reviews=%d, avgRating=%.1f]",
                id, title, price, reviewCount, avgRating);
    }
}
