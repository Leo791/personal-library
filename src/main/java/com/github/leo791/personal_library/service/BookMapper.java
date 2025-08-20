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

import static com.github.leo791.personal_library.util.MapperUtils.*;

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
    if (GoogleBookResponse.getTotalItems() == 0) {
        return null;
    }
    GoogleBookResponse.Item item = GoogleBookResponse.getItems().getFirst();
    GoogleBookResponse.VolumeInfo volumeInfo = item.getVolumeInfo();

    // Get the ISBN_13 from industry identifiers, if not available, use ISBN_10
    String isbn = extractIsbn(volumeInfo.getIndustryIdentifiers());

    // Get the title
    String title = volumeInfo.getTitle();

    // Get the first author, if available
    String author = extractFirstAuthor(volumeInfo.getAuthors());

    // Get the first genre out of categories, if available
    String genre = extractGenre(volumeInfo);

    // Get the description, if available
    String description = cleanDescription(volumeInfo.getDescription());

    // Get the language, if available
    String language = extractLanguage(volumeInfo.getLanguage());

    // Get the page count, if available
    Integer pageCount = Math.max(volumeInfo.getPageCount(), 0);

    // Get the publisher, if available
    String publisher = volumeInfo.getPublisher() != null ? volumeInfo.getPublisher() : "";

    // Get the published date, if available
    String publishedDate = extractPublishedDate(volumeInfo.getPublishedDate());

    return new Book(isbn, title, author, genre, description, language, pageCount, publisher, publishedDate);
}


}
