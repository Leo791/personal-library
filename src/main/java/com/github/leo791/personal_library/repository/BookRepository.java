package com.github.leo791.personal_library.repository;

import com.github.leo791.personal_library.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing Book entities.
 * This interface extends JpaRepository to provide CRUD operations for Book entities.
 */
public interface BookRepository extends JpaRepository<Book, Long> {
    /**
     * Finds books by their title.
     *
     * @param title the title of the books to search for
     * @return the book entities with the specified title, or null if not found
     */
    List<Book> findByTitleContainingIgnoreCase(String title);

    /**
     * Finds books by their author.
     *
     * @param author the author of the books to search for
     * @return the book entities with the specified author, or null if not found
     */
    List<Book> findByAuthorContainingIgnoreCase(String author);

    /**
     * Finds books by their genre.
     *
     * @param genre the genre of the books to search for
     * @return the book entities with the specified genre, or null if not found
     */
    List<Book> findByGenreContainingIgnoreCase(String genre);

    /**
     * Finds a book by its ISBN.
     *
     * @param isbn the ISBN of the book to search for
     * @return the book entity with the specified ISBN, or null if not found
     */
    Book findByIsbn(String isbn);

    /**
     * Check if a book exists by its ISBN.
     *
     * @param isbn the ISBN of the book to check
     @return true if the book exists, false otherwise
     */
    boolean existsByIsbn(String isbn);

    /**
     * Deletes a book by its ISBN.
     *
     * @param isbn the ISBN of the book to delete
     */
    void deleteByIsbn(String isbn);
}
