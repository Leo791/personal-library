package com.github.leo791.personal_library.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IsbnUtilsTest {

    String validIsbn10 = "0441172717";
    String validIsbn13 = "9780441172719";
    String validIsbn10WithX = "972-23-3445-X";
    String validIsbn10WithHyphens = "0-441-17271-7";

    @Test
    void testIsValidIsbn() {
        assertTrue(IsbnUtils.isValidIsbn(validIsbn13), "Valid ISBN13 should return true");
        assertTrue(IsbnUtils.isValidIsbn(validIsbn10), "Valid ISBN10 return true");
        assertTrue(IsbnUtils.isValidIsbn(validIsbn10WithHyphens), "Valid ISBN10 with hyphens should return true");
        assertTrue(IsbnUtils.isValidIsbn(validIsbn10WithX), "Valid ISBN10 with 'X' should return true");

        assertFalse(IsbnUtils.isValidIsbn("1234567890"), "Invalid ISBN10 should return false");
        assertFalse(IsbnUtils.isValidIsbn("9783161484101"), "Invalid ISBN13 should return false");

        assertFalse(IsbnUtils.isValidIsbn("123456789"), "Invalid ISBN with less than 10 digits should return false");
        assertFalse(IsbnUtils.isValidIsbn("12345678901234"), "Invalid ISBN with more than 13 digits should return false");

        assertFalse(IsbnUtils.isValidIsbn(""), "Empty string should return false");
        assertFalse(IsbnUtils.isValidIsbn(null), "Null should return false");
    }

    @Test
    void testCalculateIsbn10CheckDigit() {
        assertEquals(7, IsbnUtils.calculateIsbn10CheckDigit(validIsbn10), "ISBN-10 check digit should be 7");
    }

    @Test
    void testCalculateIsbn13CheckDigit() {
        assertEquals(9, IsbnUtils.calculateIsbn13CheckDigit(validIsbn13), "ISBN-13 check digit should be 9");
    }

}