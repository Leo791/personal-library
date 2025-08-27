package com.github.leo791.personal_library.service;

import com.github.leo791.personal_library.client.GoogleBooksClient;
import com.github.leo791.personal_library.client.LibreTranslateClient;
import com.github.leo791.personal_library.model.dto.BookDTO;
import com.github.leo791.personal_library.model.entity.Book;
import com.github.leo791.personal_library.model.entity.GoogleBookResponse;
import com.github.leo791.personal_library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Unit tests for the BookService class.
 * This class is responsible for testing the functionality of the BookService,
 * which includes inserting, retrieving, and deleting books.
 */

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    private GoogleBookResponse mockResponse;
    // This is a valid ISBN for testing purposes.
    String isbn = "9780553212471";
    // This is an invalid ISBN for testing purposes.
    String invalidIsbn = "9783161484101";
    
    Book Frankestein = new Book(isbn, "Frankenstein", "Mary Shelley", "Horror",
            "A novel about a scientist who creates a creature in an unorthodox experiment.",
            "EN", 280, "Lackington, Hughes, Harding, Mavor & Jones", "1818");

    BookDTO FrankesteinDTO = new BookDTO(isbn, "Frankenstein", "Mary Shelley", "Horror",
            "A novel about a scientist who creates a creature in an unorthodox experiment.",
            "EN", 280, "Lackington, Hughes, Harding, Mavor & Jones", "1818");

    Book ToKillAMockingbird = new Book("0987654321", "To Kill a Mockingbird", "Harper Lee", "Fiction",
            "A novel about the serious issues of racism and injustice in the Deep South.",
            "EN", 281, "J.B. Lippincott & Co.", "1960");

    BookDTO ToKillAMockingbirdDTO = new BookDTO("0987654321", "To Kill a Mockingbird", "Harper Lee", "Fiction",
            "A novel about the serious issues of racism and injustice in the Deep South.",
            "EN", 281, "J.B. Lippincott & Co.", "1960");

    Book AnimalFarm = new Book("1122334455", "Animal Farm", "George Orwell", "Fiction",
            "A satirical allegory of the Russian Revolution and the rise of Stalinism.",
            "EN", 112, "Secker & Warburg", "1945");

    BookDTO AnimalFarmDTO = new BookDTO("1122334455", "Animal Farm", "George Orwell", "Fiction",
            "A satirical allegory of the Russian Revolution and the rise of Stalinism.",
            "EN", 112, "Secker & Warburg", "1945");

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private GoogleBooksClient googleBooksClient;

    @Mock
    private LibreTranslateClient libreTranslateClient;

    @InjectMocks
    private BookService bookService;

    private void setUpGoogleBooksResponse() {
        // Arrange a googleBooksClient response
        GoogleBookResponse.IndustryIdentifier isbn10 = new GoogleBookResponse.IndustryIdentifier("ISBN_10", isbn);
        GoogleBookResponse.VolumeInfo volumeInfo = new GoogleBookResponse.VolumeInfo();
        volumeInfo.setTitle("Frankenstein");
        volumeInfo.setAuthors(List.of("Mary Shelley"));
        volumeInfo.setCategories(List.of("Horror"));
        volumeInfo.setIndustryIdentifiers(List.of(isbn10));

        GoogleBookResponse.Item item = new GoogleBookResponse.Item();
        item.setVolumeInfo(volumeInfo);


        this.mockResponse = new GoogleBookResponse();
        this.mockResponse.setTotalItems(1);
        this.mockResponse.setItems(List.of(item));
    }

    @Test
    void insertBookFromIsbn_NewBook() throws Exception {
        // Arrange

        setUpGoogleBooksResponse();

        // Mock
        // Simulate that the book does not exist in the repository
        when(bookRepository.existsByIsbn(isbn)).thenReturn(false);

        // Simulate the GoogleBooksClient returning a book response
        when(googleBooksClient.fetchBookByIsbn(isbn)).thenReturn(mockResponse);
        when(bookMapper.fromGoogleResponseToBook(any(GoogleBookResponse.class)))
                .thenReturn(Frankestein);
        when(bookMapper.bookToDto(any(Book.class))).thenReturn(FrankesteinDTO);
        when(libreTranslateClient.detect(Frankestein.getDescription()))
                .thenReturn("en");

        // Act
        BookDTO result = bookService.insertBookFromIsbn(isbn);

        // Assert
        assertEquals(FrankesteinDTO, result);
        verify(bookRepository).existsByIsbn(isbn);
        verify(bookMapper).bookToDto(Frankestein);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());
        Book savedBook = captor.getValue();
        assertEquals("Frankenstein", savedBook.getTitle());
    }

    @Test
    void insertBookFromIsbn_ExistingBook() {
        // Arrange
        String isbn = "9780441172719";

        // Mock
        when(bookRepository.existsByIsbn(isbn)).thenReturn(true);

        // Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookService.insertBookFromIsbn(isbn));
        assertEquals("Book with ISBN 9780441172719 already exists in Library", exception.getMessage());
        verify(bookRepository).existsByIsbn(isbn);
    }

    @Test
    void insertBookFromIsbn_BookNotFound() {
        String isbn = "9780441172719";
        GoogleBookResponse responseWithNullItems = new GoogleBookResponse();
        responseWithNullItems.setTotalItems(0);;

        when(bookRepository.existsByIsbn(isbn)).thenReturn(false);
        when(googleBooksClient.fetchBookByIsbn(isbn)).thenReturn(responseWithNullItems);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookService.insertBookFromIsbn(isbn));
        assertEquals("Book with ISBN 9780441172719 not found in Google Books API", exception.getMessage());
        verify(bookRepository).existsByIsbn(isbn);
    }

    @Test
    void insertBookFromIsbn_DatabaseError() throws Exception {
        // Arrange
        String isbn = "9780441172719";
        setUpGoogleBooksResponse();
        // Mock
        when(bookRepository.existsByIsbn(isbn)).thenReturn(false);
        when(googleBooksClient.fetchBookByIsbn(isbn)).thenReturn(mockResponse);
        when(bookMapper.fromGoogleResponseToBook(any(GoogleBookResponse.class)))
                .thenReturn(Frankestein);
        when(libreTranslateClient.detect(Frankestein.getDescription()))
                .thenReturn("en");

        // Simulate a database error
        doThrow(new DataAccessException("Database error") {
        }).when(bookRepository).save(any(Book.class));

        // Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookService.insertBookFromIsbn(isbn));
        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void insertBookFromIsbn_ResponseHasNotIsbn() throws Exception {
        // Arrange
        String isbn = "9780441172719";
        setUpGoogleBooksResponse();
        // Make the mapper return a book without ISBN
        Frankestein.setIsbn("");
        FrankesteinDTO.setIsbn("9780441172719");

        // Mock
        when(bookRepository.existsByIsbn(isbn)).thenReturn(false);
        when(googleBooksClient.fetchBookByIsbn(isbn)).thenReturn(mockResponse);
        when(bookMapper.fromGoogleResponseToBook(any(GoogleBookResponse.class)))
                .thenReturn(Frankestein);
        when(bookMapper.bookToDto(any(Book.class))).thenReturn(FrankesteinDTO);
        when(libreTranslateClient.detect(Frankestein.getDescription()))
                .thenReturn("en");
        // Act
        BookDTO result = bookService.insertBookFromIsbn(isbn);

        // Assert
        assertEquals(FrankesteinDTO, result);
        assertEquals(isbn.replace("-", ""), result.getIsbn());
    }

    @Test
    void insertBookFromIsbn_TranslationRequired() throws Exception {
        // Arrange
        String isbn = "9780441172719";
        String spanishDescription = "Una novela sobre un científico que crea una criatura en un experimento poco ortodoxo.";
        setUpGoogleBooksResponse();
        Frankestein.setLanguage("es"); // Set book language to Spanish to trigger translation
        FrankesteinDTO.setDescription(spanishDescription); // Spanish description is expected in result

        // Mock
        when(bookRepository.existsByIsbn(isbn)).thenReturn(false);
        when(googleBooksClient.fetchBookByIsbn(isbn)).thenReturn(mockResponse);
        when(bookMapper.fromGoogleResponseToBook(any(GoogleBookResponse.class)))
                .thenReturn(Frankestein);
        when(libreTranslateClient.detect(Frankestein.getDescription()))
                .thenReturn("en");
        when(bookMapper.bookToDto(any(Book.class))).thenReturn(FrankesteinDTO);

        // Act
        BookDTO result = bookService.insertBookFromIsbn(isbn);

        // Assert
        assertEquals(FrankesteinDTO, result);
        assertEquals("Una novela sobre un científico que crea una criatura en un experimento poco ortodoxo.", result.getDescription());
    }



    @Test
    void updateBook() {
        // Arrange
        Book newFrankenstein = new Book(isbn, "Frankenstein", "Mary Shelley", "Fiction",
                "A novel about a scientist who creates a creature in an unorthodox experiment.",
                "EN", 280, "Lackington, Hughes, Harding, Mavor & Jones", "1818");
        BookDTO updatedFrankensteinDTO = new BookDTO(isbn, "Frankenstein", "Mary Shelley", "Fiction",
                "A novel about a scientist who creates a creature in an unorthodox experiment.",
                "EN", 280, "Lackington, Hughes, Harding, Mavor & Jones", "1818");

        // Mock
        when(bookRepository.findByIsbn(isbn)).thenReturn(Frankestein);
        when(bookMapper.DTOtoBook(updatedFrankensteinDTO)).thenReturn(newFrankenstein);

        // Act
        bookService.updateBook(updatedFrankensteinDTO);

        // Assert
        verify(bookRepository).findByIsbn(isbn);
        verify(bookMapper).DTOtoBook(updatedFrankensteinDTO);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());
        Book savedBook = captor.getValue();
        assertEquals("Frankenstein", savedBook.getTitle());
        assertEquals("Mary Shelley", savedBook.getAuthor());
        assertEquals("Fiction", savedBook.getGenre());
    }

    @Test
    void updateBook_NullValues() {
        // Arrange
        Book existingBook = new Book(isbn, "Frankenstein", "Bram Stoker", "Fiction",
                "A novel about a scientist who creates a creature in an unorthodox experiment.",
                "EN", 280, "Lackington, Hughes, Harding, Mavor & Jones", "1818");

        // Mock
        when(bookRepository.findByIsbn(isbn)).thenReturn(existingBook);
        when(bookMapper.DTOtoBook(FrankesteinDTO)).thenReturn(Frankestein);

        // Act
        bookService.updateBook(FrankesteinDTO);

        // Assert
        verify(bookRepository).findByIsbn(isbn);
        verify(bookMapper).DTOtoBook(FrankesteinDTO);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());
        Book savedBook = captor.getValue();
        assertEquals("Frankenstein", savedBook.getTitle());
        assertEquals("Mary Shelley", savedBook.getAuthor());
        assertEquals("Horror", savedBook.getGenre());
    }

    @Test
    void updateBook_NullIsbn() {
        // Arrange
        BookDTO updatedBookDTO = new BookDTO(null, "Frankenstein", "Mary Shelley", "Horror",
                "A novel about a scientist who creates a creature in an unorthodox experiment.",
                "EN", 280, "Lackington, Hughes, Harding, Mavor & Jones", "1818");

        // Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> bookService.updateBook( updatedBookDTO));
        assertEquals("ISBN must be provided in the update request.", exception.getMessage());
    }

    @Test
    void updateBook_BlankIsbn() {
        // Arrange
        BookDTO updatedBookDTO = new BookDTO("", "Frankenstein", "Mary Shelley", "Horror",
                "A novel about a scientist who creates a creature in an unorthodox experiment.",
                "EN", 280, "Lackington, Hughes, Harding, Mavor & Jones", "1818");

        // Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> bookService.updateBook( updatedBookDTO));
        assertEquals("ISBN must be provided in the update request.", exception.getMessage());
    }

    @Test
    void updateBook_InvalidIsbn() {
        // Arrange
        BookDTO updatedBookDTO = new BookDTO(invalidIsbn, "Frankenstein", "Mary Shelley", "Horror",
                "A novel about a scientist who creates a creature in an unorthodox experiment.",
                "EN", 280, "Lackington, Hughes, Harding, Mavor & Jones", "1818");

        // Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> bookService.updateBook(updatedBookDTO));
        assertEquals("Invalid ISBN: " + invalidIsbn, exception.getMessage());
    }

    @Test
    void updateBook_NotFound() {
        // Arrange

        // Mock
        when(bookRepository.findByIsbn(isbn)).thenReturn(null);

        // Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookService.updateBook(FrankesteinDTO));
        assertEquals("Book with ISBN " + isbn + " not found in Library", exception.getMessage());
    }

    @Test
    void testGetBookByIsbn() {
        // Mock
        when(bookRepository.findByIsbn(isbn)).thenReturn(Frankestein);
        when(bookMapper.bookToDto(Frankestein)).thenReturn(FrankesteinDTO);

        // Act
        BookDTO result = bookService.getBookByIsbn(isbn);

        // Assert
        assertEquals(FrankesteinDTO, result);
        verify(bookRepository).findByIsbn(isbn);
        verify(bookMapper).bookToDto(Frankestein);
    }

    @Test
    void testGetBookByIsbn_NotFound() {

        // Mock
        when(bookRepository.findByIsbn(isbn)).thenReturn(null);

        // Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookService.updateBook(FrankesteinDTO));
        assertEquals("Book with ISBN " + isbn + " not found in Library", exception.getMessage());
    }

    @Test
    void testGetBookByIsbn_InvalidIsbn() {

        // Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> bookService.getBookByIsbn(invalidIsbn));
        assertEquals("Invalid ISBN: " + invalidIsbn, exception.getMessage());
    }

    @Test
    void testSearchBooks_All() {
        // Arrange
        List<Book> books = List.of(Frankestein, ToKillAMockingbird);

        // Mock
        when(bookRepository.findAll()).thenReturn(books);
        when(bookMapper.bookListToDtoList(books)).thenReturn(List.of(
                FrankesteinDTO, ToKillAMockingbirdDTO

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
        String title = "To Kill a Mockingbird";
        List<Book> books = List.of(ToKillAMockingbird);

        // Mock
        when(bookRepository.findByTitleIgnoreCase(title)).thenReturn(books);
        when(bookMapper.bookListToDtoList(books)).thenReturn(List.of(ToKillAMockingbirdDTO));

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
        List<Book> books = List.of(ToKillAMockingbird);

        // Mock
        when(bookRepository.findByAuthorIgnoreCase(author)).thenReturn(books);
        when(bookMapper.bookListToDtoList(books)).thenReturn(List.of(ToKillAMockingbirdDTO));

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
        List<Book> books = List.of(Frankestein, ToKillAMockingbird, AnimalFarm);

        // Mock
        when(bookRepository.findByGenreIgnoreCase(genre)).thenReturn(books);
        when(bookMapper.bookListToDtoList(books)).thenReturn(List.of(ToKillAMockingbirdDTO, AnimalFarmDTO
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

        // Mock
        when(bookRepository.findByIsbn(isbn)).thenReturn(Frankestein);

        // Act
        bookService.deleteBook(isbn);

        // Assert
        verify(bookRepository).findByIsbn(isbn);
        verify(bookRepository).deleteByIsbn(isbn);
    }

    @Test
    void testDeleteBook_NotFound() {

        // Mock
        when(bookRepository.findByIsbn(isbn)).thenReturn(null);

        // Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookService.deleteBook(isbn));
        assertEquals("Book with ISBN " + isbn + " not found in Library", exception.getMessage());
        verify(bookRepository).findByIsbn(isbn);
    }

    @Test
    void testDeleteBook_InvalidIsbn() {
        // Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> bookService.deleteBook(invalidIsbn));
        assertEquals("Invalid ISBN: " + invalidIsbn, exception.getMessage());
    }
}
