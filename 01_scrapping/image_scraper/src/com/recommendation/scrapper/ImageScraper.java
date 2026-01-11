package com.recommendation.scrapper;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Hybrid Image Scraper.
 * Combines high-speed HTTP downloading with robust Selenium fallback.
 * 
 * Strategy:
 * 1. Try fast Java HttpClient download (mimicking browser headers).
 * 2. If blocked (403/503), fall back to Selenium to bypass protection.
 * 
 * Output: ../../02_data_collection/images/
 */
public class ImageScraper {

    private static final String CSV_DIR = "../../02_data_collection/raw";
    private static final String OUTPUT_DIR = "../../02_data_collection/images"; // Moved to data collection
    private static final int MAX_THREADS = 8;
    private static final int HTTP_TIMEOUT = 10;

    private final ExecutorService executor;
    private final HttpClient httpClient;
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failCount = new AtomicInteger(0);
    private final AtomicInteger skipCount = new AtomicInteger(0);
    private final AtomicInteger fallbackCount = new AtomicInteger(0);

    private final Set<String> uniqueProducts = new HashSet<>();

    public ImageScraper() {
        this.executor = Executors.newFixedThreadPool(MAX_THREADS);
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(HTTP_TIMEOUT))
                .build();
    }

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   HYBRID IMAGE DOWNLOADER (HTTP + SELENIUM)");
        System.out.println("=================================================");
        System.out.println();

        new ImageScraper().run();
    }

    public void run() {
        File outputDir = new File(OUTPUT_DIR);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File csvDir = new File(CSV_DIR);
        File[] csvFiles = csvDir.listFiles((dir, name) -> name.endsWith(".csv"));

        if (csvFiles == null || csvFiles.length == 0) {
            System.err
                    .println("No CSV files found in: " + (csvDir.exists() ? csvDir.getAbsolutePath() : "unknown path"));
            return;
        }

        System.out.println("Reading from: " + csvDir.getName());
        System.out.println("Saving to:    " + outputDir.getAbsolutePath());
        System.out.println();

        // Collect all unique image URLs keyed by Product ID
        // Map<ProductID, ImageURL>
        Map<String, String> idToUrlMap = new LinkedHashMap<>();

        for (File csvFile : csvFiles) {
            try {
                collectImagesFromCSV(csvFile, idToUrlMap);
            } catch (IOException e) {
                System.err.println("Error reading " + csvFile.getName() + ": " + e.getMessage());
            }
        }

        System.out.println("Total unique products in CSV: " + uniqueProducts.size());
        System.out.println("Products with Image URLs:     " + idToUrlMap.size());

        // Filter out already downloaded images
        List<Map.Entry<String, String>> toDownload = new ArrayList<>();
        int alreadyExists = 0;

        for (Map.Entry<String, String> entry : idToUrlMap.entrySet()) {
            File f = new File(OUTPUT_DIR, entry.getKey() + ".jpg");
            if (f.exists() && f.length() > 0) {
                alreadyExists++;
            } else {
                toDownload.add(entry);
            }
        }

        System.out.println("Already downloaded:           " + alreadyExists);
        System.out.println("To download:                  " + toDownload.size());

        if (toDownload.isEmpty()) {
            printSummary(idToUrlMap.size(), alreadyExists);
            return;
        }

        System.out.println("\nStarting download with " + MAX_THREADS + " threads...");

        List<Future<?>> futures = new ArrayList<>();
        int index = 0;
        int total = toDownload.size();

        for (Map.Entry<String, String> entry : toDownload) {
            final int idx = ++index;
            final String productId = entry.getKey();
            final String url = entry.getValue();

            futures.add(executor.submit(() -> processImage(url, productId, idx, total)));
        }

        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (Exception ignored) {
            }
        }

        executor.shutdown();
        printSummary(idToUrlMap.size(), alreadyExists + successCount.get());
    }

    private void printSummary(int totalDownloadable, int totalDownloaded) {
        System.out.println("\n=================================================");
        System.out.println("   VERIFICATION SUMMARY");
        System.out.println("=================================================");
        System.out.println("Total Products Scanned: " + uniqueProducts.size());
        System.out.println("Missing/Empty URLs:     " + (uniqueProducts.size() - totalDownloadable));
        System.out.println("Downloadable Images:    " + totalDownloadable);
        System.out.println("Successfully Verified:  " + totalDownloaded);

        double coverage = uniqueProducts.size() > 0 ? (totalDownloaded * 100.0 / uniqueProducts.size()) : 0;
        System.out.printf("Final Coverage:         %.1f%%\n", coverage);
        System.out.println("=================================================");
    }

    private void processImage(String url, String productId, int index, int total) {
        String filename = productId + ".jpg";
        File outputFile = new File(OUTPUT_DIR, filename);

        // Attempt 1: Fast HTTP
        if (downloadHttp(url, outputFile)) {
            successCount.incrementAndGet();
            System.out.println(String.format("[%d/%d] HTTP OK: %s", index, total, filename));
            return;
        }

        // Attempt 2: Selenium Fallback
        System.out.println(String.format("[%d/%d] HTTP Failed, trying Selenium: %s", index, total, filename));
        fallbackCount.incrementAndGet();

        if (downloadSelenium(url, outputFile)) {
            successCount.incrementAndGet();
            System.out.println(String.format("[%d/%d] Selenium OK: %s", index, total, filename));
        } else {
            failCount.incrementAndGet();
            System.out.println(String.format("[%d/%d] FAILED: %s", index, total, filename));
        }
    }

    private boolean downloadHttp(String imageUrl, File outputFile) {
        try {
            String encodedUrl = imageUrl.replace(" ", "%20");
            URI uri = new URI(encodedUrl);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(Duration.ofSeconds(HTTP_TIMEOUT))
                    .header("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36")
                    .header("Accept", "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8")
                    .header("Referer", "https://www.cdiscount.com/")
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200 && isImageData(response.body())) {
                Files.write(outputFile.toPath(), response.body());
                return true;
            }
        } catch (Exception e) {
            // Log debug if needed: e.getMessage()
        }
        return false;
    }

    private boolean downloadSelenium(String imageUrl, File outputFile) {
        WebDriver driver = null;
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox", "--window-size=1920,1080");
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

            driver = new ChromeDriver(options);
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));

            driver.get(imageUrl);

            org.openqa.selenium.OutputType<byte[]> outputType = org.openqa.selenium.OutputType.BYTES;
            byte[] screenshot = ((org.openqa.selenium.TakesScreenshot) driver).getScreenshotAs(outputType);

            if (screenshot.length > 0) {
                Files.write(outputFile.toPath(), screenshot);
                return true;
            }
        } catch (Exception e) {
            // Selenium failed
        } finally {
            if (driver != null) {
                try {
                    driver.quit();
                } catch (Exception ignored) {
                }
            }
        }
        return false;
    }

    private boolean isImageData(byte[] data) {
        if (data == null || data.length < 4)
            return false;
        int b0 = data[0] & 0xFF, b1 = data[1] & 0xFF, b2 = data[2] & 0xFF;
        return (b0 == 0xFF && b1 == 0xD8 && b2 == 0xFF) || // JPEG
                (b0 == 0x89 && b1 == 0x50 && b2 == 0x4E) || // PNG
                (b0 == 0x47 && b1 == 0x49 && b2 == 0x46) || // GIF
                (b0 == 0x52 && b1 == 0x49 && b2 == 0x46); // WebP
    }

    private void collectImagesFromCSV(File csvFile, Map<String, String> idToUrlMap) throws IOException {
        Set<String> seenInFile = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] fields = parseCSVLine(line);
                if (fields.length >= 4) {
                    String link = fields[2];
                    String imageUrl = fields[3];

                    if (link != null && !link.isEmpty() && !seenInFile.contains(link)) {
                        seenInFile.add(link);
                        uniqueProducts.add(link);

                        if (imageUrl != null && !imageUrl.isEmpty() && imageUrl.startsWith("http")) {
                            idToUrlMap.put(generateId(link), imageUrl);
                        }
                    }
                }
            }
        }
    }

    private String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"')
                inQuotes = !inQuotes;
            else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString().trim());
                currentField = new StringBuilder();
            } else
                currentField.append(c);
        }
        fields.add(currentField.toString().trim());
        return fields.toArray(new String[0]);
    }

    private String generateId(String url) {
        return (url == null || url.isEmpty()) ? "unknown_" + System.currentTimeMillis()
                : "prod_" + Math.abs(url.hashCode());
    }
}
