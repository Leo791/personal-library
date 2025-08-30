package com.github.leo791.personal_library.service;

import com.github.leo791.personal_library.client.GoogleBooksClient;
import com.github.leo791.personal_library.client.LibreTranslateClient;
import com.github.leo791.personal_library.client.OpenLibraryClient;
import com.github.leo791.personal_library.exception.BookExistsException;
import com.github.leo791.personal_library.model.dto.BookDTO;
import com.github.leo791.personal_library.model.entity.Book;
import com.github.leo791.personal_library.model.entity.GoogleBookResponse;
import com.github.leo791.personal_library.model.entity.OpenLibraryBookResponse;
import com.github.leo791.personal_library.repository.BookRepository;
import com.github.leo791.personal_library.exception.BookNotFoundException;
import com.github.leo791.personal_library.util.BookUtils;
import com.github.leo791.personal_library.util.IsbnUtils;
import com.github.leo791.personal_library.util.OpenLibraryResponseMapperUtils;
import com.github.leo791.personal_library.util.TranslationUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
/**
 * Service class for managing Book entities.
 * This class provides methods to interact with the BookRepository.
 */
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final GoogleBooksClient googleBooksClient;
    private final LibreTranslateClient libreTranslateClient;
    private final OpenLibraryClient openLibraryClient;
    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    public BookService(BookRepository bookRepository, BookMapper bookMapper,
                       GoogleBooksClient googleBooksClient, LibreTranslateClient libreTranslateClient,
                       OpenLibraryClient openLibraryClient) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.googleBooksClient = googleBooksClient;
        this.libreTranslateClient = libreTranslateClient;
        this.openLibraryClient = openLibraryClient;
    }

    // ================= Insert / Update =================

    /**
     * Inserts a new book entity into the repository using its ISBN.
     * If the book already exists, it throws a BookExistsException.
     * If the ISBN is invalid or the book is not found in Google Books API, it throws an appropriate exception.
     *
     * @param isbn the ISBN of the book to insert
     * @return the inserted BookDTO
     */
    public BookDTO insertBookFromIsbn(String isbn) throws Exception {
        Book book = null;
        // Validate the ISBN format
        if(!IsbnUtils.isValidIsbn(isbn)) {
            throw new IllegalArgumentException("Invalid ISBN: " + isbn);
        }
        // Check if the book already exists in the repository
        if (bookRepository.existsByIsbn(isbn)) {
            throw new BookExistsException(isbn);
        }
        // Try to fetch the book from Google Books API
        GoogleBookResponse googleBook = googleBooksClient.fetchBookByIsbn(isbn);
        // If book is found in Google Books API, map it to a Book entity
        if (GoogleBookResponse.getTotalItems() != 0) {
            log.info("Book with ISBN {} found in Google Books API", isbn);
            book = bookMapper.fromGoogleResponseToBook(googleBook);
            BookUtils.capitalizeStringFields(book);
            // If book is not found in Google Books API, try Open Library API
        } else {
            log.warn("Book with ISBN {} not found in Google Books API. Trying Open Library API", isbn);
            OpenLibraryBookResponse openLibraryBook = searchBookOnOpenLibrary(isbn);
            String author = getAuthorFromOpenLibraryBook(openLibraryBook.getAuthors());
            book = bookMapper.fromOpenLibraryResponseToBook(openLibraryBook, author);
            BookUtils.capitalizeStringFields(book);
        }

        // Check if description language matches the book language, if not translate it
        String detectedLanguage = detectDescriptionLanguage(book.getDescription());
        if (TranslationUtils.isTranslationRequired(detectedLanguage, book.getLanguage())) {
            log.info("Translating description from {} to {}", detectedLanguage.toUpperCase(), book.getLanguage());
            try {
                String translatedDescription = libreTranslateClient.translate(
                        book.getDescription(), detectedLanguage, book.getLanguage());
                book.setDescription(translatedDescription);
            } catch (Exception e) {
                log.error("Translation failed for ISBN {}: {}", isbn, e.getMessage());
                // Proceed with the original description if translation fails
            }
        } else {
            log.info("No description translation required for book with ISBN {}", isbn);
        }

        // Set the ISBN from the request if Google Books API does not provide it
        if (book.getIsbn().isBlank()) {
            book.setIsbn(isbn.replace("-", ""));
        }

        // Save the book entity
        bookRepository.save(book);

        // Return the saved book DTO
        return  bookMapper.bookToDto(book);
    }

    /**
     * Updates an existing book entity in the repository.
     * If the ISBN is changed, it throws an IllegalArgumentException.
     * If the book does not exist, it throws a BookNotFoundException.
     *
     * @param newBook the new BookDTO with updated data
     */
    @Transactional
    public BookDTO updateBook(BookDTO newBook) {
        // Check if ISBN is provided throwing IllegalArgumentException if not
        if(newBook.getIsbn() == null || newBook.getIsbn().isBlank()) {
            throw new IllegalArgumentException("ISBN must be provided in the update request.");
        }
        // Check if ISBN is valid
        if(!IsbnUtils.isValidIsbn(newBook.getIsbn())) {
            throw new IllegalArgumentException("Invalid ISBN: " + newBook.getIsbn());
        }
        // Check if book with the given ISBN exists
        String isbn = newBook.getIsbn();
        Book existingBook = bookRepository.findByIsbn(isbn);
        if (existingBook == null) {
            throw new BookNotFoundException(isbn, "Library");
        }
        // Update the existing book entity with the new data where applicable
        BookUtils.updateBookFields(existingBook, bookMapper.DTOtoBook(newBook));

        // Capitalize string fields in the updated book entity
        BookUtils.capitalizeStringFields(existingBook);

        bookRepository.save(existingBook);

        return bookMapper.bookToDto(existingBook);
    }

    // ================= Search =================

    /**
     * Retrieves a book entity by its ISBN.
     *
     * @param isbn the ISBN of the book to retrieve
     * @return the book entity with the specified isbn, or null if not found
     */
    public BookDTO getBookByIsbn(String isbn) {
        // Validate the ISBN format
        if(!IsbnUtils.isValidIsbn(isbn)) {
            throw new IllegalArgumentException("Invalid ISBN: " + isbn);
        }
        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            throw new BookNotFoundException(isbn, "Library");
        }
        return bookMapper.bookToDto(book);
    }

    /**
     * Searches for books based on title, author, or genre.
     * If no parameters are provided, it returns all books.
     * If multiple parameters are provided, it prioritizes title > author > genre.
     * @param title the title of the book to search for (optional)
     * @param author the author of the book to search for (optional)
     * @param genre the genre of the book to search for (optional)
     * @return a list of BookDTO objects that match the search criteria
     */
    public List<BookDTO> searchBooks(String title, String author, String genre) {
        List<Book> books;
        if (title != null) {
            books = bookRepository.findByTitleContainingIgnoreCase(title);
        } else if (author != null) {
            books = bookRepository.findByAuthorContainingIgnoreCase(author);
        } else if (genre != null) {
            books = bookRepository.findByGenreContainingIgnoreCase(genre);
        } else {
            books = bookRepository.findAll();
        }
        return bookMapper.bookListToDtoList(books);
    }

    // ================= Delete =================

    /**
     * Deletes a book entity by its isbn.
     * @param isbn the isbn of the book to delete
     */
    @Transactional
    public void deleteBook(String isbn) {
        // Validate the ISBN format
        if(!IsbnUtils.isValidIsbn(isbn)) {
            throw new IllegalArgumentException("Invalid ISBN: " + isbn);
        }
        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            throw new BookNotFoundException(isbn, "Library");
        }
        bookRepository.deleteByIsbn(isbn);
    }

    // ================= Private Methods =================

   private String detectDescriptionLanguage(String description) {
         if (description == null || description.isBlank()) {
              log.warn("Description is empty or null, cannot detect language.");
              return "unknown";
         }
       try {
           return libreTranslateClient.detect(description);
       } catch (Exception e) {
           log.error("Failed to detect language for description: {}", description, e);
           return "unknown";
       }
   }

   private OpenLibraryBookResponse searchBookOnOpenLibrary(String isbn) {
       try {
           OpenLibraryBookResponse openLibraryBook = openLibraryClient.fetchBookByIsbn(isbn);
           log.info("Book with ISBN {} found in Open Library API", isbn);
           return openLibraryBook;
       } catch (Exception e) {
           log.error("Error fetching book with ISBN {} from Open Library API: {}", isbn, e.getMessage());
           throw new BookNotFoundException(isbn, "Google Books or Open Library APIs");
       }
   }

    private String getAuthorFromOpenLibraryBook(List<OpenLibraryBookResponse.AuthorKey> authors) throws Exception {
        String author = OpenLibraryResponseMapperUtils.extractFirstAuthor(authors);
        if (author.isBlank()) {
            log.warn("Author is not provided by Open Library API");
            return "";
        } else {
            return openLibraryClient.fetchAuthorByKey(author);
        }
    }
}
