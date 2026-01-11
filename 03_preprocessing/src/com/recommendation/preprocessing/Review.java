package com.recommendation.preprocessing;

import java.time.LocalDate;

/**
 * Represents a single customer review for a product.
 */
public class Review {
    private String author;
    private double rating; // Numeric rating 1.0-5.0
    private String title;
    private String body;
    private LocalDate date;

    public Review(String author, double rating, String title, String body, LocalDate date) {
        this.author = author != null ? author : "";
        this.rating = rating;
        this.title = title != null ? title : "";
        this.body = body != null ? body : "";
        this.date = date;
    }

    // Getters
    public String getAuthor() {
        return author;
    }

    public double getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public LocalDate getDate() {
        return date;
    }

    // Setters
    public void setAuthor(String author) {
        this.author = author;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return String.format("Review[author=%s, rating=%.1f, title=%s, date=%s]",
                author, rating, title, date);
    }
}
