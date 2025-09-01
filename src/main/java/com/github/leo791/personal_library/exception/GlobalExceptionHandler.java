package com.github.leo791.personal_library.exception;

import com.github.leo791.personal_library.model.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BookExistsException.class)
    public ResponseEntity<ErrorResponse> handleBookExists(BookExistsException ex) {
        log.warn("{}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                ex.getIsbn(),
                "Look up the book on the library.",
                Map.of("searchBook", "/api/v1/books/" + ex.getIsbn())
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DatabaseBookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFoundInDatabase(DatabaseBookNotFoundException ex) {
        log.warn("{}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                ex.getIsbn(),
                "Insert the book first.",
                Map.of("insertBook", "/api/v1/books/" + ex.getIsbn())

        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                "Invalid argument",
                null,
                "Check the request parameters.",
                null
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExternalBookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExternalBookNotFound(ExternalBookNotFoundException ex) {
        log.warn("Book not found in external APIs: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                "Book not found in external APIs",
                ex.getIsbn(),
                "Add the book manually.",
                Map.of("manualAdd", "/api/v1/books/manual")
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(
                "An unexpected error occurred",
                null,
                "Try again later or contact support.",
                null
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}