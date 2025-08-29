package com.github.leo791.personal_library.util;


import com.github.leo791.personal_library.model.entity.GoogleBookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleBooksResponseMapperUtils {

    private static final Logger log = LoggerFactory.getLogger(GoogleBooksResponseMapperUtils.class);

    /**
     * Extracts the first author from a list of authors.
     * If the list is empty or null, returns an empty string.
     *
     * @param authors List of authors
     * @return The first author or an empty string if the list is empty
     */
    public static String extractFirstAuthor(List<String> authors){
        return (authors != null && !authors.isEmpty())
                ? authors.getFirst()
                : "";
    }

    /**
     * Extracts the ISBN-13 or 10 from a list of industry identifiers.
     * If none is found an error is thrown.
     *
     * @param industryIdentifiers List of industry identifiers
     * @return The ISBN-13 or 10
     */
    public static String extractIsbn(List<GoogleBookResponse.IndustryIdentifier> industryIdentifiers) {
        if (industryIdentifiers == null || industryIdentifiers.isEmpty()) {
            throw new IllegalArgumentException("Industry identifiers list must not be null or empty");
        }
        // Look for ISBN_13 first
        for (GoogleBookResponse.IndustryIdentifier identifier : industryIdentifiers) {
            if ("ISBN_13".equals(identifier.getType())) {
                return identifier.getIdentifier();
            }
        }
        // If not found, look for ISBN_10
        for (GoogleBookResponse.IndustryIdentifier identifier : industryIdentifiers) {
            if ("ISBN_10".equals(identifier.getType())) {
                return identifier.getIdentifier();
            }
        }
        // If neither is found, return an empty string
        log.warn("No ISBN found in industry identifiers, relying on search ISBN");
        return "";
    }

    /**
     * Extracts the genre from the volume info.
     * If mainCategory is available, it returns that.
     * If categories are available, it returns the first one.
     * If neither is available, it returns an empty string.
     *
     * @param volumeInfo The volume info from which to extract the genre
     * @return The extracted genre or an empty string if not found
     */
    public static String extractGenre(GoogleBookResponse.VolumeInfo volumeInfo) {
        String genre = "";
        // If mainCategory is available, return it
        if (volumeInfo.getMainCategory() != null && !volumeInfo.getMainCategory().isBlank()) {
            genre = volumeInfo.getMainCategory();
        // If categories are available, return the first one
        } else if (volumeInfo.getCategories() != null && !volumeInfo.getCategories().isEmpty()) {
            genre = volumeInfo.getCategories().getFirst();
        }
        // Replace "&" with "and" for better readability & searchability
        return genre.replace("&", "and");
    }

    /**
     * Extracts the language code from a given language string.
     * If the string is null or empty, returns an empty string.
     * The language code is expected to be in ISO 639-1 format (e.g., "en", "pt").
     *
     * @param language The language string to extract the code from
     * @return The extracted language code in uppercase, or an empty string if input is null or empty
     */
    public static String extractLanguage(String language) {
        if (language == null || language.isBlank()) {
            return "";
        }
        // Language is expected to be in ISO 639-1 format.
        // But they might be locale codes (e.g. pt-BT or en-GB). So we take the first two characters.
        return language.toUpperCase().substring(0, 2);
    }

    /**
     * Extracts the page count from a given Integer.
     * If the Integer is null or less than or equal to zero, returns 0.
     *
     * @param pageCount The page count to extract from
     * @return The extracted page count, or 0 if input is null or less than or equal to zero
     */

    public static Integer extractPageCount(Integer pageCount) {
        return (pageCount != null && pageCount > 0) ? pageCount : 0;
    }

    /**
     * Extracts the publisher from a given string.
     * If the string is null, returns an empty string.
     *
     * @param publisher The publisher string to extract from
     * @return The extracted publisher, or an empty string if input is null
     */
    public static String extractPublisher(String publisher) {
        return (publisher != null) ? publisher : "";
    }

    /**
     * Extracts the published date from a given string.
     * If the string is null or empty, returns an empty string.
     * The published date is expected to be in ISO 8601 format (e.g., "2023-10-01").
     * We will return just the year (e.g., "2023").
     *
     * @param publishedDate The published date string to extract from
     * @return The extracted year as a string, or an empty string if input is null or empty
     */
    public static String extractPublishedDate(String publishedDate) {
        if (publishedDate == null || publishedDate.isBlank()) {
            return "";
        }
        Pattern pattern = Pattern.compile("\\b(\\d{4})\\b");
        Matcher matcher = pattern.matcher(publishedDate);

        if(matcher.find()) {
            return matcher.group();
        }
        return "";

    }

    /**
     * Cleans up a book description by removing trailing copyright notices,
     * publisher disclaimers, or extraneous characters.
     */
    public static String cleanDescription(String description) {
        if (description == null || description.isBlank()) {
            return "";
        }

        // Normalize whitespace
        String cleaned = description.trim().replaceAll("\\s+", " ");

        // Remove quotes in the description
        cleaned = cleaned.replaceAll("\"", "").trim();
        cleaned = cleaned.replaceAll("[“”]", "").trim();

        // Cut off at "Copyright" (case-insensitive)
        int copyrightIndex = cleaned.toLowerCase().indexOf("copyright");
        if (copyrightIndex != -1) {
            cleaned = cleaned.substring(0, copyrightIndex).trim();
        }

        // Remove space before punctuation
        cleaned = cleaned.replaceAll("\\s+([.,;:!?])", "$1").trim();

        // Fix ellipsis
        cleaned = cleaned.replaceAll("\\.\\s\\.\\s\\.", "...").trim();
        cleaned = cleaned.replaceAll("(?<!\\.)\\.\\.(?!\\.)", "...").trim();

        // Cut off at em dash or en dash if present (– or —)
        int dashIndex = cleaned.indexOf("–");  // en dash
        int emDashIndex = cleaned.indexOf("—"); // em dash
        int splitIndex = -1;

        if (dashIndex != -1 && emDashIndex != -1) {
            splitIndex = Math.min(dashIndex, emDashIndex);
        } else {
            splitIndex = Math.max(dashIndex, emDashIndex);
        }

        if (splitIndex != -1) {
            cleaned = cleaned.substring(0, splitIndex).trim();
        }

        return cleaned;
    }
}
