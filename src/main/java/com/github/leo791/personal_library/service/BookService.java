package com.github.leo791.personal_library.service;

import com.github.leo791.personal_library.exception.BookInsertException;
import com.github.leo791.personal_library.model.dto.BookDTO;
import com.github.leo791.personal_library.model.entity.Book;
import com.github.leo791.personal_library.repository.BookRepository;
import com.github.leo791.personal_library.exception.BookNotFoundException;
import com.github.leo791.personal_library.util.BookUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Service class for managing Book entities.
 * This class provides methods to interact with the BookRepository.
 */
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    // ================= Insert / Update =================

    /**
     * Saves a book entity to the repository.
     *
     * @param book the book entity to save
     */
    public void insertBook(BookDTO book) {
        try {
            Book bookEntity = bookMapper.toEntity(book);
            // Capitalize string fields in the book entity
            BookUtils.capitalizeStringFields(bookEntity);
            bookRepository.save(bookEntity);
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
    public void updateBook(String isbn, BookDTO newBook) {
        if (newBook.getIsbn() != null && !newBook.getIsbn().equals(isbn)) {
            throw new IllegalArgumentException("ISBN cannot be changed.");
        }
        Book existingBook = bookRepository.findByIsbn(isbn);
        if (existingBook == null) {
            throw new BookNotFoundException(isbn);
        }
        // Update the existing book entity with the new data where applicable
        BookUtils.updateBookFields(existingBook, bookMapper.toEntity(newBook));
        // Capitalize string fields in the updated book entity
        BookUtils.capitalizeStringFields(existingBook);

        bookRepository.save(existingBook);
    }

    // ================= Search =================

    /**
     * Retrieves all books from the repository.
     *
     * @return a list of all books
     */
    public List<BookDTO> getAllBooks() {
        List<Book> bookEntities = bookRepository.findAll();
        return bookMapper.toDtoList(bookEntities);


    }

    /**
     * Retrieves a book entity by its ISBN.
     *
     * @param isbn the ISBN of the book to retrieve
     * @return the book entity with the specified isbn, or null if not found
     */
    public BookDTO getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn);
        return bookMapper.toDto(book);
    }
    // ================= Delete =================

    /**
     * Deletes a book entity by its isbn.
     * @param isbn the isbn of the book to delete
     */
    public void deleteBook(String isbn) {
        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            throw new BookNotFoundException(isbn);
        }
        bookRepository.deleteByIsbn(isbn);
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
    public Iterable<BookDTO> searchBooks(String title, String author, String genre) {
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
        return bookMapper.toDtoList(books);
    }
}
