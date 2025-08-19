package com.github.leo791.personal_library.service;

import com.github.leo791.personal_library.model.dto.BookDTO;
import com.github.leo791.personal_library.model.entity.Book;
import com.github.leo791.personal_library.model.entity.GoogleBookResponse;
import org.springframework.beans.PropertyValues;
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
    public BookDTO bookToDto(Book book) {
        if (book == null) {
            return null;
        }
        return new BookDTO(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getGenre());
    }

    // DTO to Entity conversion
    public Book DTOtoBook(BookDTO bookDTO) {
        if( bookDTO == null) {
            return null;
        }
        return new Book(bookDTO.getIsbn(), bookDTO.getTitle(), bookDTO.getAuthor(), bookDTO.getGenre());
    }

    // Convert a list of entities to a list of DTOs
    public List<BookDTO> bookListToDtoList(List<Book> books) {
        if (books == null || books.isEmpty()) {
            return Collections.emptyList();
        }
        return books.stream()
                .map(this::bookToDto)
                .collect(Collectors.toList());
    }

public Book fromGoogleResponseToBook(GoogleBookResponse googleBookResponse) {
    if (googleBookResponse == null || GoogleBookResponse.getItems() == null || GoogleBookResponse.getItems().isEmpty()) {
        return null;
    }
    GoogleBookResponse.Item item = GoogleBookResponse.getItems().getFirst();
    GoogleBookResponse.VolumeInfo volumeInfo = item.getVolumeInfo();

    // Get the ISBN_10 from industry identifiers, if available
    String isbn = null;
    if (volumeInfo.getIndustryIdentifiers() != null) {
        isbn = volumeInfo.getIndustryIdentifiers().stream()
                .filter(identifier -> "ISBN_10".equals(identifier.getType()))
                .map(GoogleBookResponse.IndustryIdentifier::getIdentifier)
                .findFirst()
                .orElse(null);
    }

    // Get the title
    String title = volumeInfo.getTitle();

    // Get the first author, if available
    String author = (volumeInfo.getAuthors() != null && !volumeInfo.getAuthors().isEmpty())
            ? volumeInfo.getAuthors().get(0)
            : null;

    // Get the first genre out of categories, if available
    String genre = (volumeInfo.getCategories() != null && !volumeInfo.getCategories().isEmpty())
            ? volumeInfo.getCategories().get(0)
            : null;

    return new Book(isbn, title, author, genre);
}


}
