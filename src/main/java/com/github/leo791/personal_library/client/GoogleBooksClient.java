package com.github.leo791.personal_library.client;

import com.github.leo791.personal_library.model.entity.GoogleBookResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GoogleBooksClient {

    private final RestTemplate restTemplate;
    private final String apiKey;

    public GoogleBooksClient(RestTemplate restTemplate, @Value("${google.books.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    public GoogleBookResponse fetchBookByIsbn(String isbn) {
        String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn + "&key=" + apiKey;
        return restTemplate.getForObject(url, GoogleBookResponse.class);
    }
}
