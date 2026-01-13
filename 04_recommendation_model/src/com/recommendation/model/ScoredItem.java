package com.recommendation.model;

import java.io.Serializable;

/**
 * A Generic class that wraps any item of type T with a numerical score.
 * Demonstrates the use of Generics in Java.
 * 
 * @param <T> The type of item being scored (e.g., Product, Category).
 */
public class ScoredItem<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private T item;
    private double score;

    public ScoredItem(T item, double score) {
        this.item = item;
        this.score = score;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "ScoredItem{item=" + item + ", score=" + score + "}";
    }
}
