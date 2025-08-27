package com.github.leo791.personal_library.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    private final Book book1 = new Book("1234567890", "The Hitchhiker's Guide to the Galaxy", "Douglas Adams", "Fantasy",
            "A science fiction comedy novel.", "English", 224, "Pan Books", "1979");
    private final Book book2 = new Book("1234567890", "Hitchhiker's Guide to the Galaxy", "Douglas Adams", "Fantasy",
            "A science fiction comedy novel.", "English", 224, "Pan Books", "1979");
    private final Book book3 = new Book("0987654321", "Dune", "Frank Herbert", "Science Fiction",
            "A science fiction novel set in a distant future.", "English", 412, "Chilton Books", "1965");

    @Test
    void testToString() {
        String expected = "Book{isbn='1234567890', title='The Hitchhiker's Guide to the Galaxy', author='Douglas Adams', " +
                "genre='Fantasy', description='A science fiction comedy novel.', language='English', publisher='Pan Books', " +
                "pageCount=224, publishedDate='1979'}";
        assertEquals(expected, book1.toString(), "toString method should return the correct string representation");
    }

    @Test
    void testEquals() {

        assertEquals(book1, book2, "Books with the same ISBN should be equal");
        assertNotEquals(book1, book3, "Books with different ISBNs should not be equal");
    }

    @Test
    void testHashCode() {

        assertEquals(book1.hashCode(), book2.hashCode(), "Books with the same ISBN should have the same hash code");
        assertNotEquals(book1.hashCode(), book3.hashCode(), "Books with different ISBNs should have different hash codes");
    }
}