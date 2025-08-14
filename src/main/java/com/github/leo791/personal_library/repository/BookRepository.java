package com.github.leo791.personal_library.repository;

import com.github.leo791.personal_library.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Book entities.
 * This interface extends JpaRepository to provide CRUD operations for Book entities.
 */
public interface BookRepository extends JpaRepository<Book, Integer> {
}
