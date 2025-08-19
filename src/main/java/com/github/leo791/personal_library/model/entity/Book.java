package com.github.leo791.personal_library.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * Represents a book entity in the personal library.
 * This class maps to a database table and contains fields for the book's ID, ISBN, title, author, and genre.
 * The ISBN is unique and cannot be null.
 */
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String isbn;

    private String title;
    private String author;
    private String genre;
    private String description;
    private String language;
    private String publisher;

    @Column(name = "page_count")
    private int pageCount;

    @Size(max=4, message="Published date must be in the format YYYY")
    @Column(name = "published_date", length = 4)
    private String publishedDate;


    /**
     * Default constructor for JPA.
     * This constructor is required by JPA to create instances of the entity.
     */
    protected Book() {
    }

    /**
     * Constructs a new Book instance with the specified parameters.
     *
     * @param isbn   the ISBN of the book, must be unique and not null
     * @param title  the title of the book
     * @param author the author of the book
     * @param genre  the genre of the book
     */
    public Book(String isbn, String title, String author, String genre, String description, String language, int pageCount, String publisher, String publishedDate) {
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

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
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
