package com.github.leo791.personal_library.util;

import com.github.leo791.personal_library.model.entity.GoogleBookResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import java.util.List;

import static com.github.leo791.personal_library.util.MapperUtils.cleanDescription;
import static org.junit.jupiter.api.Assertions.*;

class MapperUtilsTest {

    // ========== extractFirstAuthor ==========

    @Test
    void testExtractFirstAuthor() {
        List<String> authors = List.of("Thomas H Cormen", "Charles E Leiserson", "Ronald L Rivest", "Clifford Stein");

        String firstAuthor = MapperUtils.extractFirstAuthor(authors);
        assertEquals("Thomas H Cormen", firstAuthor, "First author should be 'Thomas H Cormen'");
    }

    @Test
    void testExtractFirstAuthor_EmptyList() {
        List<String> authors = List.of();

        String firstAuthor = MapperUtils.extractFirstAuthor(authors);
        assertEquals("", firstAuthor, "First author should be an empty string for an empty list");
    }

    @Test
    void testExtractFirstAuthor_NullList() {

        String firstAuthor = MapperUtils.extractFirstAuthor(null);
        assertEquals("", firstAuthor, "First author should be an empty string for a null list");
    }

    // ========== extractIsbn ==========

    @Test
    void testExtractIsbn() {
        List<GoogleBookResponse.IndustryIdentifier> industryIdentifiers = List.of(
                new GoogleBookResponse.IndustryIdentifier("ISBN_13", "9780134686097"),
                new GoogleBookResponse.IndustryIdentifier("ISBN_10", "0134686098")
        );

        String isbn = MapperUtils.extractIsbn(industryIdentifiers);
        assertEquals("9780134686097", isbn, "Should return ISBN_13 if available");
    }

    @Test
    void testExtractIsbn_NoIsbn13() {
        List<GoogleBookResponse.IndustryIdentifier> industryIdentifiers = List.of(
                new GoogleBookResponse.IndustryIdentifier("ISBN_10", "0134686098")
        );

        String isbn = MapperUtils.extractIsbn(industryIdentifiers);
        assertEquals("0134686098", isbn, "Should return ISBN_10 if ISBN_13 is not available");
    }

    @Test
    void testExtractIsbn_NoValidIsbn() {
        List<GoogleBookResponse.IndustryIdentifier> industryIdentifiers = List.of(
                new GoogleBookResponse.IndustryIdentifier("OTHER", "1234567890")
        );

        String isbn = MapperUtils.extractIsbn(industryIdentifiers);
        assertEquals("", isbn, "Should return an empty string if neither ISBN_13 nor ISBN_10 is available");
    }

