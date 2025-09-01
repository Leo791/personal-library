package com.github.leo791.personal_library.exception;

public class ExternalBookNotFoundException extends RuntimeException {
    private final String isbn;

    public ExternalBookNotFoundException(String isbn) {
        super("Book with ISBN " + isbn + " not found in external APIs");
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }
}
