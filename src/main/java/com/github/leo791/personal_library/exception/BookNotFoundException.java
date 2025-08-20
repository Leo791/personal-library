package com.github.leo791.personal_library.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String isbn, String location) {
        super("Book with ISBN " + isbn + " not found in " + location);
    }
}