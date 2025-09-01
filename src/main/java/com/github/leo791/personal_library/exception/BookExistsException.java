package com.github.leo791.personal_library.exception;

public class BookExistsException extends RuntimeException {
    private final String isbn;

    public BookExistsException(String isbn) {
        super("Book with ISBN " +  isbn + " already exists in Library");
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }
}
