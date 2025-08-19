package com.github.leo791.personal_library.controller;

import com.github.leo791.personal_library.exception.BookExistsException;
import com.github.leo791.personal_library.exception.BookNotFoundException;
import com.github.leo791.personal_library.model.dto.BookDTO;
import com.github.leo791.personal_library.model.dto.ErrorResponse;
import com.github.leo791.personal_library.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing book-related operations.
 * This class handles HTTP requests related to books, such as retrieving, adding, updating, and deleting books.
 */
@RestController
@RequestMapping("api/v1/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // ================= Insert / Update =================

    /**
     * This method handles POST requests to add a new book.
     * It expects a JSON object in the request body, with the book's data.
     * @param isbn the book object to be added
     */
    @PostMapping
    public ResponseEntity<BookDTO> addNewBook(@RequestParam String isbn) {
        try {
            BookDTO createdBook = bookService.insertBookFromIsbn(isbn);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
        } catch (Exception e) {
            return switch (e) {
                case IllegalArgumentException illegalArgumentException ->
                        ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                case BookNotFoundException bookNotFoundException ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                case BookExistsException bookExistsException ->
                        ResponseEntity.status(HttpStatus.CONFLICT).build();
                default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            };
        }
    }

    /**
     * This method handles PUT requests to update an existing book.
     * It expects a JSON object in the request body, with the updated book's data.
     * @param isbn the isbn of the book to be updated
     * @param book the book object with updated data
     */
    @PutMapping("/{isbn}")
    public ResponseEntity<ErrorResponse> updateBook(@PathVariable String isbn, @RequestBody BookDTO book) {
        try {
            bookService.updateBook(isbn, book);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
            } else if (e instanceof BookNotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // ================= Search =================

    /**
     * This method handles GET requests to retrieve a book by its ISBN.
     * It returns the book's data as a JSON object.
     * @param isbn the isbn of the book to be retrieved
     * @return the book object if found, or a 404 Not Found status if not found
     */
    @GetMapping("/{isbn}")
    public ResponseEntity<BookDTO> getBook(@PathVariable String isbn) {
        BookDTO book = bookService.getBookByIsbn(isbn);
        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(book);
    }


    /**
     * This method handles GET requests to search for books by title, author, or genre.
     * It returns a list of books that match the search criteria as a JSON array.
     * If no search criteria are provided, it returns all books.
     * @param title the title of the book to search for (optional)
     * @param author the author of the book to search for (optional)
     * @param genre the genre of the book to search for (optional)
     * @return a list of books that match the search criteria, or an empty list if no books match
     */

    @GetMapping
    public ResponseEntity<Iterable<BookDTO>> searchBooks(@RequestParam(required = false) String title,
                                                         @RequestParam(required = false) String author,
                                                         @RequestParam(required = false) String genre) {
        Iterable<BookDTO> books = bookService.searchBooks(title, author, genre);
        return ResponseEntity.ok(books);
    }

    // ================= Delete =================
    /**
     * This method handles DELETE requests to remove a book by its ISBN.
     * It returns a 204 No Content status if the book was successfully deleted,
     * or a 404 Not Found status if the book was not found.
     * @param isbn the isbn of the book to be deleted
     */
    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
        try {
            bookService.deleteBook(isbn);
            return ResponseEntity.noContent().build();
        } catch (BookNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


}
