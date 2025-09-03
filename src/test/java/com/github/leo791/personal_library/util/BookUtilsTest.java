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
    void testCapitalizeAuthorName(){
        String author = "j.k. rowling";
        String capitalizedAuthor = BookUtils.capitalizeAuthorName(author);
        assertEquals("J.K. Rowling", capitalizedAuthor, "Author name should be properly capitalized");

        String authorWithSpaces = "george r. r. martin  ";
        String capitalizedAuthorWithSpaces = BookUtils.capitalizeAuthorName(authorWithSpaces);
        assertEquals("George R. R. Martin", capitalizedAuthorWithSpaces, "Author name should be properly capitalized and trimmed");

        String authorNormal = "agatha christie";
        String capitalizedAuthorNormal = BookUtils.capitalizeAuthorName(authorNormal);
        assertEquals("Agatha Christie", capitalizedAuthorNormal, "Author name should be capitalized");

        String authorNameWithMc = "robert mcgregor";
        String capitalizedAuthorWithMc = BookUtils.capitalizeAuthorName(authorNameWithMc);
        assertEquals("Robert McGregor", capitalizedAuthorWithMc, "Author name with 'Mc' should be properly capitalized");

        String authorNameWithMac = "ian macdonald";
        String capitalizedAuthorWithMac = BookUtils.capitalizeAuthorName(authorNameWithMac);
        assertEquals("Ian MacDonald", capitalizedAuthorWithMac, "Author name with 'Mac' should be properly capitalized");
    }
}