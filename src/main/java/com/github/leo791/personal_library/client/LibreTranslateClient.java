package com.github.leo791.personal_library.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class LibreTranslateClient {

    private static final String BASE_URL = "http://libretranslate:5000";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public LibreTranslateClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public String translate(String text, String sourceLang, String targetLang) throws Exception {
        String url = BASE_URL + "/translate";
        String body = "q=" + URLEncoder.encode(text, StandardCharsets.UTF_8) +
                      "&source=" + URLEncoder.encode(sourceLang, StandardCharsets.UTF_8) +
                      "&target=" + URLEncoder.encode(targetLang, StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        if (response.getStatusCode().is2xxSuccessful() && jsonNode.has("translatedText")) {
                return jsonNode.get("translatedText").asText();
        } else if (response.getStatusCode().is4xxClientError() && jsonNode.has("error")) {
                throw new Exception(jsonNode.get("error").asText());
        }
        throw new Exception("Unexpected response from LibreTranslate: " + response.getBody());
    }

    public String detect(String text) throws Exception {
        String url = BASE_URL + "/detect";
        String body = "q=" + URLEncoder.encode(text, StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        if (response.getStatusCode().is2xxSuccessful() && jsonNode.get(0).has("language")) {
                return jsonNode.get(0).get("language").asText();
        } else if (response.getStatusCode().is4xxClientError() && jsonNode.has("error")) {
                 throw new Exception(jsonNode.get("error").asText());
        }
        throw new Exception("Unexpected response from LibreTranslate: " + response.getBody());
    }


}
