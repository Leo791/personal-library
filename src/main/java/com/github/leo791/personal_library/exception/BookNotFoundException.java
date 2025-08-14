package com.github.leo791.personal_library.exception;

/**
 * Custom exception class for handling library-related errors.
 * This exception is thrown when a book is not found in the database.
 */

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String isbn) {
        super("Book with ISBN " + isbn + " not found in database");
    }
}