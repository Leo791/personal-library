import com.fasterxml.jackson.databind.JsonNode;
import com.github.leo791.personal_library.model.dto.BookDTO;
import com.github.leo791.personal_library.model.entity.Book;
import com.github.leo791.personal_library.repository.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * Integration Test for retrieving a book by its ISBN.
 * Uses Testcontainers to spin up a temporary PostgreSQL database.
 * Tests the /api/v1/books/{isbn} endpoint for various scenarios:
 * - Successfully retrieving an existing book.
 * - Handling the case where the book does not exist.
 * - Handling invalid ISBN input.
 * The test library is created using MockUtils.createSampleBook() and contains:
 * - "The Little Prince" by Antoine de Saint-Exupéry (ISBN: 9789722060172)
 */
@SpringBootTest(classes = com.github.leo791.personal_library.PersonalLibraryApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class GetBookByIsbnIT {

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
    void shouldGetBookByIsbn() {
        // Arrange
        Book existingBook = MockUtils.createSampleBook();
        bookRepository.save(existingBook);
        // Act
        ResponseEntity<BookDTO> response = restTemplate.getForEntity("/api/v1/books/" + isbn, BookDTO.class);

        BookDTO bookResponse = response.getBody();

        // Assert Response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(bookResponse).isNotNull();
        assertAll(
                () -> assertThat(bookResponse.getIsbn()).isEqualTo(isbn),
                () -> assertThat(bookResponse.getTitle()).isEqualTo("The Little Prince"),
                () -> assertThat(bookResponse.getAuthor()).isEqualTo("Antoine de Saint-Exupéry"),
                () -> assertThat(bookResponse.getGenre()).isEqualTo("Fiction"),
                () -> assertThat(bookResponse.getPublishedDate()).isEqualTo("1943"),
                () -> assertThat(bookResponse.getDescription()).isEqualTo("A poetic tale of a young prince who travels from planet to planet, learning about life, love, and human nature."),
                () -> assertThat(bookResponse.getLanguage()).isEqualTo("ENG"),
                () -> assertThat(bookResponse.getPageCount()).isEqualTo(96),
                () -> assertThat(bookResponse.getPublisher()).isEqualTo("Reynal & Hitchcock")
        );
    }

    @Test
    void shouldReturnNotFound_WhenBookDoesNotExist(){
        // Arrange
        JsonNode expectedResponse = MockUtils.readJsonNodeFromFile(errorsBasePath + "BookNotFoundInDatabase.json");

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/books/" + isbn, String.class);
        String errorResponse = response.getBody();

        // Assert
        // Assert Response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorResponse).isEqualTo(expectedResponse.toString());
    }

    @Test
    void shouldReturnBadRequest_WhenIsbnIsInvalid(){
        // Arrange
        String invalidIsbn = "123";
        JsonNode expectedResponse = MockUtils.readJsonNodeFromFile(errorsBasePath + "InvalidIsbn.json");

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/books/" + invalidIsbn, String.class);
        String errorResponse = response.getBody();

        // Assert
        // Assert Response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorResponse).isEqualTo(expectedResponse.toString());
    }
}