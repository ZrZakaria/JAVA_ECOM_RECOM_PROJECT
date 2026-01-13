package com.recommendation.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A Probabilistic Machine Learning Classifier implementing Multinomial Naive
 * Bayes.
 * 
 * Algorithm:
 * P(Class | Doc) = P(Class) * Product( P(Word | Class) )
 * 
 * Features:
 * - Laplace Smoothing (k=1) to handle unknown words.
 * - Log-probabilities to prevent floating point underflow.
 * - Hardcoded seed dataset for "Cold Start" training.
 */
public class NaiveBayesClassifier {

    private Map<String, Integer> positiveWordCounts = new HashMap<>();
    private Map<String, Integer> negativeWordCounts = new HashMap<>();
    private int totalPositiveWords = 0;
    private int totalNegativeWords = 0;
    private int totalPositiveDocs = 0;
    private int totalNegativeDocs = 0;
    private Set<String> vocabulary = new HashSet<>();

    public NaiveBayesClassifier() {
        // Train on seed data immediately upon instantiation
        trainWithSeedData();
    }

    /**
     * Train the model with a hardcoded dataset of labeled reviews.
     * This ensures the AI has a baseline understanding before reading real data.
     */
    private void trainWithSeedData() {
        // POSITIVE EXAMPLES
        train("The battery life is amazing and lasts all day", true);
        train("Fast shipping and excellent quality product", true);
        train("I love this phone it works perfectly", true);
        train("Great value for the price recommmended", true);
        train("Screen is beautiful and very bright", true);
        train("Sound quality is superb and crisp", true);
        train("Easy to setup and very user friendly", true);
        train("Best purchase I have made this year", true);
        train("Customer service was helpful and polite", true);
        train("Durable and well built construction", true);
        train("Super fast processor and lots of ram", true);
        train("Camera takes stunning photos at night", true);
        train("Very happy with this performance", true);
        train("Exceeded my expectations in every way", true);
        train("Highly recommend to anyone looking for quality", true);

        // NEGATIVE EXAMPLES
        train("The screen cracked after one day of use", false);
        train("Terrible battery life drains in an hour", false);
        train("Slow shipping and arrived damaged", false);
        train("Waste of money do not buy this garbage", false);
        train("Customer service was rude and unhelpful", false);
        train("Product stopped working after a week", false);
        train("Poor quality materials feel very cheap", false);
        train("Overheating issues when playing games", false);
        train("The sound is muffled and quiet", false);
        train("Hard to use and keeping freezing", false);
        train("Worst experience ever avoided this brand", false);
        train("Not worth the price at all rip off", false);
        train("Buttons are loose and rattle", false);
        train("Software is buggy and crashes often", false);
        train("False advertising does not have features", false);
    }

    /**
     * Train on a single document.
     * 
     * @param text       The review text.
     * @param isPositive True if positive, False if negative.
     */
    public void train(String text, boolean isPositive) {
        if (text == null || text.isEmpty())
            return;

        List<String> tokens = tokenize(text);

        if (isPositive) {
            totalPositiveDocs++;
            for (String token : tokens) {
                positiveWordCounts.put(token, positiveWordCounts.getOrDefault(token, 0) + 1);
                totalPositiveWords++;
                vocabulary.add(token);
            }
        } else {
            totalNegativeDocs++;
            for (String token : tokens) {
                negativeWordCounts.put(token, negativeWordCounts.getOrDefault(token, 0) + 1);
                totalNegativeWords++;
                vocabulary.add(token);
            }
        }
    }

    /**
     * Predict sentiment probability.
     * 
     * @return A value between -1.0 (Negative) and 1.0 (Positive).
     */
    public double predict(String text) {
        if (text == null || text.isEmpty())
            return 0.0;

        List<String> tokens = tokenize(text);
        int totalDocs = totalPositiveDocs + totalNegativeDocs;
        int vocabSize = vocabulary.size();

        // 1. Calculate Prior Probabilities P(Class)
        // Use logs to prevent underflow: log(A*B) = log(A) + log(B)
        double logProbPos = Math.log((double) totalPositiveDocs / totalDocs);
        double logProbNeg = Math.log((double) totalNegativeDocs / totalDocs);

        // 2. Calculate Likelihoods P(Word | Class)
        for (String token : tokens) {
            if (!vocabulary.contains(token))
                continue; // Skip unknown words

            // Laplace Smoothing: (Count + 1) / (Total + VocabSize)
            double probWordPos = (double) (positiveWordCounts.getOrDefault(token, 0) + 1)
                    / (totalPositiveWords + vocabSize);

            double probWordNeg = (double) (negativeWordCounts.getOrDefault(token, 0) + 1)
                    / (totalNegativeWords + vocabSize);

            logProbPos += Math.log(probWordPos);
            logProbNeg += Math.log(probWordNeg);
        }

        // 3. Convert Log-Odds to Probability (-1 to 1 scale)
        // If Pos > Neg, score is positive.
        // We normalize the difference to get a confidence score.
        double diff = logProbPos - logProbNeg;

        // Sigmoid-like squashing function to map (-inf, inf) to (-1, 1)
        // Logistic function: 2 / (1 + e^-x) - 1
        return (2.0 / (1.0 + Math.exp(-diff))) - 1.0;
    }

    private List<String> tokenize(String text) {
        // Simple tokenizer: remove non-word chars, lowercase, split by whitespace
        String clean = text.replaceAll("[^a-zA-Z\\s]", "").toLowerCase();
        return Arrays.asList(clean.split("\\s+"));
    }
}
