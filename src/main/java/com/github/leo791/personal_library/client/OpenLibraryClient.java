package com.github.leo791.personal_library.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leo791.personal_library.model.entity.OpenLibraryBookResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class OpenLibraryClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OpenLibraryClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }


    public OpenLibraryBookResponse fetchBookByIsbn(String isbn) {
        String url = "https://openlibrary.org/isbn/" + isbn;
        return restTemplate.getForObject(url, OpenLibraryBookResponse.class);
    }

    public String fetchAuthorByKey(String authorKey) throws Exception {
        String url = "https://openlibrary.org" + authorKey + ".json";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        if (response.getStatusCode().is2xxSuccessful() && jsonNode.has("name")) {
            return jsonNode.get("name").asText();
        } else  {
            throw new RuntimeException("Error fetching author with code: " + response.getBody());
        }
    }
}


