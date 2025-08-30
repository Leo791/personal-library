package com.github.leo791.personal_library.util;

import com.github.leo791.personal_library.model.entity.OpenLibraryBookResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OpenLibraryResponseMapperUtilsTest {

    // ==== extractFirstAuthor ====
    @Test
    void testExtractFirstAuthor_WithAuthors() {
        OpenLibraryBookResponse.AuthorKey author1 = new OpenLibraryBookResponse.AuthorKey();
        author1.setKey("/authors/OL12345A");
        OpenLibraryBookResponse.AuthorKey author2 = new OpenLibraryBookResponse.AuthorKey();
        author2.setKey("/authors/OL67890A");
        List<OpenLibraryBookResponse.AuthorKey> testList = List.of(author1, author2);
        String result = OpenLibraryResponseMapperUtils.extractFirstAuthor(testList);
        assertEquals("/authors/OL12345A", result); // Should return the first author's key
    }

    @Test
    void testExtractFirstAuthor_EmptyList() {
        String result = OpenLibraryResponseMapperUtils.extractFirstAuthor(List.of());
        assertEquals("", result, "Should return empty string when authors list is empty");
    }

    @Test
    void testExtractFirstAuthor_NullList() {
        String result = OpenLibraryResponseMapperUtils.extractFirstAuthor(null);
        assertEquals("", result, "Should return empty string when authors list is null");
    }


    // ==== extractIsbn ====
    @Test
    void testExtractIsbn_WithValidIsbn13() {
        OpenLibraryBookResponse testResponse = new OpenLibraryBookResponse();
        testResponse.setIsbn13(List.of("9780134686097"));

        String result = OpenLibraryResponseMapperUtils.extractIsbn(testResponse);
        assertEquals("9780134686097", result);
    }

    @Test
    void testExtractIsbn_WithValidIsbn10() {
        OpenLibraryBookResponse testResponse = new OpenLibraryBookResponse();
        testResponse.setIsbn13(List.of());
        testResponse.setIsbn10(List.of("0134686097"));

        String result = OpenLibraryResponseMapperUtils.extractIsbn(testResponse);
        assertEquals("0134686097", result);

    }

    @Test
    void testExtractIsbn_WithNoIsbn() {
        OpenLibraryBookResponse testResponse = new OpenLibraryBookResponse();
        testResponse.setIsbn13(List.of());
        testResponse.setIsbn10(List.of());

        String result = OpenLibraryResponseMapperUtils.extractIsbn(testResponse);
        assertEquals("", result);
    }

    @Test
    void testExtractIsbn_WithNullIsbns() {
        OpenLibraryBookResponse testResponse = new OpenLibraryBookResponse();

        String result = OpenLibraryResponseMapperUtils.extractIsbn(testResponse);
        assertEquals("", result, "Should return empty string when isbn13 is null");
    }

    // ==== extractLanguage ====


    @Test
    void testExtractLanguage_WithLanguage() {
        OpenLibraryBookResponse.LanguageKey langKey = new OpenLibraryBookResponse.LanguageKey("/languages/por");
        List<OpenLibraryBookResponse.LanguageKey> testList = List.of(langKey);

        String result = OpenLibraryResponseMapperUtils.extractLanguage(testList);
        assertEquals("POR", result); // Should return uppercase language code
    }

    @Test
    void testExtractLanguage_NullList() {
        String result = OpenLibraryResponseMapperUtils.extractLanguage(null);
        assertEquals("", result, "Should return empty string when languages list is null");
    }

    @Test
    void testExtractLanguage_EmptyList() {
        String result = OpenLibraryResponseMapperUtils.extractLanguage(List.of());
        assertEquals("", result, "Should return empty string when languages list is empty");
    }

    @Test
    void testExtractLanguage_InvalidFormat() {
        OpenLibraryBookResponse.LanguageKey langKey = new OpenLibraryBookResponse.LanguageKey("/languages/portuguese");
        List<OpenLibraryBookResponse.LanguageKey> testList = List.of(langKey);

        String result = OpenLibraryResponseMapperUtils.extractLanguage(testList);
        assertEquals("POR", result, "Should cut to first 3 letters if format is unexpected");
    }
    // ==== extractPublisher ====
    @Test
    void testExtractPublisher_WithPublisher() {
        List<String> testList = List.of("O'Reilly Media");

        String result = OpenLibraryResponseMapperUtils.extractPublisher(testList);
        assertEquals("O'Reilly Media", result); // Should return the publisher
    }

    @Test
    void testExtractPublisher_NullList() {
        String result = OpenLibraryResponseMapperUtils.extractPublisher(null);
        assertEquals("", result, "Should return empty string when publishers list is null");
    }

    @Test
    void testExtractPublisher_EmptyList() {
        String result = OpenLibraryResponseMapperUtils.extractPublisher(List.of());
        assertEquals("", result, "Should return empty string when publishers list is empty");
    }
}