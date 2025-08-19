package com.github.leo791.personal_library.exception;

public class BookExistsException extends RuntimeException {
    public BookExistsException(String isbn) {
        super("Book with ISBN " +  isbn + " already exists in Library");
    }
}
