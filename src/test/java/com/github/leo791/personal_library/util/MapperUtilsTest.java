package com.github.leo791.personal_library.util;

import com.github.leo791.personal_library.model.entity.GoogleBookResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import java.util.List;

import static com.github.leo791.personal_library.util.MapperUtils.cleanDescription;
import static org.junit.jupiter.api.Assertions.*;

class MapperUtilsTest {

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

    @Test
    void testExtractGenre_MainCategory() {
        GoogleBookResponse.VolumeInfo volumeInfo = new GoogleBookResponse.VolumeInfo();
        volumeInfo.setMainCategory("Science Fiction");
        volumeInfo.setCategories(List.of("Adventure", "Fantasy"));

        String genre = MapperUtils.extractGenre(volumeInfo);
        assertEquals("Science Fiction", genre, "Should return mainCategory if available");
    }

    @Test
    void testExtractGenre_Categories() {
        GoogleBookResponse.VolumeInfo volumeInfo = new GoogleBookResponse.VolumeInfo();
        volumeInfo.setMainCategory(null);
        volumeInfo.setCategories(List.of("Adventure", "Fantasy"));

        String genre = MapperUtils.extractGenre(volumeInfo);
        assertEquals("Adventure", genre, "Should return first category if mainCategory is not available");
    }

    @Test
    void testExtractGenre_EmptyCategories() {
        GoogleBookResponse.VolumeInfo volumeInfo = new GoogleBookResponse.VolumeInfo();
        volumeInfo.setMainCategory(null);
        volumeInfo.setCategories(List.of());

        String genre = MapperUtils.extractGenre(volumeInfo);
        assertEquals("", genre, "Should return an empty string if both mainCategory and categories are not available");
    }

    @Test
    void testExtractGenre_NullCategories() {
        GoogleBookResponse.VolumeInfo volumeInfo = new GoogleBookResponse.VolumeInfo();
        volumeInfo.setMainCategory(null);
        volumeInfo.setCategories(null);

        String genre = MapperUtils.extractGenre(volumeInfo);
        assertEquals("", genre, "Should return an empty string if both mainCategory and categories are null");
    }

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

}