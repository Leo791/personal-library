package com.github.leo791.personal_library.util;

public class TranslationUtils {

    /**
     * Determines if translation is required based on detected and book languages.
     *
     * @param detectedLanguage the language detected from the text
     * @param bookLanguage     the language of the book
     * @return true if translation is required, false otherwise
     */
    public static boolean isTranslationRequired(String detectedLanguage, String bookLanguage) {
        return !detectedLanguage.equalsIgnoreCase(bookLanguage) && !detectedLanguage.equals("unknown");
    }
}
