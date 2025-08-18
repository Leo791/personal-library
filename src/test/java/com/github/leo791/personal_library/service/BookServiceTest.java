package com.github.leo791.personal_library.service;

import com.github.leo791.personal_library.exception.BookInsertException;
import com.github.leo791.personal_library.model.dto.BookDTO;
import com.github.leo791.personal_library.model.entity.Book;
import com.github.leo791.personal_library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    @Test
    void testInsertBook() {
        // Arrange
        Book book = new Book("1234567890", "Frankenstein", "Mary Shelley", "Fiction");
        BookDTO bookDTO = new BookDTO( "1234567890", "Frankenstein", "Mary Shelley", "Fiction");

        // Mock
        when(bookMapper.toEntity(bookDTO)).thenReturn(book);

        // Act
        bookService.insertBook(bookDTO);

        // Assert
        verify(bookMapper).toEntity(bookDTO);
        verify(bookRepository).save(book);
    }

    @Test
    void testInsertBook_CapitalizeFields() {
        // Arrange
        Book book = new Book("1234567890", "the great gatsby", "f. scott fitzgerald", "fiction");
        BookDTO bookDTO = new BookDTO( "1234567890", "The Great Gatsby", "F. Scott Fitzgerald", "Fiction");
        when(bookMapper.toEntity(bookDTO)).thenReturn(book);

        // Act
        bookService.insertBook(bookDTO);

        // Assert
        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());
        Book savedBook = captor.getValue();
        assertEquals("The Great Gatsby", savedBook.getTitle());
        assertEquals("F. Scott Fitzgerald", savedBook.getAuthor());
        assertEquals("Fiction", savedBook.getGenre());
    }

    @Test
    void testInsertBook_Failure() {
        // Arrange
        Book book = new Book("1234567890", "Dracula", "Bram Stoker", "Horror");
        BookDTO bookDTO = new BookDTO( "1234567890", "Dracula", "Bram Stoker", "Horror");

        // Mock
        when(bookMapper.toEntity(bookDTO)).thenReturn(book);
        when(bookRepository.save(book)).thenThrow(new BookInsertException(book.getTitle()));

        // Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookService.insertBook(bookDTO));
        assertEquals("Failed to insert Dracula in Database", exception.getMessage());
    }

    @Test
    void updateBook() {
        // Arrange
        String isbn = "1234567890";
        Book existingBook = new Book(isbn, "Frankstein", "Bram Stoker", "Fiction");
        Book newBook = new Book(null, "Frankenstein", "Mary Shelley", "Horror");
        BookDTO updatedBookDTO = new BookDTO(null, "Frankenstein", "Mary Shelley", "Horror");

        // Mock
        when(bookRepository.findByIsbn(isbn)).thenReturn(existingBook);
        when(bookMapper.toEntity(updatedBookDTO)).thenReturn(newBook);

        // Act
        bookService.updateBook(isbn, updatedBookDTO);

        // Assert
        verify(bookRepository).findByIsbn(isbn);
        verify(bookMapper).toEntity(updatedBookDTO);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());
        Book savedBook = captor.getValue();
        assertEquals("Frankenstein", savedBook.getTitle());
        assertEquals("Mary Shelley", savedBook.getAuthor());
        assertEquals("Horror", savedBook.getGenre());
    }

    @Test
    void updateBook_NullValues(){
        // Arrange
        String isbn = "1234567890";
        Book existingBook = new Book(isbn, "Frankenstein", "Bram Stoker", "Fiction");
        Book newBook = new Book(null, null, "Mary Shelley", null);
        BookDTO updatedBookDTO = new BookDTO(null, "Frankenstein", "Mary Shelley", "Fiction");

        // Mock
        when(bookRepository.findByIsbn(isbn)).thenReturn(existingBook);
        when(bookMapper.toEntity(updatedBookDTO)).thenReturn(newBook);

        // Act
        bookService.updateBook(isbn, updatedBookDTO);

        // Assert
        verify(bookRepository).findByIsbn(isbn);
        verify(bookMapper).toEntity(updatedBookDTO);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());
        Book savedBook = captor.getValue();
        assertEquals("Frankenstein", savedBook.getTitle());
        assertEquals("Mary Shelley", savedBook.getAuthor());
        assertEquals("Fiction", savedBook.getGenre());
    }

    @Test
    void updateBook_IsbnChange() {
        // Arrange
        String isbn = "1234567890";
        BookDTO updatedBookDTO = new BookDTO("0987654321", "Frankenstein", "Mary Shelley", "Horror");

        // Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> bookService.updateBook(isbn, updatedBookDTO));
        assertEquals("ISBN cannot be changed.", exception.getMessage());
    }

    @Test
    void updateBook_NotFound() {
        // Arrange
        String isbn = "1234567890";
        BookDTO updatedBookDTO = new BookDTO("1234567890", "Frankenstein", "Mary Shelley", "Horror");

        // Mock
        when(bookRepository.findByIsbn(isbn)).thenReturn(null);

        // Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookService.updateBook(isbn, updatedBookDTO));
        assertEquals("Book with ISBN 1234567890 not found in database", exception.getMessage());
    }

    @Test
    void testGetBookByIsbn() {
        // Arrange
        String isbn = "1234567890";
        Book book = new Book(isbn, "Scarlet Letter", "Nathaniel Hawthorne", "Fiction");
        BookDTO bookDTO = new BookDTO( isbn, "Scarlet Letter", "Nathaniel Hawthorne", "Fiction");

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
    void testSearchBooks_All() {
        // Arrange
        Book book1 = new Book("1234567890", "The Great Gatsby", "F. Scott Fitzgerald", "Fiction");
        Book book2 = new Book("0987654321", "To Kill a Mockingbird", "Harper Lee", "Fiction");
        List<Book> books = List.of(book1, book2);

        // Mock
        when(bookRepository.findAll()).thenReturn(books);
        when(bookMapper.toDtoList(books)).thenReturn(List.of(
                new BookDTO("1234567890", "The Great Gatsby", "F. Scott Fitzgerald", "Fiction"),
                new BookDTO("0987654321", "To Kill a Mockingbird", "Harper Lee", "Fiction")
        ));
        // Act
        List<BookDTO> result = bookService.searchBooks(null, null, null);

        // Assert
        assertEquals(2, result.size());
        verify(bookRepository).findAll();
    }

    @Test
    void testSearchBooks_ByTitle() {
        // Arrange
        String title = "The Great Gatsby";
        Book book = new Book("1234567890", title, "F. Scott Fitzgerald", "Fiction");
        List<Book> books = List.of(book);

        // Mock
        when(bookRepository.findByTitleIgnoreCase(title)).thenReturn(books);
        when(bookMapper.toDtoList(books)).thenReturn(List.of(new BookDTO("1234567890", title, "F. Scott Fitzgerald", "Fiction")));

        // Act
        List<BookDTO> result = bookService.searchBooks(title, null, null);

        // Assert
        assertEquals(1, result.size());
        assertEquals(title, result.getFirst().getTitle());
        verify(bookRepository).findByTitleIgnoreCase(title);
    }

    @Test
    void testSearchBooks_ByAuthor() {
        // Arrange
        String author = "Harper Lee";
        Book book = new Book("0987654321", "To Kill a Mockingbird", author, "Fiction");
        List<Book> books = List.of(book);

        // Mock
        when(bookRepository.findByAuthorIgnoreCase(author)).thenReturn(books);
        when(bookMapper.toDtoList(books)).thenReturn(List.of(new BookDTO("0987654321", "To Kill a Mockingbird", author, "Fiction")));

        // Act
        List<BookDTO> result = bookService.searchBooks(null, author, null);

        // Assert
        assertEquals(1, result.size());
        assertEquals(author, result.getFirst().getAuthor());
        verify(bookRepository).findByAuthorIgnoreCase(author);
    }

    @Test
    void testSearchBooks_ByGenre() {
        // Arrange
        String genre = "Fiction";
        Book book = new Book("1234567890", "The Great Gatsby", "F. Scott Fitzgerald", genre);
        Book book1 = new Book("0987654321", "To Kill a Mockingbird", "Harper Lee", genre);
        Book book2 = new Book("1122334455", "1984", "George Orwell", "Distopian");
        List<Book> books = List.of(book, book1, book2);

        // Mock
        when(bookRepository.findByGenreIgnoreCase(genre)).thenReturn(books);
        when(bookMapper.toDtoList(books)).thenReturn(List.of(
                new BookDTO("1234567890", "The Great Gatsby", "F. Scott Fitzgerald", genre),
                new BookDTO("0987654321", "To Kill a Mockingbird", "Harper Lee", genre)
        ));

        // Act
        List<BookDTO> result = bookService.searchBooks(null, null, genre);

        // Assert
        assertEquals(2, result.size());
        assertEquals(genre, result.getFirst().getGenre());
        assertEquals(genre, result.get(1).getGenre());
        verify(bookRepository).findByGenreIgnoreCase(genre);
    }

    @Test
    void testDeleteBook() {
        // Arrange
        String isbn = "1234567890";
        Book book = new Book(isbn, "The Great Gatsby", "F. Scott Fitzgerald", "Fiction");

        // Mock
        when(bookRepository.findByIsbn(isbn)).thenReturn(book);

        // Act
        bookService.deleteBook(isbn);

        // Assert
        verify(bookRepository).findByIsbn(isbn);
        verify(bookRepository).deleteByIsbn(isbn);
    }

    @Test
    void testDeleteBook_NotFound() {
        // Arrange
        String isbn = "1234567890";

        // Mock
        when(bookRepository.findByIsbn(isbn)).thenReturn(null);

        // Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookService.deleteBook(isbn));
        assertEquals("Book with ISBN " + isbn + " not found in database", exception.getMessage());
        verify(bookRepository).findByIsbn(isbn);
    }
}