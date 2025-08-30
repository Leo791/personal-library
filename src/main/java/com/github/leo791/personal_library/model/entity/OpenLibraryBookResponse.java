package com.github.leo791.personal_library.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenLibraryBookResponse {
    public List<String> publishers;
    public List<AuthorKey> authors;
    public String title;
    public List<LanguageKey> languages;

    @JsonProperty("isbn_13")
    public List<String> isbn13;


    @JsonProperty("isbn_10")
    public List<String> isbn10;

    @JsonProperty("publish_date")
    public String publishDate;

    @JsonProperty("number_of_pages")
    public Integer numberOfPages;

    public List<String> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<String> publishers) {
        this.publishers = publishers;
    }

    public List<AuthorKey> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorKey> authors) {
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(List<String> isbn13) {
        this.isbn13 = isbn13;
    }

    public List<LanguageKey> getLanguages() {
        return languages;
    }

    public void setLanguages(List<LanguageKey> languages) {
        this.languages = languages;
    }

    public List<String> getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(List<String> isbn10) {
        this.isbn10 = isbn10;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public Integer getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public static class LanguageKey {
        public String key;

        public LanguageKey(String path) {
            this.key = path;
        }

        public LanguageKey() {
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class AuthorKey {
        public String key;

        public void setKey(String path) {
            this.key = path;
        }
    }

    @Override
    public String toString() {
        return "OpenLibraryBookResponse{" +
                "publishers=" + publishers +
                ", authors=" + authors +
                ", title='" + title + '\'' +
                ", languages=" + languages +
                ", isbn13=" + isbn13 +
                ", isbn10=" + isbn10 +
                ", publishDate='" + publishDate + '\'' +
                ", numberOfPages=" + numberOfPages +
                '}';
    }
}