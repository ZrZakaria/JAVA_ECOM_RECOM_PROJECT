package com.recommendation.model;

import java.io.Serializable;

/**
 * Represents a recommendation result with ranking information.
 */
public class RecommendationResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private String productId;
    private String title;
    private double price;
    private String imageUrl;
    private String link;
    private String description;
    private double avgRating;
    private int reviewCount;
    private double score; // Composite recommendation score
    private int rank; // Ranking position (1 = best)
    private String category;

    public RecommendationResult(String productId, String title, double price, String imageUrl,
            String link, String description, double avgRating, int reviewCount,
            double score, String category) {
        this.productId = productId;
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
        this.link = link;
        this.description = description;
        this.avgRating = avgRating;
        this.reviewCount = reviewCount;
        this.score = score;
        this.category = category;
        this.rank = 0;
    }

    // Getters
    public String getProductId() {
        return productId;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public double getScore() {
        return score;
    }

    public int getRank() {
        return rank;
    }

    public String getCategory() {
        return category;
    }

    // Setters
    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setScore(double score) {
        this.score = score;
    }

    /**
     * Get rank badge for UI display.
     */
    public String getRankBadge() {
        switch (rank) {
            case 1:
                return "🥇 BEST";
            case 2:
                return "🥈";
            case 3:
                return "🥉";
            default:
                return "#" + rank;
        }
    }

    /**
     * Check if this is the least recommended item.
     */
    public boolean isLeastRecommended() {
        return rank > 0; // Will be set externally
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %.2f€ (Score: %.2f, Rating: %.1f, Reviews: %d)",
                getRankBadge(), title, price, score, avgRating, reviewCount);
    }
}
