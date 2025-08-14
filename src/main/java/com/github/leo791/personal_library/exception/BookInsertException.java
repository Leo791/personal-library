package com.github.leo791.personal_library.exception;

public class BookInsertException extends RuntimeException {
    public BookInsertException(String title) {

        super("Failed to insert " +  title + " in Database");
    }
}
