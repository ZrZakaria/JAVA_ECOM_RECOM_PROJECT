package com.example.scrapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVWriterRich {

    public static void writeRichProductsToCSV(List<RichProductRecord> rows, String filename) {
        if (!filename.toLowerCase().endsWith(".csv")) {
            filename += ".csv";
        }

        File file = new File(filename);

        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write("Title,Price,Link,Image,Description,ReviewAuthor,ReviewRating,ReviewTitle,ReviewBody,ReviewDate\n");

            for (RichProductRecord r : rows) {
                writer.write(
                        esc(r.getTitle()) + "," +
                        esc(r.getPrice()) + "," +
                        esc(r.getLink()) + "," +
                        esc(r.getImage()) + "," +
                        esc(r.getDescription()) + "," +
                        esc(r.getReviewAuthor()) + "," +
                        esc(r.getReviewRating()) + "," +
                        esc(r.getReviewTitle()) + "," +
                        esc(r.getReviewBody()) + "," +
                        esc(r.getReviewDate()) + "\n"
                );
            }

            System.out.println("Rich CSV created: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error writing rich CSV");
            e.printStackTrace();
        }
    }

    private static String esc(String s) {
        if (s == null) s = "";
        String cleaned = s.replace("\r", " ").replace("\n", " ").replace("\t", " ").replace("\"", "'");
        return "\"" + cleaned + "\"";
    }
}
