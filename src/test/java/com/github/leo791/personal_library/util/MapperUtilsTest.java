package com.github.leo791.personal_library.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapperUtilsTest {

    // ========== extractPageCount ==========
    @Test
    void extractPageCount() {
        Integer pageCount = 350;

        Integer pages = MapperUtils.extractPageCount(pageCount);
        assertEquals(350, pages, "Should return the page count if available");
    }

    @Test
    void extractPageCount_Null() {
        Integer pages = MapperUtils.extractPageCount(null);
        assertEquals(0, pages, "Should return 0 if page count is null");
    }

    @Test
    void extractPageCount_Zero() {
        Integer pages = MapperUtils.extractPageCount(0);
        assertEquals(0, pages, "Should return 0 if page count is zero");
    }

    // ========== extractPublishedDate ==========
    @Test
    void extractPublishedDate() {
        String publishedDate = "2023-10-01";

        String date = MapperUtils.extractPublishedDate(publishedDate);
        assertEquals("2023", date, "Should return the published year if available");
    }

    @Test
    void extractPublishedDate_Null() {
        String date = MapperUtils.extractPublishedDate(null);
        assertEquals("", date, "Should return an empty string if published date is null");
    }

    @Test
    void extractPublishedDate_Empty() {
        String date = MapperUtils.extractPublishedDate("");
        assertEquals("", date, "Should return an empty string if published date is empty");
    }

    @Test
    void extractPublishedDate_YearOnly() {
        String publishedDate = "2023";

        String date = MapperUtils.extractPublishedDate(publishedDate);
        assertEquals("2023", date, "Should return the year if the published date is just a year");
    }

    @Test
    void extractPublishedDate_YearAtEnd() {
        String publishedDate = "10-2023";

        String date = MapperUtils.extractPublishedDate(publishedDate);
        assertEquals("2023", date, "Should return the year if the published date is in YYYY-MM format");
    }

    @Test
    void extractPublishedDate_YearAtStart() {
        String publishedDate = "2023/10";

        String date = MapperUtils.extractPublishedDate(publishedDate);
        assertEquals("2023", date, "Should return the year if the published date is in YYYY/MM format");
    }

    @Test
    void extractPublishedDate_NoYearFound() {
        String publishedDate = "October 202";

        String date = MapperUtils.extractPublishedDate(publishedDate);
        assertEquals("", date, "Should return an empty string if no year is found in the published date");
    }
}