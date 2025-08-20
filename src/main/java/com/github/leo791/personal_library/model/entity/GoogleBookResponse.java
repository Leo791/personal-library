package com.github.leo791.personal_library.model.entity;

import java.util.List;

/**
 * Represents the response from the Google Books API.
 * This class contains a list of items, each representing a book with its details.
 * The following link provides more information about the Google Books API response structure:
 * <a href="https://developers.google.com/books/docs/v1/reference/volumes#resource-representations">...</a>
 */


public class GoogleBookResponse {
    private static Integer totalItems;
    private static List<Item> items;


    public static Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        GoogleBookResponse.totalItems = totalItems;
    }

    public static List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        GoogleBookResponse.items = items;
    }

    public static class Item {
        private VolumeInfo volumeInfo;

        public VolumeInfo getVolumeInfo() {
            return volumeInfo;
        }

        public void setVolumeInfo(VolumeInfo volumeInfo) {
            this.volumeInfo = volumeInfo;
        }
    }

    public static class IndustryIdentifier {
        private String type;
        private String identifier;

        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public String getIdentifier() {
            return identifier;
        }
        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }
    }

    public static class VolumeInfo {
        private String title;
        private List<String> authors;
        private String publisher;
        private String publishedDate;
        private List<String> categories;
        private String description;
        private String language;
        private Integer pageCount;
        private List<IndustryIdentifier> industryIdentifiers;


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getAuthors() {
            return authors;
        }

        public void setAuthors(List<String> authors) {
            this.authors = authors;
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

        public List<String> getCategories() {
            return categories;
        }

        public void setCategories(List<String> categories) {
            this.categories = categories;
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

        public Integer getPageCount() {
            return pageCount;
        }

        public void setPageCount(Integer pageCount) {
            this.pageCount = pageCount;
        }

        public List<IndustryIdentifier> getIndustryIdentifiers() {
            return industryIdentifiers;
        }

        public void setIndustryIdentifiers(List<IndustryIdentifier> industryIdentifiers) {
            this.industryIdentifiers = industryIdentifiers;
        }
    }
}