package com.github.leo791.personal_library.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leo791.personal_library.model.entity.OpenLibraryBookResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OpenLibraryClient {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OpenLibraryClient.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public OpenLibraryClient(RestTemplate restTemplate, @Value("${openlibrary.api.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }


    public OpenLibraryBookResponse fetchBookByIsbn(String isbn) {
        String url = baseUrl + "/isbn/" + isbn + ".json";
        try {
            return restTemplate.getForObject(url, OpenLibraryBookResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching book with ISBN " + isbn + ": " + e.getMessage(), e);
        }
    }

    public String fetchAuthorByKey(String authorKey) throws Exception {
        String url = baseUrl + authorKey + ".json";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        log.info("Author response: {}", response.getBody());
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        if (response.getStatusCode().is2xxSuccessful() && jsonNode.has("name")) {
            return jsonNode.get("name").asText();
        } else  {
            throw new RuntimeException("Error fetching author with code: " + response.getBody());
        }
    }
}


