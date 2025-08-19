package com.github.leo791.personal_library.service;

import com.github.leo791.personal_library.client.GoogleBooksClient;
import com.github.leo791.personal_library.exception.BookExistsException;
import com.github.leo791.personal_library.exception.BookInsertException;
import com.github.leo791.personal_library.model.dto.BookDTO;
import com.github.leo791.personal_library.model.entity.Book;
import com.github.leo791.personal_library.model.entity.GoogleBookResponse;
import com.github.leo791.personal_library.repository.BookRepository;
import com.github.leo791.personal_library.exception.BookNotFoundException;
import com.github.leo791.personal_library.util.BookUtils;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    public BookService(BookRepository bookRepository, BookMapper bookMapper,
                       GoogleBooksClient googleBooksClient) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        String apiKey = System.getenv("GOOGLE_BOOKS_API_KEY");
        this.googleBooksClient = new GoogleBooksClient(new RestTemplate(), apiKey);
    }

    // ================= Insert / Update =================

    /**
     * Saves a book entity to the repository.
     *
     * @param isbn of the book  to save
     */
    public BookDTO insertBookFromIsbn(String isbn) {
        Book book = null;
        try {
            // Check if the book already exists in the repository
            if (bookRepository.existsByIsbn(isbn)) {
                throw new BookExistsException(isbn);
            }
            // Fetch the book details from Google Books API
            GoogleBookResponse googleBook = googleBooksClient.fetchBookByIsbn(isbn);
            if (googleBook == null || GoogleBookResponse.getItems() == null || GoogleBookResponse.getItems().isEmpty()) {
                throw new BookNotFoundException(isbn);
            }

            // Map the GoogleBookResponse to a Book entity
            book = bookMapper.fromGoogleResponseToBook(googleBook);

            // Save the book entity
            bookRepository.save(book);

            // Return the saved book DTO
            return  bookMapper.bookToDto(book);


        } catch (DataAccessException e) {
            throw new BookInsertException(book.getTitle());
        }
    }

    /**
     * Updates an existing book entity by its ISBN.
     *
     * @param isbn the ISBN of the book to update
     * @param newBook the book entity with updated data
     */
    @Transactional
    public void updateBook(String isbn, BookDTO newBook) {
        if (newBook.getIsbn() != null && !newBook.getIsbn().equals(isbn)) {
            throw new IllegalArgumentException("ISBN cannot be changed.");
        }
        Book existingBook = bookRepository.findByIsbn(isbn);
        if (existingBook == null) {
            throw new BookNotFoundException(isbn);
        }
        // Update the existing book entity with the new data where applicable
        BookUtils.updateBookFields(existingBook, bookMapper.DTOtoBook(newBook));
        // Capitalize string fields in the updated book entity
        BookUtils.capitalizeStringFields(existingBook);

        bookRepository.save(existingBook);
    }

    // ================= Search =================

    /**
     * Retrieves a book entity by its ISBN.
     *
     * @param isbn the ISBN of the book to retrieve
     * @return the book entity with the specified isbn, or null if not found
     */
    public BookDTO getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn);
        return bookMapper.bookToDto(book);
    }

    /**
     * Searches for books based on title, author, or genre.
     * If no parameters are provided, it returns all books.
     * If multiple parameters are provided, it prioritizes title > author > genre.
     * @param title the title of the book to search for (optional)
     * @param author the author of the book to search for (optional)
     * @param genre the genre of the book to search for (optional)
     * @return an iterable of BookDTO objects that match the search criteria
     */
    public List<BookDTO> searchBooks(String title, String author, String genre) {
        List<Book> books;
        if (title != null) {
            books = bookRepository.findByTitleIgnoreCase(title);
        } else if (author != null) {
            books = bookRepository.findByAuthorIgnoreCase(author);
        } else if (genre != null) {
            books = bookRepository.findByGenreIgnoreCase(genre);
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
        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            throw new BookNotFoundException(isbn);
        }
        bookRepository.deleteByIsbn(isbn);
    }

}
