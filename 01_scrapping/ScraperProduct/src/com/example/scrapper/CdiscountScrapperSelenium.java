package com.example.scrapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Selenium-based Cdiscount scraper that renders JavaScript before scraping.
 * Handles dynamic content and anti-bot protections.
 */
public class CdiscountScrapperSelenium {

    // Same selectors as jsoup version, now on rendered DOM
    private static final List<String> LIST_ITEM_SELECTORS = Arrays.asList(
            "article.sc-1aa1prr-8.kxeHhZ.offerWrapper[data-e2e=offer-item]",
            "article.sc-1aa1prr-8.offerWrapper[data-e2e=offer-item]",
            "article[data-e2e=offer-item]",
            "a.sc-1aa1prr-20 article[data-e2e=offer-item]",
            "div.search-result-block article"
    );

    private static final List<String> TITLE_SELECTORS = Arrays.asList(
            "h2[data-e2e=lplr-title]",
            ".title",
            "h2.title",
            "h3"
    );

    private static final List<String> PRICE_SELECTORS = Arrays.asList(
            "div[data-e2e=lplr-price] span.sc-e4stwg-1",
            "div[data-e2e=lplr-price] span",
            "span[class*=price]",
            ".price"
    );

    private static final List<String> IMAGE_SELECTORS = Arrays.asList(
            "img[src]",
            "img[data-src]",
            "img.productImage"
    );

    private static final List<String> DESCRIPTION_SELECTORS = Arrays.asList(
            "#MarketingLongDescription",
            "#MarketingDescriptionSyndicate",
            "#LoadBeeContent",
            "#wc-power-page",
            "#BoostItContent",
            "#ourOpinion"
    );

    private static final List<String> REVIEWS_LIST_SELECTORS = Arrays.asList(
            "li.c-customer-reviews__item",
            "ul.c-customer-reviews__list > li",
            "ul#productReviews > li",
            "ul.avis > li"
    );

    private static final List<String> REVIEW_AUTHOR_SELECTORS = Arrays.asList(
            "span.c-customer-review__author",
            "[itemprop=author]",
            ".user-pseudo",
            ".author"
    );

    private static final List<String> REVIEW_RATING_SELECTORS = Arrays.asList(
            "span.c-stars-result[data-score]",
            "[itemprop=ratingValue]",
            "[aria-label*=étoile]",
            ".rating"
    );

    private static final List<String> REVIEW_TITLE_SELECTORS = Arrays.asList(
            "div.c-customer-review__header h3",
            "h3",
            ".review-title",
            "h4"
    );

    private static final List<String> REVIEW_BODY_SELECTORS = Arrays.asList(
            "div.c-customer-review__content div.o-text p",
            "div.o-text p",
            "[itemprop=reviewBody]",
            ".review-content"
    );

    private static final List<String> REVIEW_DATE_SELECTORS = Arrays.asList(
            "span.ratingPublishDetails",
            "[itemprop=datePublished]",
            "time",
            ".review-date"
    );

    private WebDriver driver;

    public CdiscountScrapperSelenium() {
        initDriver();
    }

