package com.github.leo791.personal_library.util;

import com.github.leo791.personal_library.model.entity.GoogleBookResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.leo791.personal_library.util.GoogleBooksResponseMapperUtils.cleanDescription;
import static org.junit.jupiter.api.Assertions.*;

class GoogleBooksResponseMapperUtilsTest {

    // ========== extractFirstAuthor ==========

    @Test
    void testExtractFirstAuthor() {
        List<String> authors = List.of("Thomas H Cormen", "Charles E Leiserson", "Ronald L Rivest", "Clifford Stein");

        String firstAuthor = GoogleBooksResponseMapperUtils.extractFirstAuthor(authors);
        assertEquals("Thomas H Cormen", firstAuthor, "First author should be 'Thomas H Cormen'");
    }

    @Test
    void testExtractFirstAuthor_EmptyList() {
        List<String> authors = List.of();

        String firstAuthor = GoogleBooksResponseMapperUtils.extractFirstAuthor(authors);
        assertEquals("", firstAuthor, "First author should be an empty string for an empty list");
    }

    @Test
    void testExtractFirstAuthor_NullList() {

        String firstAuthor = GoogleBooksResponseMapperUtils.extractFirstAuthor(null);
        assertEquals("", firstAuthor, "First author should be an empty string for a null list");
    }

    // ========== extractGenre ==========
    @Test
    void testExtractGenre_MainCategoryExists() {
        GoogleBookResponse.VolumeInfo volumeInfo = new GoogleBookResponse.VolumeInfo();
        volumeInfo.setMainCategory("Science Fiction");
        volumeInfo.setCategories(List.of("Adventure", "Fantasy"));

        String genre = GoogleBooksResponseMapperUtils.extractGenre(volumeInfo);
        assertEquals("Science Fiction", genre, "Should return mainCategory if available");
    }

    @Test
    void testExtractGenre_MainCategoryIsNull_CategoriesExist() {
        GoogleBookResponse.VolumeInfo volumeInfo = new GoogleBookResponse.VolumeInfo();
        volumeInfo.setMainCategory(null);
        volumeInfo.setCategories(List.of("Adventure", "Fantasy"));

        String genre = GoogleBooksResponseMapperUtils.extractGenre(volumeInfo);
        assertEquals("Adventure", genre, "Should return first category if mainCategory is not available");
    }

    @Test
    void testExtractGenre_MainCategoryIsBlank_CategoriesExist() {
        GoogleBookResponse.VolumeInfo volumeInfo = new GoogleBookResponse.VolumeInfo();
        volumeInfo.setMainCategory("");
        volumeInfo.setCategories(List.of("Adventure", "Fantasy"));

        String genre = GoogleBooksResponseMapperUtils.extractGenre(volumeInfo);
        assertEquals("Adventure", genre, "Should return first category if mainCategory is not available");
    }

    @Test
    void testExtractGenre_MainCategoryIsNull_CategoriesIsEmpty() {
        GoogleBookResponse.VolumeInfo volumeInfo = new GoogleBookResponse.VolumeInfo();
        volumeInfo.setMainCategory(null);
        volumeInfo.setCategories(List.of());

        String genre = GoogleBooksResponseMapperUtils.extractGenre(volumeInfo);
        assertEquals("", genre, "Should return an empty string if both mainCategory and categories are not available");
    }

    @Test
    void testExtractGenre_MainCategoryIsNull_CategoriesIsNull() {
        GoogleBookResponse.VolumeInfo volumeInfo = new GoogleBookResponse.VolumeInfo();
        volumeInfo.setMainCategory(null);
        volumeInfo.setCategories(null);

        String genre = GoogleBooksResponseMapperUtils.extractGenre(volumeInfo);
        assertEquals("", genre, "Should return an empty string if both mainCategory and categories are null");
    }

    @Test
    void testExtractGenre_WithAmpersand() {
        GoogleBookResponse.VolumeInfo volumeInfo = new GoogleBookResponse.VolumeInfo();
        volumeInfo.setMainCategory("Science & Technology");

        String genre = GoogleBooksResponseMapperUtils.extractGenre(volumeInfo);
        assertEquals("Science and Technology", genre, "Should return mainCategory with ampersand replaced by 'and'");
    }

    // ========== extractLanguage ==========
    @Test
    void extractLanguage() {
        String languageCode = "en";

        String language = GoogleBooksResponseMapperUtils.extractLanguage(languageCode);
        assertEquals("EN", language, "Should return the language if available");
    }

    @Test
    void extractLanguage_Null() {
        String language = GoogleBooksResponseMapperUtils.extractLanguage(null);
        assertEquals("", language, "Should return an empty string if language is null");
    }

    @Test
    void extractLanguage_Empty() {
        String language = GoogleBooksResponseMapperUtils.extractLanguage("");
        assertEquals("", language, "Should return an empty string if language is empty");
    }

    @Test
    void extractLanguage_LocaleCode() {
        String languageCode = "pt-BR";

        String language = GoogleBooksResponseMapperUtils.extractLanguage(languageCode);
        assertEquals("PT", language, "Should return the language code in uppercase");
    }

    // ========== extractPublisher ==========
    @Test
    void extractPublisher(){
        String publisher = "Penguin Random House";

        String pub = GoogleBooksResponseMapperUtils.extractPublisher(publisher);
        assertEquals("Penguin Random House", pub, "Should return the publisher if available");
    }

    @Test
    void extractPublisher_Null() {
        String pub = GoogleBooksResponseMapperUtils.extractPublisher(null);
        assertEquals("", pub, "Should return an empty string if publisher is null");
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