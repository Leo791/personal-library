package com.github.leo791.personal_library.util;


import com.github.leo791.personal_library.model.entity.OpenLibraryBookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OpenLibraryResponseMapperUtils {

    private static final Logger log = LoggerFactory.getLogger(OpenLibraryResponseMapperUtils.class);

    /**
     * Extracts the first author from a list of authors.
     * If the list is empty or null, returns an empty string.
     * *
     * @param authors List of authors
     * @return The first author or an empty string if the list is empty
     */
    public static String extractFirstAuthor(List<OpenLibraryBookResponse.AuthorKey> authors){
        if (authors == null || authors.isEmpty()) {
            return "";
        }
        // Get the first author's key
        return authors.getFirst().key;
    }

    /**
     * Extracts the language code from a given language string.
     * If the string is null or empty, returns an empty string.
     * The language code is expected to be in ISO 639-1 format (e.g., "en", "pt").
     *
     * @param languages The list of language keys to extract from
     * @return The extracted language code in uppercase, or an empty string if input is null or empty
     */
    public static String extractLanguage(List<OpenLibraryBookResponse.LanguageKey> languages) {
        if (languages == null || languages.isEmpty()) {
            return "";
        }
        // Language is in the ISO 639-2 format, e.g., "eng"
        String language = languages.getFirst().key.substring("/languages/".length());
        if (language.length() != 3) {
           language = language.substring(0, 3); // Fallback to first 3 letters
            // log.warn("Language code '{}' is not in expected ISO 639-2 format", language);
        }
        return language.toUpperCase();
    }

    /**
     * Extracts the publisher from a given string.
     * If the string is null, returns an empty string.
     *
     * @param publishers The list of publishers to extract from
     * @return The extracted publisher, or an empty string if input is null
     */
    public static String extractPublisher(List<String> publishers) {
        if (publishers == null || publishers.isEmpty()) {
            return "";
        }
        return publishers.getFirst();
    }

}
