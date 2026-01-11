package com.recommendation.test;

import com.recommendation.model.TFIDFVectorizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class TFIDFVectorizerTest {

    private TFIDFVectorizer vectorizer;

    @BeforeEach
    public void setUp() {
        vectorizer = new TFIDFVectorizer();
    }

    @Test
    public void testFitAndVocabSize() {
        List<String> docs = Arrays.asList(
                "Smartphone Samsung Galaxy",
                "iPhone Apple Smartphone",
                "Samsung TV 4K");
        vectorizer.fit(docs);

        // Words expected (tokenized): smartphone, samsung, galaxy, iphone, apple, tv
        // Note: 4k might be tokenized if length > 2
        // Actually TFIDFVectorizer.java line 129: if (word.length() > 2 ...)
        // smartphone (10), samsung (7), galaxy (6), iphone (6), apple (5), tv (2)
        // "tv" will be ignored because length <= 2.

        assertTrue(vectorizer.getVocabSize() > 0);
    }

    @Test
    public void testTransform() {
        List<String> docs = Arrays.asList("apple banana cherry", "banana cherry date");
        vectorizer.fit(docs);

        double[] vector = vectorizer.transform("apple banana");

        assertNotNull(vector);
        assertEquals(vectorizer.getVocabSize(), vector.length);

        // Verify that untrained transform throws exception
        TFIDFVectorizer untrained = new TFIDFVectorizer();
        assertThrows(IllegalStateException.class, () -> untrained.transform("test"));
    }
}