    private void initDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--start-maximized",
                "--disable-blink-features=AutomationControlled",
                "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36",
                "--disable-gpu",
                "--no-sandbox",
                "--disable-dev-shm-usage"
        );
        
        try {
            this.driver = new ChromeDriver(options);
            this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        } catch (Exception e) {
            System.err.println("Error initializing ChromeDriver. Ensure chromedriver is in PATH or configure webdriver.chrome.driver property.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<RichProductRecord> scrape(String searchUrl) {
        List<RichProductRecord> out = new ArrayList<>();
        try {
            System.out.println("Loading URL with Selenium: " + searchUrl);
            driver.get(searchUrl);

            // Wait for product cards to load
            try {
                new WebDriverWait(driver, Duration.ofSeconds(15))
                        .until(d -> d.findElements(By.cssSelector("article[data-e2e=offer-item]")).size() > 0);
            } catch (Exception e) {
                System.out.println("Timeout waiting for products; proceeding with current DOM");
            }

            // Get rendered HTML and parse with jsoup
            String pageSource = driver.getPageSource();
            Document doc = Jsoup.parse(pageSource);

            Elements productCards = findFirstNonEmpty(doc, LIST_ITEM_SELECTORS);
            if (productCards == null || productCards.isEmpty()) {
                System.out.println("No product cards found after JS rendering");
                dumpHtml("cdiscount_list_selenium", pageSource);
                return out;
            }

            System.out.println("Found " + productCards.size() + " product cards");

            for (Element card : productCards) {
                // Extract anchor - check if parent is anchor or find anchor within card
                Element anchor = null;
                if (card.parent() != null && card.parent().tagName().equalsIgnoreCase("a")) {
                    anchor = card.parent();
                } else {
                    anchor = card.selectFirst("a.sc-1aa1prr-20[href]");
                    if (anchor == null) {
                        anchor = card.selectFirst("a[href]");
                    }
                }
                
                String title = textFrom(card, TITLE_SELECTORS);
                String price = textFrom(card, PRICE_SELECTORS);
                
                // Extract link from anchor
                String link = "";
                if (anchor != null) {
                    link = anchor.attr("href");
                    if (link.isEmpty()) link = anchor.attr("data-href");
                    if (link.isEmpty()) link = anchor.attr("data-url");
                }
                String image = imageFrom(card);

                // Normalize link (handle protocol-relative and absolute first)
                if (link != null && !link.isEmpty()) {
                    if (link.startsWith("http")) {
                        // already absolute
                    } else if (link.startsWith("//")) {
                        link = "https:" + link;
                    } else if (link.startsWith("/")) {
                        link = "https://www.cdiscount.com" + link;
                    } else if (link.startsWith("www.")) {
                        link = "https://" + link;
                    } else {
                        link = "https://www.cdiscount.com/" + link;
                    }
                }

                if (title.isEmpty()) continue; // Skip cards with no title

                String description = fetchDescription(link);
                List<RichProductRecord> rows = fetchReviews(link);

                if (rows.isEmpty()) {
                    out.add(new RichProductRecord(title, price, link, image, description,
                            "", "", "", "", ""));
                } else {
                    for (RichProductRecord r : rows) {
                        out.add(new RichProductRecord(title, price, link, image, description,
                                r.getReviewAuthor(), r.getReviewRating(), r.getReviewTitle(), r.getReviewBody(), r.getReviewDate()));
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error scraping with Selenium: " + e.getMessage());
            e.printStackTrace();
        }
        return out;
    }

    public void close() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.out.println("Error closing WebDriver: " + e.getMessage());
            }
        }
    }

   private String fetchDescription(String productUrl) {
        if (productUrl == null || productUrl.isEmpty()) return "";
        WebDriver tempDriver = null;
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized", "--disable-gpu", "--no-sandbox");
            tempDriver = new ChromeDriver(options);
            tempDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            tempDriver.get(productUrl);

            // Try to expand accordion panels if collapsed
            try {
                List<By> headerCandidates = Arrays.asList(
                        By.id("ProductSheetAccordion-header-1"),
                        By.cssSelector(".js-accordion__header"),
                        By.cssSelector("[aria-controls^=ProductSheetAccordion-content]"),
                        By.cssSelector("button[aria-controls*=Accordion-content], a[aria-controls*=Accordion-content]")
                );
                for (By by : headerCandidates) {
                    try {
                        org.openqa.selenium.WebElement he = tempDriver.findElement(by);
                        if (he != null) { he.click(); Thread.sleep(200); break; }
                    } catch (Exception ignored) { }
                }
            } catch (Exception ignored) { }

            // --- AJOUT MAJEUR : Scroll Progressif ---
            try {
                JavascriptExecutor js = (JavascriptExecutor) tempDriver;
                // Scroll progressif pour forcer le chargement des images et textes
                for (int i = 0; i < 5; i++) {
                    js.executeScript("window.scrollBy(0, 700)");
                    Thread.sleep(400);
                }
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                Thread.sleep(1000);
            } catch (Exception ex) {
                System.out.println("Erreur durant le scroll (Description) : " + ex.getMessage());
            }

            // Wait for any known description containers to be present
            try {
                new WebDriverWait(tempDriver, Duration.ofSeconds(15))
                        .until(d -> d.findElements(By.cssSelector(
                                "#MarketingLongDescription, #MarketingDescriptionSyndicate, #LoadBeeContent, #wc-power-page, #BoostItContent, #ourOpinion"
                        )).size() > 0);
            } catch (Exception ignored) { 
                 // On laisse passer, peut-être que Jsoup trouvera quand même quelque chose
            }

            String pageSource = tempDriver.getPageSource();
            Document doc = Jsoup.parse(pageSource);

            for (String sel : DESCRIPTION_SELECTORS) {
                Element node = doc.selectFirst(sel);
                if (node != null) {
                    String t = node.text().trim();
                    if (!t.isEmpty()) return t;
                }
            }
            // Save HTML for troubleshooting when description not found
            dumpHtml("cdiscount_product_desc_selenium", pageSource);
        } catch (Exception e) {
            // --- CORRECTION : On imprime l'erreur ---
            System.err.println("ERREUR CRITIQUE dans fetchDescription : " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (tempDriver != null) {
                try {
                    tempDriver.quit();
                } catch (Exception ignored) { }
            }
        }
        return "";
    }

    private List<RichProductRecord> fetchReviews(String productUrl) {
        List<RichProductRecord> rows = new ArrayList<>();
        if (productUrl == null || productUrl.isEmpty()) return rows;

        WebDriver tempDriver = null;
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized", "--disable-gpu", "--no-sandbox");
            
            // --- AJOUT : Mode Headless souvent plus stable pour les popups ---
            // options.addArguments("--headless=new"); 

            tempDriver = new ChromeDriver(options);
            tempDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            tempDriver.get(productUrl);

            // --- AJOUT MAJEUR : Simulation du Scroll pour Lazy Loading ---
            try {
                JavascriptExecutor js = (JavascriptExecutor) tempDriver;
                // On descend progressivement par sauts de 600px
                for (int i = 0; i < 6; i++) {
                    js.executeScript("window.scrollBy(0, 600)");
                    Thread.sleep(500); // Pause pour laisser le contenu charger
                }
                // Scroll final tout en bas
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                Thread.sleep(1000); // Pause finale
            } catch (Exception ex) {
                System.out.println("Erreur durant le scroll (Reviews) : " + ex.getMessage());
            }

            // Wait for reviews
            try {
                new WebDriverWait(tempDriver, Duration.ofSeconds(10))
                        .until(d -> d.findElements(By.cssSelector("li.c-customer-reviews__item, ul.avis > li")).size() > 0);
            } catch (Exception e) { 
                System.out.println("Timeout : Les avis ne sont pas apparus pour " + productUrl);
            }

            String pageSource = tempDriver.getPageSource();
            Document doc = Jsoup.parse(pageSource);

            Elements reviewNodes = null;
            for (String sel : REVIEWS_LIST_SELECTORS) {
                reviewNodes = doc.select(sel);
                if (reviewNodes != null && !reviewNodes.isEmpty()) break;
            }

            if (reviewNodes == null || reviewNodes.isEmpty()) {
                // System.out.println("Aucun nœud d'avis trouvé après parsing Jsoup.");
                return rows;
            }

            for (Element r : reviewNodes) {
                String author = textFrom(r, REVIEW_AUTHOR_SELECTORS);
                String rating = extractRating(r);
                String title = textFrom(r, REVIEW_TITLE_SELECTORS);
                String body = textFrom(r, REVIEW_BODY_SELECTORS);
                String date = textFrom(r, REVIEW_DATE_SELECTORS);
                rows.add(new RichProductRecord("", "", "", "", "", author, rating, title, body, date));
            }
        } catch (Exception e) {
            // --- CORRECTION : On imprime l'erreur au lieu de l'ignorer ---
            System.err.println("ERREUR CRITIQUE dans fetchReviews : " + e.getMessage());
            e.printStackTrace(); 
        } finally {
            if (tempDriver != null) {
                try {
                    tempDriver.quit();
                } catch (Exception ignored) { }
            }
        }
        return rows;
    }

    private static Elements findFirstNonEmpty(Document doc, List<String> selectors) {
        for (String s : selectors) {
            Elements els = doc.select(s);
            if (els != null && !els.isEmpty()) return els;
        }
        return new Elements();
    }

    private static String textFrom(Element root, List<String> selectors) {
        for (String s : selectors) {
            Element el = root.selectFirst(s);
            if (el != null) {
                String t = el.text().trim();
                if (!t.isEmpty()) return t;
            }
        }
        return "";
    }

    private static String imageFrom(Element root) {
        for (String s : IMAGE_SELECTORS) {
            Element el = root.selectFirst(s);
            if (el != null) {
                String src = el.hasAttr("data-src") ? el.attr("data-src") : el.attr("src");
                if (src != null && !src.trim().isEmpty()) {
                    src = src.trim();
                    if (src.startsWith("//")) return "https:" + src;
                    if (!src.startsWith("http")) return "https://www.cdiscount.com" + (src.startsWith("/") ? "" : "/") + src;
                    return src;
                }
            }
        }
        return "";
    }

    private static String extractRating(Element reviewElement) {
        // Try to find data-score attribute (Cdiscount uses 0-100 scale)
        for (String sel : REVIEW_RATING_SELECTORS) {
            Element el = reviewElement.selectFirst(sel);
            if (el != null && el.hasAttr("data-score")) {
                try {
                    int score = Integer.parseInt(el.attr("data-score"));
                    // Convert 0-100 to 0-5 stars
                    double stars = score / 20.0;
                    return String.format("%.1f", stars);
                } catch (NumberFormatException ignored) { }
            }
        }
        // Fallback to text parsing
        String raw = textFrom(reviewElement, REVIEW_RATING_SELECTORS);
        return parseRating(raw);
    }

    private static String parseRating(String raw) {
        if (raw == null) return "";
        String t = raw.replace(',', '.');
        String digits = t.replaceAll("[^0-9.]", " ").trim();
        if (digits.isEmpty()) return raw.trim();
        for (String tok : digits.split(" ")) {
            if (tok.matches("[0-9]+(\\.[0-9]+)?")) return tok;
        }
        return raw.trim();
    }

    private void dumpHtml(String name, String html) {
        try {
            File dir = new File("debug");
            if (!dir.exists()) dir.mkdirs();
            File f = new File(dir, name + ".html");
            try (FileWriter w = new FileWriter(f, false)) {
                w.write(html);
            }
            System.out.println("Saved debug HTML: " + f.getAbsolutePath());
        } catch (IOException ignore) { }
    }
}
