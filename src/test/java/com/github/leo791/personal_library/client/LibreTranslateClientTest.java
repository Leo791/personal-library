package com.github.leo791.personal_library.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LibreTranslateClientTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private LibreTranslateClient libreTranslateClient;


    @Test
    void translate_ShouldReturnTranslatedText() throws Exception {
        String text = "Hello";
        String sourceLang = "en";
        String targetLang = "es";
        String mockJson = "{\"translatedText\":\"Hola\"}";

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(mockJson));

        String result = libreTranslateClient.translate(text, sourceLang, targetLang);

        assertEquals("Hola", result);
    }

    @Test
    void translate_ShouldThrowExceptionOnErrorResponse() {
        String text = "Hello";
        String sourceLang = "en";
        String targetLang = "xx"; // Invalid target language
        String mockJson = "{\"error\":\"xx not supported\"}";

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.badRequest().body(mockJson));

        Exception exception = assertThrows(Exception.class, () -> {
            libreTranslateClient.translate(text, sourceLang, targetLang);
        });

        assertEquals("xx not supported", exception.getMessage());
    }

    @Test
    void translate_ShouldThrowExceptionOnUnexpectedResponse() {
        String text = "Hello";
        String sourceLang = "en";
        String targetLang = "es";
        String mockJson = "{\"unexpectedField\":\"value\"}";

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(mockJson));

        Exception exception = assertThrows(Exception.class, () -> {
            libreTranslateClient.translate(text, sourceLang, targetLang);
        });

        assertTrue(exception.getMessage().contains("Unexpected response from LibreTranslate"));
    }

    @Test
    void detect_ShouldReturnDetectedLanguage() throws Exception {
        String text = "Hello";
        String mockJson = "[{\"language\":\"en\",\"confidence\":1}]";

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(mockJson));

        String result = libreTranslateClient.detect(text);

        assertEquals("en", result);
    }

    @Test
    void detect_ShouldThrowExceptionOnErrorResponse() {
        String text = "Hello";
        String mockJson = "{\"error\":\"Invalid request\"}";

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.badRequest().body(mockJson));

        Exception exception = assertThrows(Exception.class, () -> {
            libreTranslateClient.detect(text);
        });

        assertEquals("Invalid request", exception.getMessage());

    }

    @Test
    void detect_ShouldThrowExceptionOnUnexpectedResponse() {
        String text = "Hello";
        String mockJson = "{\"unexpectedField\":\"value\"}";

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(mockJson));

        Exception exception = assertThrows(Exception.class, () -> {
            libreTranslateClient.detect(text);
        });

        assertTrue(exception.getMessage().contains("Unexpected response from LibreTranslate"));
    }



}