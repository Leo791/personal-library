package com.github.leo791.personal_library.util;

import static org.junit.jupiter.api.Assertions.*;

import com.github.leo791.personal_library.model.entity.Book;
import org.junit.jupiter.api.Test;


/**
 * Unit tests for the BookUtils class.
 * This class tests the utility methods for manipulating Book entities.
 */
class BookUtilsTest {

    @Test
    void testCapitalizeStringFields() {

        Book book = new Book("1234567890", "", "f. scott fitzgerald", null,
                "A classic novel set in the 1920s.", "En", 180, "Scribner", "1925");
        BookUtils.capitalizeStringFields(book);

        assertEquals("", book.getTitle(), "Title should remain empty");
        assertEquals("F. Scott Fitzgerald", book.getAuthor(), "Author should be capitalized");
        assertNull(book.getGenre(), "Genre should remain null");
        assertEquals("A classic novel set in the 1920s.", book.getDescription(), "Description should not be capitalized");
        assertEquals("EN", book.getLanguage(), "Language should be fully capitalized");
        assertEquals("Scribner", book.getPublisher(), "Publisher should remain unchanged");
        assertEquals("1925", book.getPublishedDate(), "Published date should remain unchanged");

    }

    @Test
    void testUpdateBookFields() {
        Book book = new Book("1234567890", "The Great Gatsby", "Fitzgerald", "Fiction",
                "A novel set in the 1920s.", "English", 180, "Scribner", "1925");
        Book newBook = new Book("1234567890", null, "F. Scott Fitzgerald", null,
                null, null, 0, null, null);

        BookUtils.updateBookFields(book, newBook);

        assertEquals("The Great Gatsby", book.getTitle(), "Title should remain unchanged");
        assertEquals("F. Scott Fitzgerald", book.getAuthor(), "Author should be updated to full name version");
        assertEquals("Fiction", book.getGenre(), "Genre should remain unchanged");
    }

    @Test
    void testCleanDescription_RemoveTrailing(){
        String expectedDescription = "A novel set in the 1920s. It explores themes of decadence, idealism, resistance to change, social upheaval, and excess.";

        String descriptionWithEmDash = "A novel set in the 1920s. It explores themes of decadence, idealism, resistance to change," +
                " social upheaval, and excess. —Entertainment Weekly";
        String cleanedDescriptionWithEmDash = BookUtils.cleanDescription(descriptionWithEmDash);

        String descriptionWithEnDash = descriptionWithEmDash.replace("—", "–");
        String cleanedDescriptionWithEnDash = BookUtils.cleanDescription(descriptionWithEnDash);

        String descriptionWithCopyright = "A novel set in the 1920s. It explores themes of decadence, idealism, resistance to change," +
                " social upheaval, and excess. Copyright © 2023 by Entertainment Weekly";
        String cleanedDescriptionWithCopyright = BookUtils.cleanDescription(descriptionWithCopyright);

        String descriptionWithBothDashes = BookUtils.cleanDescription("A novel set in the 1920s. It explores themes of decadence, idealism, resistance to change," +
                " social upheaval, and excess. —Entertainment–Weekly");
        String cleanedDescriptionWithBothDashes = BookUtils.cleanDescription(descriptionWithBothDashes);


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
        String cleanedEmptyDescription = BookUtils.cleanDescription(emptyDescription);
        assertEquals("", cleanedEmptyDescription, "Empty description should return an empty string");

        String cleanedNullDescription = BookUtils.cleanDescription(null);
        assertEquals("", cleanedNullDescription, "Null description should return an empty string");
    }
}