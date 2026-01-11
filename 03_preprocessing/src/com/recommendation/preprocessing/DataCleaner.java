package com.recommendation.preprocessing;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Processes raw CSV data to create aggregated Product objects.
 * Combines multiple review rows into single product entries.
 */
public class DataCleaner {

    /**
     * Process a CSV file and return a list of aggregated products.
     * 
     * @param csvFilepath Path to the CSV file
     * @return List of Product objects with aggregated reviews
     */
    public static List<Product> processCSV(String csvFilepath) throws IOException {
        // Load raw CSV rows
        List<String[]> rows = DataLoader.loadCSV(csvFilepath);

        // Extract category from filename
        String category = DataLoader.extractCategory(csvFilepath);

        // Group rows by product link (unique identifier)
        Map<String, Product> productMap = new HashMap<>();

        for (String[] row : rows) {
            // CSV columns:
            // Title,Price,Link,Image,Description,ReviewAuthor,ReviewRating,ReviewTitle,ReviewBody,ReviewDate
            String title = row[0];
            String priceStr = row[1];
            String link = row[2];
            String image = row[3];
            String description = row[4];
            String reviewAuthor = row[5];
            String reviewRatingStr = row[6];
            String reviewTitle = row[7];
            String reviewBody = row[8];
            String reviewDateStr = row[9];

            // Parse product data
            String productId = DataLoader.generateId(link);
            double price = DataLoader.parsePrice(priceStr);

            // Get or create product
            Product product = productMap.get(link);
            if (product == null) {
                product = new Product(productId, title, price, link, image, description, category);
                productMap.put(link, product);
            }

            // Add review if it exists
            if (reviewAuthor != null && !reviewAuthor.isEmpty()) {
                double rating = DataLoader.parseRating(reviewRatingStr);
                LocalDate date = DataLoader.parseDate(reviewDateStr);
                Review review = new Review(reviewAuthor, rating, reviewTitle, reviewBody, date);
                product.addReview(review);
            }
        }

        return new ArrayList<>(productMap.values());
    }

    /**
     * Process multiple CSV files and combine into a single list.
     */
    public static List<Product> processMultipleCSVs(String... filepaths) throws IOException {
        List<Product> allProducts = new ArrayList<>();

        for (String filepath : filepaths) {
            System.out.println("Processing: " + filepath);
            List<Product> products = processCSV(filepath);
            allProducts.addAll(products);
            System.out.println("  -> Loaded " + products.size() + " products");
        }

        return allProducts;
    }

    /**
     * Clean and normalize product text fields.
     */
    public static String cleanText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        // Remove extra whitespace
        text = text.trim().replaceAll("\\s+", " ");

        // Remove control characters
        text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");

        return text;
    }

    /**
     * Save processed products to a simple text format for inspection.
     */
    public static void saveToTextFile(List<Product> products, String outputPath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.println("=".repeat(80));
            writer.println("PREPROCESSED PRODUCTS");
            writer.println("Total products: " + products.size());
            writer.println("=".repeat(80));
            writer.println();

            for (Product p : products) {
                writer.println("-".repeat(80));
                writer.println("ID: " + p.getId());
                writer.println("Title: " + p.getTitle());
                writer.println("Price: " + String.format("%.2f €", p.getPrice()));
                writer.println("Category: " + p.getCategory());
                writer.println("Reviews: " + p.getReviewCount());
                writer.println("Avg Rating: " + String.format("%.1f / 5.0", p.getAvgRating()));
                writer.println("Link: " + p.getLink());
                writer.println("Image: " + p.getImageUrl());
                writer.println();
                writer.println("Description:");
                String desc = p.getDescription();
                if (desc.length() > 200) {
                    writer.println(desc.substring(0, 200) + "...");
                } else {
                    writer.println(desc);
                }
                writer.println();

                if (p.getReviewCount() > 0) {
                    writer.println("Sample Reviews:");
                    int count = 0;
                    for (Review r : p.getReviews()) {
                        if (count >= 3)
                            break;
                        writer.println("  - [" + r.getRating() + "/5] " + r.getTitle());
                        if (r.getBody().length() > 100) {
                            writer.println("    " + r.getBody().substring(0, 100) + "...");
                        } else {
                            writer.println("    " + r.getBody());
                        }
                        count++;
                    }
                }
                writer.println();
            }
        }
    }
}
