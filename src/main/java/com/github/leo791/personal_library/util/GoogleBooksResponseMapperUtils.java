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
