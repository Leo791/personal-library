package com.github.leo791.personal_library.service;

import com.github.leo791.personal_library.model.dto.BookDTO;
import com.github.leo791.personal_library.model.entity.Book;
import com.github.leo791.personal_library.model.entity.GoogleBookResponse;
import com.github.leo791.personal_library.model.entity.OpenLibraryBookResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookMapperTest {

    private GoogleBookResponse mockGoogleBookResponse;
    private OpenLibraryBookResponse mockOpenLibraryBookResponse;

    Book HitchikerBook = new Book("978-3-16-148410-0", "The Hitchhiker's Guide to the Galaxy", "Douglas Adams", "Science Fiction",
            "A comedic science fiction series that follows the adventures of Arthur Dent, an unwitting human who is swept off Earth before its destruction.",
            "English", 224, "Pan Books", "1979");

    BookDTO HitchikerBookDTO = new BookDTO("978-3-16-148410-0", "The Hitchhiker's Guide to the Galaxy", "Douglas Adams", "Science Fiction",
            "A comedic science fiction series that follows the adventures of Arthur Dent, an unwitting human who is swept off Earth before its destruction.",
            "English", 224, "Pan Books", "1979");

    Book Dune = new Book("978-0-441-17271-9", "Dune", "Frank Herbert", "Science Fiction",
            "A science fiction novel set in a distant future amidst a feudal interstellar society, focusing on the young noble Paul Atreides.",
            "English", 412, "Chilton Books", "1965");

    private void setUpGoogleBooksResponse() {
        // Arrange a googleBooksClient response
        GoogleBookResponse.IndustryIdentifier isbn10 = new GoogleBookResponse.IndustryIdentifier("ISBN_10", "1234567890");

        GoogleBookResponse.VolumeInfo volumeInfo = new GoogleBookResponse.VolumeInfo();
        volumeInfo.setTitle("Frankenstein");
        volumeInfo.setAuthors(List.of("Mary Shelley"));
        volumeInfo.setCategories(List.of("Horror"));
        volumeInfo.setIndustryIdentifiers(List.of(isbn10));
        volumeInfo.setPageCount(299);

        GoogleBookResponse.Item item = new GoogleBookResponse.Item();
        item.setVolumeInfo(volumeInfo);


        this.mockGoogleBookResponse = new GoogleBookResponse();
        this.mockGoogleBookResponse.setTotalItems(1);
        this.mockGoogleBookResponse.setItems(List.of(item));
    }

    private void setUpOpenLibraryResponse() {
        // Arrange an openLibraryBookResponse response
        this.mockOpenLibraryBookResponse = new OpenLibraryBookResponse();
        this.mockOpenLibraryBookResponse.setTitle("Frankenstein");
        this.mockOpenLibraryBookResponse.setNumberOfPages(299);
        OpenLibraryBookResponse.AuthorKey authorKey = new OpenLibraryBookResponse.AuthorKey();
        authorKey.setKey("/author/OL12345A");
        this.mockOpenLibraryBookResponse.setAuthors(List.of(authorKey));
        this.mockOpenLibraryBookResponse.setIsbn10(List.of("1234567890"));
        this.mockOpenLibraryBookResponse.setLanguages(List.of(new OpenLibraryBookResponse.LanguageKey("/languages/eng")));
        this.mockOpenLibraryBookResponse.setPublishers(List.of("Penguin Classics"));
        this.mockOpenLibraryBookResponse.setPublishDate("1818");

    }

    @Test
    void bookToDto() {
        BookMapper bookMapper = new BookMapper();
        var bookDTO = bookMapper.bookToDto(HitchikerBook);

        assertNotNull(bookDTO);
        assertEquals(HitchikerBook.getIsbn(), bookDTO.getIsbn());
        assertEquals(HitchikerBook.getTitle(), bookDTO.getTitle());
        assertEquals(HitchikerBook.getAuthor(), bookDTO.getAuthor());
        assertEquals(HitchikerBook.getGenre(), bookDTO.getGenre());
        assertEquals(HitchikerBook.getDescription(), bookDTO.getDescription());
        assertEquals(HitchikerBook.getLanguage(), bookDTO.getLanguage());
        assertEquals(HitchikerBook.getPageCount(), bookDTO.getPageCount());
        assertEquals(HitchikerBook.getPublisher(), bookDTO.getPublisher());
        assertEquals(HitchikerBook.getPublishedDate(), bookDTO.getPublishedDate());
    }

    @Test
    void bookToDto_Null() {
        BookMapper bookMapper = new BookMapper();
        var bookDTO = bookMapper.bookToDto(null);

        assertNull(bookDTO);
    }

    @Test
    void DTOtoBook() {
        BookMapper bookMapper = new BookMapper();
        var book = bookMapper.DTOtoBook(HitchikerBookDTO);

        assertNotNull(book);
        assertEquals(HitchikerBookDTO.getIsbn(), book.getIsbn());
        assertEquals(HitchikerBookDTO.getTitle(), book.getTitle());
        assertEquals(HitchikerBookDTO.getAuthor(), book.getAuthor());
        assertEquals(HitchikerBookDTO.getGenre(), book.getGenre());
        assertEquals(HitchikerBookDTO.getDescription(), book.getDescription());
        assertEquals(HitchikerBookDTO.getLanguage(), book.getLanguage());
        assertEquals(HitchikerBookDTO.getPageCount(), book.getPageCount());
        assertEquals(HitchikerBookDTO.getPublisher(), book.getPublisher());
        assertEquals(HitchikerBookDTO.getPublishedDate(), book.getPublishedDate());
    }

    @Test
    void DTOtoBook_Null() {
        BookMapper bookMapper = new BookMapper();
        var book = bookMapper.DTOtoBook(null);

        assertNull(book);
    }

    @Test
    void bookListToDtoList() {
        BookMapper bookMapper = new BookMapper();
        var bookList = List.of(HitchikerBook, Dune);
        var bookDTOList = bookMapper.bookListToDtoList(bookList);

        assertNotNull(bookDTOList);
        assertEquals(2, bookDTOList.size());

        var firstBookDTO = bookDTOList.getFirst();
        assertEquals(HitchikerBook.getIsbn(), firstBookDTO.getIsbn());
        assertEquals(HitchikerBook.getTitle(), firstBookDTO.getTitle());
        assertEquals(HitchikerBook.getAuthor(), firstBookDTO.getAuthor());
        assertEquals(HitchikerBook.getGenre(), firstBookDTO.getGenre());
        assertEquals(HitchikerBook.getDescription(), firstBookDTO.getDescription());
        assertEquals(HitchikerBook.getLanguage(), firstBookDTO.getLanguage());
        assertEquals(HitchikerBook.getPageCount(), firstBookDTO.getPageCount());
        assertEquals(HitchikerBook.getPublisher(), firstBookDTO.getPublisher());
        assertEquals(HitchikerBook.getPublishedDate(), firstBookDTO.getPublishedDate());

        var secondBookDTO = bookDTOList.get(1);
        assertEquals(Dune.getIsbn(), secondBookDTO.getIsbn());
        assertEquals(Dune.getTitle(), secondBookDTO.getTitle());
        assertEquals(Dune.getAuthor(), secondBookDTO.getAuthor());
        assertEquals(Dune.getGenre(), secondBookDTO.getGenre());
        assertEquals(Dune.getDescription(), secondBookDTO.getDescription());
        assertEquals(Dune.getLanguage(), secondBookDTO.getLanguage());
        assertEquals(Dune.getPageCount(), secondBookDTO.getPageCount());
        assertEquals(Dune.getPublisher(), secondBookDTO.getPublisher());
        assertEquals(Dune.getPublishedDate(), secondBookDTO.getPublishedDate());
    }

    @Test
    void bookListToDtoList_Empty() {
        BookMapper bookMapper = new BookMapper();
        var bookDTOList = bookMapper.bookListToDtoList(List.of());

        assertNotNull(bookDTOList);
        assertTrue(bookDTOList.isEmpty());
    }

    @Test
    void bookListToDtoList_Null() {
        BookMapper bookMapper = new BookMapper();
        var bookDTOList = bookMapper.bookListToDtoList(null);

        assertNotNull(bookDTOList);
        assertTrue(bookDTOList.isEmpty());
    }

    @Test
    void fromGoogleResponseToBook() {
        setUpGoogleBooksResponse();
        BookMapper bookMapper = new BookMapper();
        var book = bookMapper.fromGoogleResponseToBook(mockGoogleBookResponse);

        assertNotNull(book);
        assertEquals("1234567890", book.getIsbn());
        assertEquals("Frankenstein", book.getTitle());
        assertEquals("Mary Shelley", book.getAuthor());
        assertEquals("Horror", book.getGenre());
        assertEquals("", book.getDescription()); // Description is not set in mock
        assertEquals("", book.getLanguage()); // Language is not set in mock
        assertEquals(299, book.getPageCount());
        assertEquals("", book.getPublisher()); // Publisher is not set in mock
        assertEquals("", book.getPublishedDate()); // Published date is not set in mock
    }

    @Test
    void fromGoogleResponseToBook_NoItems() {
        GoogleBookResponse emptyResponse = new GoogleBookResponse();
        emptyResponse.setTotalItems(0);
        emptyResponse.setItems(List.of());

        BookMapper bookMapper = new BookMapper();
        var book = bookMapper.fromGoogleResponseToBook(emptyResponse);

        assertNull(book, "Should return null if there are no items in the response");
    }

    @Test
    void fromOpenLibraryResponseToBook(){
        setUpOpenLibraryResponse();
        BookMapper bookMapper = new BookMapper();
        var book = bookMapper.fromOpenLibraryResponseToBook(mockOpenLibraryBookResponse, "Mary Shelley");

        assertNotNull(book);
        assertEquals("1234567890", book.getIsbn());
        assertEquals("Frankenstein", book.getTitle());
        assertEquals("Mary Shelley", book.getAuthor());
        assertEquals("", book.getGenre()); // Genre is not set in mock
        assertEquals("", book.getDescription()); // Description is not set in mock
        assertEquals("ENG", book.getLanguage());
        assertEquals(299, book.getPageCount());
        assertEquals("Penguin Classics", book.getPublisher());
        assertEquals("1818", book.getPublishedDate());
    }

    @Test
    void fromOpenLibraryResponseToBook_Null(){
        BookMapper bookMapper = new BookMapper();
        var book = bookMapper.fromOpenLibraryResponseToBook(null, "Some Author");

        assertNull(book, "Should return null if the OpenLibraryBookResponse is null");
    }
}