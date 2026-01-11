package com.recommendation.test;

import com.recommendation.preprocessing.DataLoader;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DataLoaderTest {

    @Test
    public void testParsePrice() {
        assertEquals(99.99, DataLoader.parsePrice("99,99 €"), 0.001);
        assertEquals(1250.0, DataLoader.parsePrice("1 250,00 €"), 0.001);
        assertEquals(10.5, DataLoader.parsePrice("10.5"), 0.001);
        assertEquals(0.0, DataLoader.parsePrice(null), 0.001);
    }

    @Test
    public void testParseRating() {
        assertEquals(4.5, DataLoader.parseRating("4,5"), 0.001);
        assertEquals(5.0, DataLoader.parseRating("5.0"), 0.001);
        assertEquals(0.0, DataLoader.parseRating("invalid"), 0.001);
    }

    @Test
    public void testGenerateId() {
        String url = "https://www.cdiscount.com/p-123.html";
        String id1 = DataLoader.generateId(url);
        String id2 = DataLoader.generateId(url);

        assertTrue(id1.startsWith("prod_"));
        assertEquals(id1, id2);
    }

    @Test
    public void testExtractCategory() {
        assertEquals("smartphones", DataLoader.extractCategory("cdiscount_smartphones.csv"));
        assertEquals("laptops", DataLoader.extractCategory("path/to/laptops.csv"));
        assertEquals("unknown", DataLoader.extractCategory(null));
    }
}
