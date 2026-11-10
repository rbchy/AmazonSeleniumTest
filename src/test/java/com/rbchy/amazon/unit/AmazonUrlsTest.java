package com.rbchy.amazon.unit;

import com.rbchy.amazon.support.AmazonUrls;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class AmazonUrlsTest {

    @Test
    void searchUrlEncodesTheQuery() {
        assertEquals("https://www.amazon.com/s?k=Google+Pixel+9", AmazonUrls.search("Google Pixel 9"));
        assertEquals("https://www.amazon.com/s?k=%22Google+Pixel+9%22", AmazonUrls.search("\"Google Pixel 9\""));
    }

    @Test
    void priceRangeIsAddedToAnExistingQuery() {
        assertEquals("https://www.amazon.com/s?k=pixel&ref=nb_sb_noss&low-price=400&high-price=2500",
                AmazonUrls.withPriceRange("https://www.amazon.com/s?k=pixel&ref=nb_sb_noss", 400, 2500));
    }

    @Test
    void aUrlWithoutAQueryStringGetsAProperOne() {
        // old code: "https://www.amazon.com/s&low-price=..." (no '?')
        assertEquals("https://www.amazon.com/s?low-price=10&high-price=20",
                AmazonUrls.withPriceRange("https://www.amazon.com/s", 10, 20));
    }

    @Test
    void anExistingRangeIsReplacedNotIgnored() {
        // old code skipped the step when "low-price" was already in the URL
        assertEquals("https://www.amazon.com/s?k=pixel&low-price=500&high-price=900",
                AmazonUrls.withPriceRange("https://www.amazon.com/s?k=pixel&low-price=1&high-price=2", 500, 900));
    }

    @Test
    void aFragmentStaysAtTheEnd() {
        assertEquals("https://www.amazon.com/s?k=pixel&low-price=1&high-price=2#top",
                AmazonUrls.withPriceRange("https://www.amazon.com/s?k=pixel#top", 1, 2));
    }

    @Test
    void anImpossibleRangeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AmazonUrls.withPriceRange(AmazonUrls.HOME, 500, 100));
        assertThrows(IllegalArgumentException.class, () -> AmazonUrls.withPriceRange(AmazonUrls.HOME, -1, 100));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {"$1,049.99|1049.99", "$499|499", "USD 12.50|12.5"})
    void displayedPricesParse(String text, double expected) {
        assertEquals(expected, AmazonUrls.parsePrice(text), 0.0001);
    }

    @Test
    void nonPricesAreNull() {
        assertNull(AmazonUrls.parsePrice("Currently unavailable."));
        assertNull(AmazonUrls.parsePrice(null));
        assertNull(AmazonUrls.parsePrice("1.2.3"));
    }
}
