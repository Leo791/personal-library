package com.github.leo791.personal_library.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapperUtils {

    /**
     * Extracts the page count from a given Integer.
     * If the Integer is null or less than or equal to zero, returns null.
     *
     * @param pageCount The page count to extract from
     * @return The extracted page count, or null if input is null or less than or equal to zero
     */
    public static Integer extractPageCount(Integer pageCount) {
        return (pageCount != null && pageCount > 0) ? pageCount : 0;
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


}