    @Test
    void testExtractIsbn_EmptyList() {
        List<GoogleBookResponse.IndustryIdentifier> industryIdentifiers = List.of();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            MapperUtils.extractIsbn(industryIdentifiers);
        });

        assertEquals("Industry identifiers list must not be null or empty", exception.getMessage(),
                "Should throw an exception for an empty list");
    }

    @Test
    void testExtractIsbn_NullList() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            MapperUtils.extractIsbn(null);
        });

        assertEquals("Industry identifiers list must not be null or empty", exception.getMessage(),
                "Should throw an exception for a null list");
    }

    // ========== extractGenre ==========
    @Test
    void testExtractGenre_MainCategoryExists() {
        GoogleBookResponse.VolumeInfo volumeInfo = new GoogleBookResponse.VolumeInfo();
        volumeInfo.setMainCategory("Science Fiction");
        volumeInfo.setCategories(List.of("Adventure", "Fantasy"));

        String genre = MapperUtils.extractGenre(volumeInfo);
        assertEquals("Science Fiction", genre, "Should return mainCategory if available");
    }

    @Test
    void testExtractGenre_MainCategoryIsNull_CategoriesExist() {
        GoogleBookResponse.VolumeInfo volumeInfo = new GoogleBookResponse.VolumeInfo();
        volumeInfo.setMainCategory(null);
        volumeInfo.setCategories(List.of("Adventure", "Fantasy"));

        String genre = MapperUtils.extractGenre(volumeInfo);
        assertEquals("Adventure", genre, "Should return first category if mainCategory is not available");
    }

    @Test
    void testExtractGenre_MainCategoryIsBlank_CategoriesExist() {
        GoogleBookResponse.VolumeInfo volumeInfo = new GoogleBookResponse.VolumeInfo();
        volumeInfo.setMainCategory("");
        volumeInfo.setCategories(List.of("Adventure", "Fantasy"));

        String genre = MapperUtils.extractGenre(volumeInfo);
        assertEquals("Adventure", genre, "Should return first category if mainCategory is not available");
    }

    @Test
    void testExtractGenre_MainCategoryIsNull_CategoriesIsEmpty() {
        GoogleBookResponse.VolumeInfo volumeInfo = new GoogleBookResponse.VolumeInfo();
        volumeInfo.setMainCategory(null);
        volumeInfo.setCategories(List.of());

        String genre = MapperUtils.extractGenre(volumeInfo);
        assertEquals("", genre, "Should return an empty string if both mainCategory and categories are not available");
    }

    @Test
    void testExtractGenre_MainCategoryIsNull_CategoriesIsNull() {
        GoogleBookResponse.VolumeInfo volumeInfo = new GoogleBookResponse.VolumeInfo();
        volumeInfo.setMainCategory(null);
        volumeInfo.setCategories(null);

        String genre = MapperUtils.extractGenre(volumeInfo);
        assertEquals("", genre, "Should return an empty string if both mainCategory and categories are null");
    }

    @Test
    void testExtractGenre_WithAmpersand() {
        GoogleBookResponse.VolumeInfo volumeInfo = new GoogleBookResponse.VolumeInfo();
        volumeInfo.setMainCategory("Science & Technology");

        String genre = MapperUtils.extractGenre(volumeInfo);
        assertEquals("Science and Technology", genre, "Should return mainCategory with ampersand replaced by 'and'");
    }

    // ========== extractLanguage ==========
    @Test
    void extractLanguage() {
        String languageCode = "en";

        String language = MapperUtils.extractLanguage(languageCode);
        assertEquals("EN", language, "Should return the language if available");
    }

    @Test
    void extractLanguage_Null() {
        String language = MapperUtils.extractLanguage(null);
        assertEquals("", language, "Should return an empty string if language is null");
    }

    @Test
    void extractLanguage_Empty() {
        String language = MapperUtils.extractLanguage("");
        assertEquals("", language, "Should return an empty string if language is empty");
    }

    @Test
    void extractLanguage_LocaleCode() {
        String languageCode = "pt-BR";

        String language = MapperUtils.extractLanguage(languageCode);
        assertEquals("PT", language, "Should return the language code in uppercase");
    }

    // ========== extractPageCount ==========
    @Test
    void extractPageCount() {
        Integer pageCount = 350;

        Integer pages = MapperUtils.extractPageCount(pageCount);
        assertEquals(350, pages, "Should return the page count if available");
    }

    @Test
    void extractPageCount_Null() {
        Integer pages = MapperUtils.extractPageCount(null);
        assertEquals(0, pages, "Should return 0 if page count is null");
    }

    @Test
    void extractPageCount_Zero() {
        Integer pages = MapperUtils.extractPageCount(0);
        assertEquals(0, pages, "Should return 0 if page count is zero");
    }

    // ========== extractPublisher ==========
    @Test
    void extractPublisher(){
        String publisher = "Penguin Random House";

        String pub = MapperUtils.extractPublisher(publisher);
        assertEquals("Penguin Random House", pub, "Should return the publisher if available");
    }

    @Test
    void extractPublisher_Null() {
        String pub = MapperUtils.extractPublisher(null);
        assertEquals("", pub, "Should return an empty string if publisher is null");
    }

    // ========== extractPublishedDate ==========
    @Test
    void extractPublishedDate() {
        String publishedDate = "2023-10-01";

        String date = MapperUtils.extractPublishedDate(publishedDate);
        assertEquals("2023", date, "Should return the published year if available");
    }

    @Test
    void extractPublishedDate_Null() {
        String date = MapperUtils.extractPublishedDate(null);
        assertEquals("", date, "Should return an empty string if published date is null");
    }

    @Test
    void extractPublishedDate_Empty() {
        String date = MapperUtils.extractPublishedDate("");
        assertEquals("", date, "Should return an empty string if published date is empty");
    }

    @Test
    void extractPublishedDate_YearOnly() {
        String publishedDate = "2023";

        String date = MapperUtils.extractPublishedDate(publishedDate);
        assertEquals("2023", date, "Should return the year if the published date is just a year");
    }

    @Test
    void extractPublishedDate_YearAtEnd() {
        String publishedDate = "10-2023";

        String date = MapperUtils.extractPublishedDate(publishedDate);
        assertEquals("2023", date, "Should return the year if the published date is in YYYY-MM format");
    }

    @Test
    void extractPublishedDate_YearAtStart() {
        String publishedDate = "2023/10";

        String date = MapperUtils.extractPublishedDate(publishedDate);
        assertEquals("2023", date, "Should return the year if the published date is in YYYY/MM format");
    }

    @Test
    void extractPublishedDate_NoYearFound() {
        String publishedDate = "October 202";

        String date = MapperUtils.extractPublishedDate(publishedDate);
        assertEquals("", date, "Should return an empty string if no year is found in the published date");
    }

    // ========== cleanDescription ==========
    @Test
    void testCleanDescription_RemoveTrailing(){
        String expectedDescription = "A novel set in the 1920s. It explores themes of decadence, idealism, resistance to change, social upheaval, and excess.";

        String descriptionWithEmDash = "A novel set in the 1920s. It explores themes of decadence, idealism, resistance to change," +
                " social upheaval, and excess. —Entertainment Weekly";
        String cleanedDescriptionWithEmDash = cleanDescription(descriptionWithEmDash);

        String descriptionWithEnDash = descriptionWithEmDash.replace("—", "–");
        String cleanedDescriptionWithEnDash = cleanDescription(descriptionWithEnDash);

        String descriptionWithCopyright = "A novel set in the 1920s. It explores themes of decadence, idealism, resistance to change," +
                " social upheaval, and excess. Copyright © 2023 by Entertainment Weekly";
        String cleanedDescriptionWithCopyright = cleanDescription(descriptionWithCopyright);

        String descriptionWithBothDashes = cleanDescription("A novel set in the 1920s. It explores themes of decadence, idealism, resistance to change," +
                " social upheaval, and excess. —Entertainment–Weekly");
        String cleanedDescriptionWithBothDashes = cleanDescription(descriptionWithBothDashes);

        assertEquals(expectedDescription,
                cleanedDescriptionWithEmDash, "Description should be cleaned of trailing citation");
        assertEquals(expectedDescription,
                cleanedDescriptionWithEnDash, "Description should be cleaned of trailing citation with hyphen");
        assertEquals(expectedDescription,
                cleanedDescriptionWithCopyright, "Description should be cleaned of trailing copyright notice");
        assertEquals(expectedDescription,
                cleanedDescriptionWithBothDashes, "Description should be cut on first hyphen or dash");

    }

    @Test
    void testCleanDescription_EmptyOrNull() {
        String emptyDescription = "   ";
        String cleanedEmptyDescription = cleanDescription(emptyDescription);
        assertEquals("", cleanedEmptyDescription, "Empty description should return an empty string");

        String cleanedNullDescription = cleanDescription(null);
        assertEquals("", cleanedNullDescription, "Null description should return an empty string");
    }

    @Test
    void testCleanDescription_NormalizeWhitespaceAndQuotes() {
        String descriptionWithExtraWhitespace = "  A novel set in the 1920s.   It explores themes of decadence, idealism,   resistance to change, social upheaval, and excess.   ";
        String cleanedDescriptionWithExtraWhitespace = cleanDescription(descriptionWithExtraWhitespace);
        assertEquals("A novel set in the 1920s. It explores themes of decadence, idealism, resistance to change, social upheaval, and excess.",
                cleanedDescriptionWithExtraWhitespace, "Description should have normalized whitespace");

        String descriptionWithQuotes = "\"A novel set in the 1920s\". It explores themes of decadence, idealism, resistance to change, social upheaval, and excess.\"";
        String cleanedDescriptionWithQuotes = cleanDescription(descriptionWithQuotes);
        assertEquals("A novel set in the 1920s. It explores themes of decadence, idealism, resistance to change, social upheaval, and excess.",
                cleanedDescriptionWithQuotes, "Description should have quotes removed");

        String descriptionWithSmartQuotes = "“A novel set in the 1920s”. It explores themes of decadence, idealism, resistance to change, social upheaval, and excess.";
        String cleanedDescriptionWithSmartQuotes = cleanDescription(descriptionWithSmartQuotes);
        assertEquals("A novel set in the 1920s. It explores themes of decadence, idealism, resistance to change, social upheaval, and excess.",
                cleanedDescriptionWithSmartQuotes, "Description should have smart quotes removed");
    }

    @Test
    void testCleanDescription_FixEllipsis() {
        String descriptionWithEllipsis1 = "A novel set in the 1920s... It explores themes of decadence, idealism, resistance to change, social upheaval, and excess.";
        String cleanedDescriptionWithEllipsis1 = cleanDescription(descriptionWithEllipsis1);
        assertEquals("A novel set in the 1920s... It explores themes of decadence, idealism, resistance to change, social upheaval, and excess.",
                cleanedDescriptionWithEllipsis1, "Description should have ellipsis normalized");

        String descriptionWithEllipsis2 = "A novel set in the 1920s.. It explores themes of decadence, idealism, resistance to change, social upheaval, and excess.";
        String cleanedDescriptionWithEllipsis2 = cleanDescription(descriptionWithEllipsis2);
        assertEquals("A novel set in the 1920s... It explores themes of decadence, idealism, resistance to change, social upheaval, and excess.",
                cleanedDescriptionWithEllipsis2, "Description should have ellipsis normalized");
    }

    @Test
    void testCleanDescription_RemoveSpaceBeforePunctuation() {
        String descriptionWithSpaceBeforePunctuation = "A novel set in the 1920s . It explores themes of decadence , idealism , resistance to change , social upheaval , and excess .";
        String cleanedDescriptionWithSpaceBeforePunctuation = cleanDescription(descriptionWithSpaceBeforePunctuation);
        assertEquals("A novel set in the 1920s. It explores themes of decadence, idealism, resistance to change, social upheaval, and excess.",
                cleanedDescriptionWithSpaceBeforePunctuation, "Description should have spaces before punctuation removed");
    }
}