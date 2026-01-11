package com.recommendation.preprocessing;

import java.io.IOException;
import java.util.List;

/**
 * Main entry point for the preprocessing module.
 * Loads raw CSV data, aggregates products, and outputs preprocessed data.
 */
public class PreprocessingMain {

    public static void main(String[] args) {
        System.out.println("===============================================");
        System.out.println("  PRODUCT DATA PREPROCESSING");
        System.out.println("===============================================");
        System.out.println();

        try {
            // Define base data directory
            String dataDir = "..\\02_data_collection\\raw\\";

            // Process all CSV files - Products separated
            String[] csvFiles = {
                    dataDir + "cdiscount_smartphones.csv",
                    dataDir + "cdiscount_claviers.csv",
                    dataDir + "cdiscount_casques_bluetooth.csv",
                    dataDir + "cdiscount_ordinateurs.csv"
            };

            List<Product> products = DataCleaner.processMultipleCSVs(csvFiles);

            System.out.println();
            System.out.println("===============================================");
            System.out.println("  PREPROCESSING COMPLETE");
            System.out.println("===============================================");
            System.out.println("Total products loaded: " + products.size());
            System.out.println();

            // Calculate statistics
            int totalReviews = 0;
            double avgPrice = 0;
            double avgRating = 0;
            int productsWithReviews = 0;

            for (Product p : products) {
                totalReviews += p.getReviewCount();
                avgPrice += p.getPrice();
                if (p.getReviewCount() > 0) {
                    avgRating += p.getAvgRating();
                    productsWithReviews++;
                }
            }

            avgPrice /= products.size();
            if (productsWithReviews > 0) {
                avgRating /= productsWithReviews;
            }

            System.out.println("Statistics:");
            System.out.println("  - Total reviews: " + totalReviews);
            System.out.println("  - Average price: " + String.format("%.2f €", avgPrice));
            System.out.println("  - Products with reviews: " + productsWithReviews);
            System.out.println("  - Average rating: " + String.format("%.1f / 5.0", avgRating));
            System.out.println();

            // Save to output file
            String outputPath = "output\\preprocessed_products.txt";
            DataCleaner.saveToTextFile(products, outputPath);
            System.out.println("Preprocessed data saved to: " + outputPath);
            System.out.println();

            // Show some examples
            System.out.println("Sample products:");
            int count = 0;
            for (Product p : products) {
                if (count >= 5)
                    break;
                System.out.println("  " + (count + 1) + ". " + p);
                count++;
            }

        } catch (IOException e) {
            System.err.println("ERROR: Failed to process CSV files");
            e.printStackTrace();
        }
    }
}
