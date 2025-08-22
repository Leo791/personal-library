package com.github.leo791.personal_library.exception;

import com.github.leo791.personal_library.controller.BookController;
import com.github.leo791.personal_library.repository.BookRepository;
import com.github.leo791.personal_library.service.BookService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class GlobalExceptionHandlerTest {

    @TestConfiguration
    static class MockConfig {
        @Bean
        public BookService bookService() {
            return Mockito.mock(BookService.class);
        }
        @Bean
        public BookRepository bookRepository() {
            return Mockito.mock(BookRepository.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    private final String isbn = "1234567890";

    @Test
    void handleBookNotFoundException() throws Exception {

        Mockito.when(bookService.getBookByIsbn(isbn))
               .thenThrow(new BookNotFoundException(isbn, "Library"));

        mockMvc.perform(get("/api/v1/books/1234567890")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\":\"Book with ISBN 1234567890 not found in Library\"}"));
    }

    @Test
    void handleBookExistsException() throws Exception {
        Mockito.when(bookService.insertBookFromIsbn(isbn))
               .thenThrow(new BookExistsException(isbn));

        mockMvc.perform(post("/api/v1/books?isbn=" + isbn)
                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isConflict())
               .andExpect(content().json("{\"message\":\"Book with ISBN " + isbn + " already exists in Library\"}"));
    }

    @Test
    void handleUnexpectedException() throws Exception {
        Mockito.when(bookService.getBookByIsbn(anyString()))
               .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/v1/books/1234567890")
                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isInternalServerError())
               .andExpect(content().json("{\"message\":\"An unexpected error occurred.\"}"));
    }

    @Test
    void handleIllegalArgumentException() throws Exception {
        Mockito.when(bookService.insertBookFromIsbn("invalid_isbn"))
               .thenThrow(new IllegalArgumentException("Invalid ISBN format: invalid_isbn"));

        mockMvc.perform(post("/api/v1/books?isbn=invalid_isbn")
                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(content().json("{\"message\":\"Invalid ISBN format: invalid_isbn\"}"));
    }
}
