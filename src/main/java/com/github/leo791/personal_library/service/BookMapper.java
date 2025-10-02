package com.github.leo791.personal_library.service;

import com.github.leo791.personal_library.model.dto.BookDTO;
import com.github.leo791.personal_library.model.entity.Book;
import com.github.leo791.personal_library.model.entity.GoogleBookResponse;
import com.github.leo791.personal_library.model.entity.OpenLibraryBookResponse;
import com.github.leo791.personal_library.util.GoogleBooksResponseMapperUtils;
import com.github.leo791.personal_library.util.MapperUtils;
import com.github.leo791.personal_library.util.OpenLibraryResponseMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between Book entities, BookDTOs, and GoogleBookResponse.
 */
@Component
public class BookMapper {

    private static final Logger log = LoggerFactory.getLogger(BookMapper.class);

    /**
     * Converts a Book entity to a BookDTO.
     *
     * @param book the Book entity to convert
     * @return the converted BookDTO, or null if the input is null
     */
    public BookDTO bookToDto(Book book) {
        if (book == null) {
            return null;
        }
        return new BookDTO(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getGenre(),
                           book.getDescription(), book.getLanguage(), book.getPageCount(),
                           book.getPublisher(), book.getPublishedDate());
    }

    /**
     * Converts a BookDTO to a Book entity.
     *
     * @param bookDTO the BookDTO to convert
     * @return the converted Book entity, or null if the input is null
     */
    public Book DTOtoBook(BookDTO bookDTO) {
        if(bookDTO == null) {
            return null;
        }
        return new Book(bookDTO.getIsbn(), bookDTO.getTitle(), bookDTO.getAuthor(), bookDTO.getGenre(),
                        bookDTO.getDescription(), bookDTO.getLanguage(), bookDTO.getPageCount(),
                        bookDTO.getPublisher(), bookDTO.getPublishedDate());
    }

    /**
     * Converts a list of Book entities to a list of BookDTOs.
     *
     * @param books the list of Book entities to convert
     * @return the converted list of BookDTOs, or an empty list if the input is null or empty
     */
    public List<BookDTO> bookListToDtoList(List<Book> books) {
        if (books == null || books.isEmpty()) {
            return Collections.emptyList();
        }
        return books.stream()
                .map(this::bookToDto)
                .collect(Collectors.toList());
    }

    /**
     * Converts a GoogleBookResponse to a Book entity.
     * Makes use of utility methods to extract relevant fields from the response.
     *
     * @param googleBookResponse the GoogleBookResponse to convert
     * @return the converted Book entity, or null if the response has no items
     */
    public Book fromGoogleResponseToBook(GoogleBookResponse googleBookResponse, String isbn) {
        if (GoogleBookResponse.getTotalItems() == 0) {
            return null;
        }
        GoogleBookResponse.Item item = GoogleBookResponse.getItems().getFirst();
        GoogleBookResponse.VolumeInfo volumeInfo = item.getVolumeInfo();

        String title = volumeInfo.getTitle();
        String author = GoogleBooksResponseMapperUtils.extractFirstAuthor(volumeInfo.getAuthors());
        String genre = GoogleBooksResponseMapperUtils.extractGenre(volumeInfo);
        String description = GoogleBooksResponseMapperUtils.cleanDescription(volumeInfo.getDescription());
        String language = GoogleBooksResponseMapperUtils.extractLanguage(volumeInfo.getLanguage());
        Integer pageCount = MapperUtils.extractPageCount(volumeInfo.getPageCount());
        String publisher = GoogleBooksResponseMapperUtils.extractPublisher(volumeInfo.getPublisher());
        String publishedDate = MapperUtils.extractPublishedDate(volumeInfo.getPublishedDate());

        return new Book(isbn, title, author, genre, description, language, pageCount, publisher, publishedDate);
    }

    public Book fromOpenLibraryResponseToBook(OpenLibraryBookResponse openLibraryResponse, String author, String isbn) {

        if (openLibraryResponse == null) {
            return null;
        }
        String genre = "";
        String description= "";
        log.warn("Genre is not provided by Open Library API");
        log.warn("Description is not provided by Open Library API");

        String title = openLibraryResponse.getTitle();
        String language = OpenLibraryResponseMapperUtils.extractLanguage(openLibraryResponse.getLanguages());
        Integer pageCount = MapperUtils.extractPageCount(openLibraryResponse.getNumberOfPages());
        String publisher = OpenLibraryResponseMapperUtils.extractPublisher(openLibraryResponse.getPublishers());
        String publishedDate = MapperUtils.extractPublishedDate(openLibraryResponse.getPublishDate());

        return new Book(isbn, title, author, genre, description, language, pageCount, publisher, publishedDate);
    }
}
