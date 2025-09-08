package com.github.leo791.personal_library.client;

import com.github.leo791.personal_library.model.entity.GoogleBookResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleBooksClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GoogleBooksClient googleBooksClient;

    @Test
    void fetchBookByIsbn_ShouldReturnResponse() {
        String isbn = "1234567890";
        googleBooksClient = new GoogleBooksClient(restTemplate,"mock-base-url", "mock-api-key");
        GoogleBookResponse mockResponse = new GoogleBookResponse();

        when(restTemplate.getForObject(anyString(),eq(GoogleBookResponse.class)))
                .thenReturn(mockResponse);

        GoogleBookResponse result = googleBooksClient.fetchBookByIsbn(isbn);

        assertEquals(mockResponse, result);
        verify(restTemplate).getForObject(contains(isbn), eq(GoogleBookResponse.class));
    }
}