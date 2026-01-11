package com.example.scrapper;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // Use args as category/search URLs if provided; otherwise defaults
        String[] categories = (args != null && args.length > 0) ? args : new String[] {
           "https://www.cdiscount.com/informatique/clavier-souris-webcam/clavier/l-1070215.html?nav_menu=222:1536:Clavier%20PC"
          
        };

        List<RichProductRecord> allRows = new ArrayList<>();
        CdiscountScrapperSelenium scraper = new CdiscountScrapperSelenium();

        try {
            for (String cat : categories) {
                System.out.println("\n=== Catégorie Cdiscount : " + cat + " ===");
                try {
                    List<RichProductRecord> rows = scraper.scrape(cat);
                    if (rows == null || rows.isEmpty()) {
                        System.out.println("Aucun produit trouvé pour cette catégorie.");
                        continue;
                    }
                    System.out.println("Nombre de lignes (produits x avis) : " + rows.size());
                    allRows.addAll(rows);
                } catch (Exception e) {
                    System.out.println("Erreur lors du scraping de la catégorie : " + cat);
                    e.printStackTrace();
                }
            }
        } finally {
            // Always close the WebDriver
            System.out.println("Closing WebDriver...");
            scraper.close();
        }

        // Écriture dans un CSV au format amazon_products.csv
        CSVWriterRich.writeRichProductsToCSV(allRows, "cdiscount_claviers3.csv");
    }
}

