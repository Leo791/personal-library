package com.github.leo791.personal_library.exception;

public class DatabaseBookNotFoundException extends RuntimeException {
    private final String isbn;

    public DatabaseBookNotFoundException(String isbn) {
        super("Book with ISBN " + isbn + " not found in Library");
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }
}