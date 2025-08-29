package com.github.leo791.personal_library.client;

import com.github.leo791.personal_library.model.entity.OpenLibraryBookResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenLibraryClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private  OpenLibraryClient openLibraryClient;

    @Test
    void fetchBookByIsbn_ShouldReturnResponse() {
        String isbn = "1234567890";
        openLibraryClient = new OpenLibraryClient(restTemplate);
        OpenLibraryBookResponse mockResponse = new OpenLibraryBookResponse();

        when(restTemplate.getForObject(anyString(),eq(OpenLibraryBookResponse.class)))
                .thenReturn(mockResponse);

        OpenLibraryBookResponse result = openLibraryClient.fetchBookByIsbn(isbn);

        assertEquals(mockResponse, result);
        verify(restTemplate).getForObject(contains(isbn), eq(OpenLibraryBookResponse.class));
    }

    @Test
    void fetchAuthorByKey_ShouldReturnAuthorName() throws Exception {
        String authorKey = "/authors/OL12345A";
        String expectedAuthorName = "John Doe";
        openLibraryClient = new OpenLibraryClient(restTemplate);
        String mockJsonResponse = "{\"name\": \"" + expectedAuthorName + "\"}";

        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new org.springframework.http.ResponseEntity<>(mockJsonResponse, org.springframework.http.HttpStatus.OK));

        String result = openLibraryClient.fetchAuthorByKey(authorKey);

        assertEquals(expectedAuthorName, result);
        verify(restTemplate).getForEntity(contains(authorKey), eq(String.class));
    }

}