package com.github.leo791.personal_library.util;

import static org.junit.jupiter.api.Assertions.*;

import com.github.leo791.personal_library.model.entity.Book;
import org.junit.jupiter.api.Test;

class BookUtilsTest {

    @Test
    void capitalizeStringFields() {

        Book book = new Book("1234567890", "", "f. scott fitzgerald", null);
        BookUtils.capitalizeStringFields(book);

        assertEquals("", book.getTitle(), "Title should remain empty");
        assertEquals("F. Scott Fitzgerald", book.getAuthor(), "Author should be capitalized");
        assertEquals(null, book.getGenre(), "Genre should remain null");
    }
}