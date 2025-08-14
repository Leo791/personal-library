package com.github.leo791.personal_library.service;

import com.github.leo791.personal_library.model.dto.BookDTO;
import com.github.leo791.personal_library.model.entity.Book;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between Book entities and DTOs.
 */
@Component
public class BookMapper {

    // Entity to DTO conversion
    public BookDTO toDto(Book book) {
        if (book == null) {
            return null;
        }
        return new BookDTO(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getGenre());
    }

    // DTO to Entity conversion
    public Book toEntity(BookDTO bookDTO) {
        if( bookDTO == null) {
            return null;
        }
        return new Book(bookDTO.getIsbn(), bookDTO.getTitle(), bookDTO.getAuthor(), bookDTO.getGenre());
    }

    // Convert a list of entities to a list of DTOs
    public List<BookDTO> toDtoList(List<Book> books) {
        if (books == null || books.isEmpty()) {
            return Collections.emptyList();
        }
        return books.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


}
