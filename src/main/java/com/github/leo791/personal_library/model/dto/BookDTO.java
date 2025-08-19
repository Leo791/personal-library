package com.github.leo791.personal_library.model.dto;

/**
 * Data Transfer Object (DTO) for Book.
 * This class is used to transfer book data between different layers of the application.
 * It contains fields for ISBN, title, author, and genre.
 */
public class BookDTO {
    private String isbn;
    private String title;
    private String author;
    private String genre;
    private String description;
    private String language;
    private int pageCount;
    private String publisher;
    private String publishedDate;

    public BookDTO() {
    }

    public BookDTO(String isbn, String title, String author, String genre, String description, String language, int pageCount, String publisher, String publishedDate) {

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

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }
}
