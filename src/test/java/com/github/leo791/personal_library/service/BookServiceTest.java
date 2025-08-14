package com.github.leo791.personal_library.service;

import com.github.leo791.personal_library.model.dto.BookDTO;
import com.github.leo791.personal_library.model.entity.Book;
import com.github.leo791.personal_library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Unit tests for the BookService class.
 * This class is responsible for testing the functionality of the BookService,
 * which includes inserting, retrieving, and deleting books.
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {


    // I want to use classics in this test class, think frankestine, dracula, for whom the bell tolls, etc.
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    @Test
    void insertBook_Success() {
        // Arrange
        Book book = new Book("1234567890", "Frankenstein", "Mary Shelley", "Fiction");
        BookDTO bookDTO = new BookDTO(1234L, "1234567890", "Frankenstein", "Mary Shelley", "Fiction");

        // Mock
        when(bookMapper.toEntity(bookDTO)).thenReturn(book);

        // Act
        bookService.insertBook(bookDTO);

        // Assert
        verify(bookMapper).toEntity(bookDTO);
        verify(bookRepository).save(book);
    }

    @Test
    void insertBook_Failure(){
        // Arrange
        Book book = new Book("1234567890", "Dracula", "Bram Stoker", "Horror");
        BookDTO bookDTO = new BookDTO(1234L, "1234567890", "Dracula", "Bram Stoker", "Horror");

        // Mock
        when(bookMapper.toEntity(bookDTO)).thenReturn(book);
        when(bookRepository.save(book)).thenThrow(new RuntimeException("Failed to insert Dracula in Database"));

        // Assert
        assertThrows(RuntimeException.class, () -> bookService.insertBook(bookDTO));

    }
    @Test
    void getAllBooks() {
    }

    @Test
    void getBookByIsbn() {
    }

    @Test
    void getBooksByTitle() {
    }

    @Test
    void getBooksByAuthor() {
    }

    @Test
    void getBooksByGenre() {
    }

    @Test
    void deleteBookByIsbn() {
    }
}