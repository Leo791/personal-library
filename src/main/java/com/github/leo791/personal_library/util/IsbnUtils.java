package com.github.leo791.personal_library.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IsbnUtils {

    private static final Logger log = LoggerFactory.getLogger(IsbnUtils.class);

    /**
     * Checks if the given ISBN is valid.
     * A valid ISBN is a non-null, non-empty string that matches the ISBN-10 or ISBN-13 format.
     *
     * @param isbn the ISBN to check
     * @return true if the ISBN is valid, false otherwise
     */
    public static boolean isValidIsbn(String isbn) {
        if (isbn == null || isbn.isEmpty()) {
            return false; // ISBN cannot be null or empty
        }
        isbn = isbn.replaceAll("-", ""); // Remove any hyphens for validation

        // Get last digit for validation, 'X' is treated as 10 for ISBN-10
        char lastChar = isbn.charAt(isbn.length() - 1);
        int lastDigit = lastChar == 'X' ? 10 : Character.getNumericValue(lastChar);

        // Check for ISBN-10 format
        if (isbn.length() == 10) {
            int checkDigit = calculateIsbn10CheckDigit(isbn);
            log.debug("ISBN-10 Check Digit: {}, Last Digit: {}", checkDigit, lastDigit);
            return lastDigit == checkDigit;
        }
        // Check for ISBN-13 format
        if (isbn.length() == 13) {
            int checkDigit = calculateIsbn13CheckDigit(isbn);
            log.debug("ISBN-13 Check Digit: {}, Last Digit: {}", checkDigit, lastDigit);
            return lastDigit == checkDigit;
        }
        return false; // Invalid length
    }

        // Calculate ISBN-10 check digit
        public static int calculateIsbn10CheckDigit(String isbn10) {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                int digit = Character.getNumericValue(isbn10.charAt(i));
                sum += digit * (i + 1);
            }
            return sum % 11;
        }

        // Calculate ISBN-13 check digit
        public static int calculateIsbn13CheckDigit(String isbn13) {
            int sum = 0;
            for (int i = 0; i < 12; i++) {
                int digit = Character.getNumericValue(isbn13.charAt(i));
                sum += (i % 2 == 0) ? digit : digit * 3;
            }
            return (10 - (sum % 10)) % 10;
        }
    }
    
