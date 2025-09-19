import com.fasterxml.jackson.databind.JsonNode;
import com.github.leo791.personal_library.model.dto.BookDTO;
import com.github.leo791.personal_library.model.entity.Book;
import com.github.leo791.personal_library.repository.BookRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration Test for deleting a book.
 * Uses Testcontainers to spin up a temporary PostgreSQL database.
 * Tests the /api/v1/books endpoint for various scenarios:
 * - Successfully deleting an existing book.
 * - Handling invalid ISBN input.
 * - Handling the case where the book does not exist in the database.
 */
@SpringBootTest(classes = com.github.leo791.personal_library.PersonalLibraryApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class DeleteBookIT {

    private static final String errorsBasePath = "src/component-test/resources/ErrorResponses/";
    private static final String isbn = "9789722060172";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookRepository bookRepository;

    // Mock database
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("google.books.api.base-url", () -> "dummy-url");
        registry.add("google.books.api.key", () -> "dummy-key");

        registry.add("openlibrary.api.base-url", () -> "dummy-url");
        registry.add("libretranslate.api.base-url", () -> "dummy-url");
    }

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
    }

    @Test
    void shouldDeleteBook(){
        // Arrange
        Book book = MockUtils.createSampleBook();
        bookRepository.save(book);

        // Act
        restTemplate.delete("/api/v1/books/" + isbn);

        // Assert
        boolean bookExists = bookRepository.existsByIsbn(isbn);
        assertThat(bookExists).isFalse();
    }

    @Test
    void shouldReturnNotFound_WhenDeletingBook_ThatDoesNotExist() {
        JsonNode bookNotFoundResponse = MockUtils.readJsonNodeFromFile(errorsBasePath + "BookNotFoundInDatabase.json");

        // Act
        ResponseEntity<String> response = restTemplate.exchange("/api/v1/books/" + isbn, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo(bookNotFoundResponse.toString());
    }

    @Test
    void shouldReturnBadRequest_WhenDeletingBook_WithInvalidIsbn() {
        String invalidIsbn = "123";

        JsonNode invalidIsbnResponse = MockUtils.readJsonNodeFromFile(errorsBasePath + "InvalidIsbn.json");

        // Act
        ResponseEntity<String> response = restTemplate.exchange("/api/v1/books/" + invalidIsbn, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(invalidIsbnResponse.toString());
    }
}
