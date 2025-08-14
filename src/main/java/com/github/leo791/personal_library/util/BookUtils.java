package com.github.leo791.personal_library.util;
import java.lang.reflect.Field;
import org.apache.commons.text.WordUtils;

/**
 * Utility class for handling book-related operations.
 * This class provides methods to manipulate book entities, such as capitalizing string fields.
 */
public class BookUtils{

    /**
     * Capitalizes all string fields in the given object.
     * This method uses reflection to access and modify string fields of the object.
     *
     * @param obj the object whose string fields are to be capitalized
     */
    public static void capitalizeStringFields(Object obj) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (field.getType().equals(String.class)) {
                field.setAccessible(true);
                try {
                    String value = (String) field.get(obj);
                    if (value != null) {
                        field.set(obj, WordUtils.capitalizeFully(value));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to access field: " + field.getName(), e);
                }
            }
        }
    }
}
