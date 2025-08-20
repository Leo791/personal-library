package com.github.leo791.personal_library.service;

import com.github.leo791.personal_library.model.dto.BookDTO;
import com.github.leo791.personal_library.model.entity.Book;
import com.github.leo791.personal_library.model.entity.GoogleBookResponse;
import com.github.leo791.personal_library.util.BookUtils;
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
        return new BookDTO(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getGenre(),
                           book.getDescription(), book.getLanguage(), book.getPageCount(),
                           book.getPublisher(), book.getPublishedDate());
    }

    // DTO to Entity conversion
    public Book DTOtoBook(BookDTO bookDTO) {
        if(bookDTO == null) {
            return null;
        }
        return new Book(bookDTO.getIsbn(), bookDTO.getTitle(), bookDTO.getAuthor(), bookDTO.getGenre(),
                        bookDTO.getDescription(), bookDTO.getLanguage(), bookDTO.getPageCount(),
                        bookDTO.getPublisher(), bookDTO.getPublishedDate());
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

    // First try to find ISBN_13, then fallback to ISBN_10 if not found
    String isbn = volumeInfo.getIndustryIdentifiers().stream()
            .filter(identifier -> "ISBN_13".equals(identifier.getType()))
            .map(GoogleBookResponse.IndustryIdentifier::getIdentifier)
            .findFirst()
            .orElseGet(() -> volumeInfo.getIndustryIdentifiers().stream()
                    .filter(identifier -> "ISBN_10".equals(identifier.getType()))
                    .map(GoogleBookResponse.IndustryIdentifier::getIdentifier)
                    .findFirst()
                    .orElse(null));

    if (isbn == null) {
        throw new IllegalArgumentException("No ISBN found in GoogleBookResponse for a search by ISBN.");
    }

    // Get the title
    String title = volumeInfo.getTitle();

    // Get the first author, if available
    String author = (volumeInfo.getAuthors() != null && !volumeInfo.getAuthors().isEmpty())
            ? volumeInfo.getAuthors().getFirst()
            : null;

    // Get the first genre out of categories, if available
    String genre = (volumeInfo.getCategories() != null && !volumeInfo.getCategories().isEmpty())
            ? volumeInfo.getCategories().getFirst()
            : null;

    // Get the description, if available
    String rawDescription = volumeInfo.getDescription() != null ? volumeInfo.getDescription() : "";
    String description = BookUtils.cleanDescription(rawDescription);

    // Get the language, if available
    String rawLanguage = volumeInfo.getLanguage() != null ? volumeInfo.getLanguage() : "";
    // Language is expected to be in ISO 639-1 format. But they might be locale codes (e.g. pt-BT or en-GB). So we take the first two characters.
    String language = rawLanguage.toUpperCase().substring(0, 2);

    // Get the page count, if available
    int pageCount = Math.max(volumeInfo.getPageCount(), 0);

    // Get the publisher, if available
    String publisher = volumeInfo.getPublisher() != null ? volumeInfo.getPublisher() : "";

    // Get the published date, if available
    String rawPublishedDate = volumeInfo.getPublishedDate() != null ? volumeInfo.getPublishedDate() : "";
    String publishedDate = rawPublishedDate.substring(0, 4);

    return new Book(isbn, title, author, genre, description, language, pageCount, publisher, publishedDate);
}


}
