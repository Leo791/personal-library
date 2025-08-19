package com.github.leo791.personal_library.exception;

public class BookExistsException extends RuntimeException {
    public BookExistsException(String isbn) {
        super("Book with isbn " +  isbn + " already exists in Database");
    }
}
