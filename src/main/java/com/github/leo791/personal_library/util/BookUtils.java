package com.github.leo791.personal_library.util;
import java.lang.reflect.Field;
import java.util.Arrays;

import org.apache.commons.text.WordUtils;

/**
 * Utility class for handling book-related operations.
 */
public class BookUtils{

    /**
     * Capitalizes the following fields: title, author, genre, publisher.
     * Capitalizes the whole string in the case of language.
     * This method uses reflection to access and modify string fields of the object.
     *
     * @param book the object whose string fields are to be capitalized
     */
    public static void capitalizeStringFields(Object book) {
        for (Field field : book.getClass().getDeclaredFields()) {
            if (field.getType().equals(String.class)) {
                field.setAccessible(true);
                try {
                    String value = (String) field.get(book);
                    if (value == null || value.isBlank()) {
                        continue; // Skip null or blank values
                    }
                    if ("language".equals(field.getName())) {
                        // Capitalize the whole string for language
                            field.set(book, value.toUpperCase());
                    } else if ("description".equals(field.getName())) {
                        // Do not capitalize description, leave it as is
                        continue;
                    } else if ("author".equals(field.getName())) {
                        // Capitalize each part of the author's name
                            field.set(book, capitalizeAuthorName(value));
                    } else {
                            field.set(book, WordUtils.capitalizeFully(value));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to access field: " + field.getName(), e);
                }
            }
        }
    }

public static String capitalizeAuthorName(String name) {
    String[] particles = {"de", "da", "di", "van", "von", "le", "la", "du", "del", "dos", "das"};
    String[] parts = name.split(" ");
    for (int i = 0; i < parts.length; i++) {
        String part = parts[i];
        if (part.matches("([a-zA-Z]\\.)+")) {
            StringBuilder sb = new StringBuilder();
            for (String initial : part.split("\\.")) {
                if (!initial.isEmpty()) {
                    sb.append(initial.toUpperCase()).append(".");
                }
            }
            parts[i] = sb.toString();
        } else {
            String[] hyphenParts = part.split("-");
            for (int j = 0; j < hyphenParts.length; j++) {
                String subPart = hyphenParts[j];
                if (i > 0 && Arrays.asList(particles).contains(subPart.toLowerCase())) {
                    hyphenParts[j] = subPart.toLowerCase();
                } else {
                    subPart = WordUtils.capitalizeFully(subPart);
                    if (subPart.matches("(?i)mc[a-z].*")) {
                        subPart = "Mc" + subPart.substring(2, 3).toUpperCase() + subPart.substring(3);
                    } else if (subPart.matches("(?i)mac[a-z].*")) {
                        subPart = "Mac" + subPart.substring(3, 4).toUpperCase() + subPart.substring(4);
                    }
                    hyphenParts[j] = subPart;
                }
            }
            parts[i] = String.join("-", hyphenParts);
        }
    }
    return String.join(" ", parts);
}

    /**
     * Updates the fields of a book entity with the given data.
     * Keeps the existing values if the new data is null.
     * @param book the book entity to be updated
     * @param newBook the new data to update the book entity with
     */
    public static void updateBookFields(Object book, Object newBook) {
        for (Field field : book.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object newValue = field.get(newBook);
                if (newValue != null) {
                    field.set(book, newValue);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access field: " + field.getName(), e);
            }
        }
    }
}
