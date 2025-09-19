package com.github.leo791.personal_library.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * Represents a book entity in the personal library.
 * This class maps to a database table and contains fields for the book's ID, ISBN, title, author, genre, description,
 * language, publisher, page count, and published date.
 */
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Size(max=13, min=10, message="ISBN must be either 10 or 13 characters")
    private String isbn;

    private String title;
    private String author;
    private String genre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max=3, message="Language must be a 2 or 3 letter code")
    private String language;
    private String publisher;

    @Column(name = "page_count")
    private Integer pageCount;

    @Size(max=4, message="Published date must be in the format YYYY")
    @Column(name = "published_date", length = 4)
    private String publishedDate;


    /**
     * Default constructor for JPA.
     * This constructor is required by JPA to create instances of the entity.
     */
    public Book() {
    }
    /**
     * Constructs a new Book entity with the specified parameters.
     *
     * @param isbn          the ISBN of the book
     * @param title         the title of the book
     * @param author        the author of the book
     * @param genre         the genre of the book
     * @param description   a brief description of the book
     * @param language      the language of the book (2-letter code)
     * @param pageCount     the number of pages in the book
     * @param publisher     the publisher of the book
     * @param publishedDate the year the book was published (YYYY format)
     */
    public Book(String isbn, String title, String author, String genre, String description, String language, Integer pageCount, String publisher, String publishedDate) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.language = language;
        this.pageCount = pageCount;
        this.publisher = publisher;
        this.publishedDate = publishedDate;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    @Override
    public String toString() {
        return "Book{isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                ", publisher='" + publisher + '\'' +
                ", pageCount=" + pageCount +
                ", publishedDate='" + publishedDate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return 31 + (isbn != null ? isbn.hashCode(): 0);
    }
}
