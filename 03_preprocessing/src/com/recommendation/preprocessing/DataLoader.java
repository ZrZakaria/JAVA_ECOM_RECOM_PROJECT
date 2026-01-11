package com.recommendation.preprocessing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to load CSV data from scraped files.
 * Handles European price formats, French decimal notation, and quoted CSV
 * fields.
 */
public class DataLoader {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Pattern PRICE_PATTERN = Pattern.compile("([0-9]+),([0-9]+)\\s*€");

    /**
     * Load a CSV file and return raw rows (each row is a String array).
     * Handles quoted fields with commas inside them.
     */
    public static List<String[]> loadCSV(String filepath) throws IOException {
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; // Skip header row
                    continue;
                }

                String[] fields = parseCSVLine(line);
                if (fields != null && fields.length >= 10) {
                    rows.add(fields);
                }
            }
        }

        return rows;
    }

    /**
     * Parse a single CSV line, handling quoted fields properly.
     * CSV format:
     * Title,Price,Link,Image,Description,ReviewAuthor,ReviewRating,ReviewTitle,ReviewBody,ReviewDate
     */
    private static String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString().trim());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }

        // Add last field
        fields.add(currentField.toString().trim());

        return fields.toArray(new String[0]);
    }

    /**
     * Parse European price format: "98,99 €" -> 98.99
     */
    public static double parsePrice(String priceStr) {
        if (priceStr == null || priceStr.isEmpty()) {
            return 0.0;
        }

        // Pre-clean: remove spaces (thousand separators)
        String cleanedInput = priceStr.replaceAll("[\\s\\u00A0]", "");

        Matcher matcher = PRICE_PATTERN.matcher(cleanedInput);
        if (matcher.find()) {
            String euros = matcher.group(1);
            String cents = matcher.group(2);
            return Double.parseDouble(euros + "." + cents);
        }

        // Fallback: try direct parsing after replacing comma
        try {
            String cleaned = priceStr.replace("€", "").replace(",", ".").trim();
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Parse French rating format: "5,0" -> 5.0
     */
    public static double parseRating(String ratingStr) {
        if (ratingStr == null || ratingStr.isEmpty()) {
            return 0.0;
        }

        try {
            // Replace French comma with dot
            String normalized = ratingStr.replace(",", ".").trim();
            double rating = Double.parseDouble(normalized);
            // Clamp between 0 and 5
            return Math.max(0.0, Math.min(5.0, rating));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Parse date format: "28/10/2024" -> LocalDate
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Generate a unique ID from a URL by taking its hash code.
     */
    public static String generateId(String url) {
        if (url == null || url.isEmpty()) {
            return "unknown";
        }
        return "prod_" + Math.abs(url.hashCode());
    }

    /**
     * Extract category from filename.
     * Example: "cdiscount_smartphones.csv" -> "smartphones"
     */
    public static String extractCategory(String filename) {
        if (filename == null) {
            return "unknown";
        }

        // Remove path and extension
        String baseName = filename.substring(filename.lastIndexOf('\\') + 1);
        baseName = baseName.substring(filename.lastIndexOf('/') + 1);
        baseName = baseName.replace(".csv", "");

        // Extract category part
        if (baseName.startsWith("cdiscount_")) {
            return baseName.substring("cdiscount_".length());
        }

        return baseName;
    }
}
