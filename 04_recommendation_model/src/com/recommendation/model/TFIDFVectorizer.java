package com.recommendation.model;

import java.util.*;

/**
 * Implements TF-IDF (Term Frequency-Inverse Document Frequency) Vectorization.
 * This is a Machine Learning technique to convert text into numerical vectors.
 * 
 * training phase: Learns vocabulary and IDF weights from corpus.
 * transform phase: Converts new text into vectors based on learned vocabulary.
 */
public class TFIDFVectorizer {

    private Map<String, Integer> vocabulary;
    private Map<String, Double> idfWeights;
    private int vocabSize;
    private boolean isTrained = false;

    // Common stop words to ignore
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "le", "la", "les", "un", "une", "des", "du", "de", "ce", "cet", "cette",
            "et", "ou", "mais", "donc", "car", "ni", "or", "a", "à", "en", "pour",
            "sur", "avec", "sans", "est", "sont", "c'est", "il", "elle", "ils", "elles",
            "que", "qui", "quoi", "dont", "où", "plus", "moins", "très", "bien", "bon"));

    public TFIDFVectorizer() {
        this.vocabulary = new HashMap<>();
        this.idfWeights = new HashMap<>();
    }

    /**
     * Train the model on a collection of documents (products).
     * Builds vocabulary and calculates IDF weights.
     */
    public void fit(List<String> documents) {
        System.out.println("Training TF-IDF model on " + documents.size() + " documents...");

        vocabulary.clear();
        idfWeights.clear();

        // 1. Build Vocabulary and count document references
        Map<String, Integer> docFrequencies = new HashMap<>();
        int totalDocuments = documents.size();

        // Temporary storage for unique words per doc
        for (String doc : documents) {
            Set<String> uniqueWords = getUniqueTokenArray(doc);
            for (String word : uniqueWords) {
                if (!vocabulary.containsKey(word)) {
                    vocabulary.put(word, vocabulary.size());
                }
                docFrequencies.put(word, docFrequencies.getOrDefault(word, 0) + 1);
            }
        }

        this.vocabSize = vocabulary.size();
        System.out.println("Vocabulary size: " + vocabSize);

        // 2. Calculate IDF Weights
        // IDF(t) = log(TotalDocuments / (Number of documents containing t))
        for (String word : docFrequencies.keySet()) {
            double idf = Math.log((double) totalDocuments / (1 + docFrequencies.get(word)));
            idfWeights.put(word, idf);
        }

        this.isTrained = true;
        System.out.println("Model training complete.");
    }

    /**
     * Transform a document into a TF-IDF vector.
     */
    public double[] transform(String text) {
        if (!isTrained) {
            throw new IllegalStateException("Model has not been trained yet. Call fit() first.");
        }

        double[] vector = new double[vocabSize];
        List<String> tokens = tokenize(text);

        if (tokens.isEmpty()) {
            return vector;
        }

        // 1. Calculate Term Frequency (TF)
        // TF(t) = (Number of times term t appears in a document) / (Total number of
        // terms in the document)
        Map<String, Integer> termCounts = new HashMap<>();
        for (String token : tokens) {
            if (vocabulary.containsKey(token)) {
                termCounts.put(token, termCounts.getOrDefault(token, 0) + 1);
            }
        }

        // 2. Compute vectors: TF * IDF
        for (Map.Entry<String, Integer> entry : termCounts.entrySet()) {
            String term = entry.getKey();
            int count = entry.getValue();
            int index = vocabulary.get(term);

            double tf = (double) count / tokens.size();
            double idf = idfWeights.getOrDefault(term, 0.0);

            vector[index] = tf * idf;
        }

        return vector;
    }

    /**
     * Helper to get unique tokens from a document (for IDF mostly).
     */
    private Set<String> getUniqueTokenArray(String text) {
        return new HashSet<>(tokenize(text));
    }

    /**
     * Tokenize text: lowercase, remove non-alphanumeric, remove stop words.
     */
    private List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        if (text == null || text.isEmpty())
            return tokens;

        // Normalize
        String normalized = text.toLowerCase().replaceAll("[^a-z0-9\\sàâäçéèêëïîôùûüÿœæ]", " ");

        for (String word : normalized.split("\\s+")) {
            if (word.length() > 2 && !STOP_WORDS.contains(word)) {
                tokens.add(word);
            }
        }
        return tokens;
    }

    public int getVocabSize() {
        return vocabSize;
    }
}
