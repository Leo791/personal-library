package com.github.leo791.personal_library.service;

import com.github.leo791.personal_library.model.dto.BookDTO;
import com.github.leo791.personal_library.model.entity.Book;
import com.github.leo791.personal_library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    void testInsertBook_Success() {
        // Arrange
        Book book = new Book("1234567890", "Frankenstein", "Mary Shelley", "Fiction");
        BookDTO bookDTO = new BookDTO(1L, "1234567890", "Frankenstein", "Mary Shelley", "Fiction");

        // Mock
        when(bookMapper.toEntity(bookDTO)).thenReturn(book);

        // Act
        bookService.insertBook(bookDTO);

        // Assert
        verify(bookMapper).toEntity(bookDTO);
        verify(bookRepository).save(book);
    }

    @Test
    void testInsertBook_Failure() {
        // Arrange
        Book book = new Book("1234567890", "Dracula", "Bram Stoker", "Horror");
        BookDTO bookDTO = new BookDTO(1L, "1234567890", "Dracula", "Bram Stoker", "Horror");

        // Mock
        when(bookMapper.toEntity(bookDTO)).thenReturn(book);
        when(bookRepository.save(book)).thenThrow(new RuntimeException("Failed to insert Dracula in Database"));

        // Assert
        assertThrows(RuntimeException.class, () -> bookService.insertBook(bookDTO));

    }
    @Test
    void testGetAllBooks() {
        // Arrange
        Book book1 = new Book("1234567890", "Frankenstein", "Mary Shelley", "Fiction");
        Book book2 = new Book("0987654321", "Dracula", "Bram Stoker", "Horror");
        BookDTO bookDTO1 = new BookDTO(1L, "1234567890", "Frankenstein", "Mary Shelley", "Fiction");
        BookDTO bookDTO2 = new BookDTO(2L, "0987654321", "Dracula", "Bram Stoker", "Horror");
        List<BookDTO> books = List.of(bookDTO1, bookDTO2);

        // Mock
        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));
        when(bookMapper.toDtoList(List.of(book1, book2))).thenReturn(books);

        // Act
        var result = bookService.getAllBooks();

        // Assert
        assertEquals(books, result);
        verify(bookRepository).findAll();
        verify(bookMapper).toDtoList(List.of(book1, book2));
    }

    @Test
    void testGetAllBooks_Empty() {
        // Arrange
        when(bookRepository.findAll()).thenReturn(List.of());
        when(bookMapper.toDtoList(List.of())).thenReturn(List.of());

        // Act
        var result = bookService.getAllBooks();

        // Assert
        assertEquals(List.of(), result);
        verify(bookRepository).findAll();
        verify(bookMapper).toDtoList(List.of());
    }

    @Test
    void testGetBookByIsbn() {
        // Arrange
        String isbn = "1234567890";
        Book book = new Book(isbn, "Scarlet Letter", "Nathaniel Hawthorne", "Fiction");
        BookDTO bookDTO = new BookDTO(1L, isbn, "Scarlet Letter", "Nathaniel Hawthorne", "Fiction");

        // Mock
        when(bookRepository.findByIsbn(isbn)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDTO);

        // Act
        BookDTO result = bookService.getBookByIsbn(isbn);

        // Assert
        assertEquals(bookDTO, result);
        verify(bookRepository).findByIsbn(isbn);
        verify(bookMapper).toDto(book);
    }

    @Test
    void testGetBookByIsbn_NotFound() {
        // Arrange
        String isbn = "1234567890";

        // Mock
        when(bookRepository.findByIsbn(isbn)).thenReturn(null);

        // Act
        BookDTO result = bookService.getBookByIsbn(isbn);

        // Assert
        assertNull(result);
        verify(bookRepository).findByIsbn(isbn);
        verify(bookMapper).toDto(null);
    }

    @Test
    void testGetBooksByTitle() {
        // Two books with the same title but different ISBNs
        // This test checks if the service can handle multiple books with the same title correctly.
        // Arrange
        String title = "The Great Gatsby";
        String author = "F. Scott Fitzgerald";
        String genre = "Fiction";
        Book book1 = new Book("1234567890", title, author, genre);
        Book book2 = new Book("0987654321", title, author, genre);
        BookDTO bookDTO1 = new BookDTO(1L, "1234567890", title, author, genre);
        BookDTO bookDTO2 = new BookDTO(2L, "0987654321", title, author, genre);
        List<BookDTO> books = List.of(bookDTO1, bookDTO2);

        // Mock
        when(bookRepository.findByTitle(title)).thenReturn(List.of(book1, book2));
        when(bookMapper.toDtoList(List.of(book1, book2))).thenReturn(books);

        // Act
        var result = bookService.getBooksByTitle(title);

        // Assert
        assertEquals(books, result);
        verify(bookRepository).findByTitle(title);
        verify(bookMapper).toDtoList(List.of(book1, book2));

    }

    @Test
    void testGetBooksByTitle_NotFound() {
        // Arrange
        String title = "Nonexistent Book";

        // Mock
        when(bookRepository.findByTitle(title)).thenReturn(List.of());
        when(bookMapper.toDtoList(List.of())).thenReturn(List.of());

        // Act
        var result = bookService.getBooksByTitle(title);

        // Assert
        assertEquals(List.of(), result);
        verify(bookRepository).findByTitle(title);
        verify(bookMapper).toDtoList(List.of());
    }

    @Test
    void testGetBooksByAuthor() {
        // Arrange
        String author = "George Orwell";
        Book book1 = new Book("1234567890", "1984", author, "Dystopian");
        Book book2 = new Book("0987654321", "Animal Farm", author, "Political Satire");
        BookDTO bookDTO1 = new BookDTO(1L, "1234567890", "1984", author, "Dystopian");
        BookDTO bookDTO2 = new BookDTO(2L, "0987654321", "Animal Farm", author, "Political Satire");
        List<BookDTO> books = List.of(bookDTO1, bookDTO2);

        // Mock
        when(bookRepository.findByAuthor(author)).thenReturn(List.of(book1, book2));
        when(bookMapper.toDtoList(List.of(book1, book2))).thenReturn(books);

        // Act
        var result = bookService.getBooksByAuthor(author);

        // Assert
        assertEquals(books, result);
        verify(bookRepository).findByAuthor(author);
        verify(bookMapper).toDtoList(List.of(book1, book2));

    }

    @Test
    void testGetBooksByAuthor_NotFound() {
        // Arrange
        String author = "Unknown Author";

        // Mock
        when(bookRepository.findByAuthor(author)).thenReturn(List.of());
        when(bookMapper.toDtoList(List.of())).thenReturn(List.of());

        // Act
        var result = bookService.getBooksByAuthor(author);

        // Assert
        assertEquals(List.of(), result);
        verify(bookRepository).findByAuthor(author);
        verify(bookMapper).toDtoList(List.of());
    }

    @Test
    void testGetBooksByGenre() {
        // Arrange
        String genre = "Science Fiction";
        Book book1 = new Book("1234567890", "Dune", "Frank Herbert", genre);
        Book book2 = new Book("0987654321", "Neuromancer", "William Gibson", genre);
        BookDTO bookDTO1 = new BookDTO(1L, "1234567890", "Dune", "Frank Herbert", genre);
        BookDTO bookDTO2 = new BookDTO(2L, "0987654321", "Neuromancer", "William Gibson", genre);
        List<BookDTO> books = List.of(bookDTO1, bookDTO2);

        // Mock
        when(bookRepository.findByGenre(genre)).thenReturn(List.of(book1, book2));
        when(bookMapper.toDtoList(List.of(book1, book2))).thenReturn(books);

        // Act
        var result = bookService.getBooksByGenre(genre);

        // Assert
        assertEquals(books, result);
        verify(bookRepository).findByGenre(genre);
        verify(bookMapper).toDtoList(List.of(book1, book2));
    }

    @Test
    void testGetBooksByGenre_NotFound() {
        // Arrange
        String genre = "Nonexistent Genre";

        // Mock
        when(bookRepository.findByGenre(genre)).thenReturn(List.of());
        when(bookMapper.toDtoList(List.of())).thenReturn(List.of());

        // Act
        var result = bookService.getBooksByGenre(genre);

        // Assert
        assertEquals(List.of(), result);
        verify(bookRepository).findByGenre(genre);
        verify(bookMapper).toDtoList(List.of());
    }

    @Test
    void deleteBookByIsbn() {
    }
}