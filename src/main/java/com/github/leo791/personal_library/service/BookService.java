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

    /**
     * Retrieves books entity by the title.
     *
     * @param title the title of the book to retrieve
     * @return the books with the specified title, or null if not found
     */
    public List<BookDTO> getBooksByTitle(String title) {
        List<Book> books = bookRepository.findByTitleIgnoreCase(title);
        return bookMapper.toDtoList(books);
    }

    /**
     * Retrieves books by their author
     * @param author the author of the books to retrieve
     * @return the books with the specified author, or null if not found
     */
    public List<BookDTO> getBooksByAuthor(String author) {
       List<Book> books = bookRepository.findByAuthorIgnoreCase(author);
       return bookMapper.toDtoList(books);
    }

    /**
     * Retrieves books by their genre
     * @param genre the author of the books to retrieve
     * @return the books with the specified genre, or null if not found
     */
    public List<BookDTO> getBooksByGenre(String genre) {
        List<Book> books =  bookRepository.findByGenreIgnoreCase(genre);
        return bookMapper.toDtoList(books);
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


}
