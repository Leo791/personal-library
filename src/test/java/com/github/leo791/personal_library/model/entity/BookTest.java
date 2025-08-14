package com.github.leo791.personal_library.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    private final Book book1 = new Book("1234567890", "The Hitchhiker's Guide to the Galaxy", "Douglas Adams", "Fantasy");
    private final Book book2 = new Book("1234567890", "Hitchhiker's Guide to the Galaxy", "Douglas Adams", "Fantasy");
    private final Book book3 = new Book("0987654321", "Dune", "Frank Herbert", "Science Fiction");


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