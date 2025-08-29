package com.github.leo791.personal_library.model.entity;

import java.util.List;
import java.util.Map;

public class OpenLibraryBookResponse {
    public List<String> publishers;
    public List<AuthorKey> authors;
    public List<String> isbn_13;
    public String title;
    public List<LanguageKey> languages;
    public List<String> isbn_10;
    public String publish_date;

    public static class LanguageKey {
        public String key;
    }

    public static class AuthorKey {
        public String key;
    }
}