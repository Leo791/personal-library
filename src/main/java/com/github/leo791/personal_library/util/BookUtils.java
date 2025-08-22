package com.github.leo791.personal_library.util;
import java.lang.reflect.Field;
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
                    } else {
                            field.set(book, WordUtils.capitalizeFully(value));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to access field: " + field.getName(), e);
                }
            }
        }
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
