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

    public BookDTO() {
    }

    public BookDTO(Long id, String isbn, String title, String author, String genre) {

        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
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
}
