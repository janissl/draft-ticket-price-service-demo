package com.github.janissl.draftticket.conf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.*;

class ExternalServiceUrlBuilderTest {
    @Test
    @DisplayName("An extra segment must be correctly appended to a base URL")
    void testUrlPlusSegment() throws MalformedURLException {
        String basePriceUrl = "http://localhost:8080/baseprice";
        String destination = "Vilnius";

        String expectedUrl = "http://localhost:8080/baseprice/Vilnius";
        String actualUrl = ExternalServiceUrlBuilder.buildUrl(basePriceUrl, destination);

        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    @DisplayName("An extra segment must be correctly appended to a base URL with a trailing slash")
    void testUrlWithTrailingSlashPlusSingleSegment() throws MalformedURLException {
        String basePriceUrl = "http://localhost:8080/baseprice/";
        String destination = "Vilnius";

        String expectedUrl = "http://localhost:8080/baseprice/Vilnius";
        String actualUrl = ExternalServiceUrlBuilder.buildUrl(basePriceUrl, destination);

        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    @DisplayName("An extra multi-segment must be correctly concatenated to a base URL with a trailing slash")
    void testUrlWithTrailingSlashPlusMultiSegment() throws MalformedURLException {
        String basePriceUrl = "http://localhost:8080/api/";
        String destination = "destination/Vilnius";

        String expectedUrl = "http://localhost:8080/api/destination/Vilnius";
        String actualUrl = ExternalServiceUrlBuilder.buildUrl(basePriceUrl, destination);

        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    @DisplayName("An extra multi-segment with a leading slash must be correctly concatenated to a base URL with a trailing slash")
    void testUrlWithTrailingSlashPlusMultiSegmentLeadingSlash() throws MalformedURLException {
        String basePriceUrl = "http://localhost:8080/api/";
        String destination = "/destination/Vilnius";

        String expectedUrl = "http://localhost:8080/api/destination/Vilnius";
        String actualUrl = ExternalServiceUrlBuilder.buildUrl(basePriceUrl, destination);

        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    @DisplayName("An extra multi-segment with a leading and trailing slash must be correctly concatenated to a base URL with a trailing slash")
    void testUrlWithTrailingSlashPlusMultiSegmentLeadingTrailingSlash() throws MalformedURLException {
        String basePriceUrl = "http://localhost:8080/api/";
        String destination = "/destination/Vilnius/";

        String expectedUrl = "http://localhost:8080/api/destination/Vilnius";
        String actualUrl = ExternalServiceUrlBuilder.buildUrl(basePriceUrl, destination);

        assertEquals(expectedUrl, actualUrl);
    }
}
