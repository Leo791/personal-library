import com.fasterxml.jackson.databind.JsonNode;
import com.github.leo791.personal_library.model.dto.BookDTO;
import com.github.leo791.personal_library.model.entity.Book;
import com.github.leo791.personal_library.repository.BookRepository;
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

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(classes = com.github.leo791.personal_library.PersonalLibraryApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class UpdateBookIT {

    private static final String fileBasePath = "src/component-test/resources/updateBookStubs/";
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

        registry.add("openlibrary.api.base-url",  () -> "dummy-url");
        registry.add("libretranslate.api.base-url", () -> "dummy-url");
    }

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
    }

    @Test
    void shouldUpdateBook() {
        // Act
        Book existingBook = MockUtils.createSampleBook();
        bookRepository.save(existingBook);
        BookDTO request = MockUtils.readBookDTOFromJson(fileBasePath + "Success.json");

        // Act
        ResponseEntity<BookDTO> response = restTemplate.exchange("/api/v1/books", HttpMethod.PUT, new HttpEntity<>(request), BookDTO.class);
        BookDTO bookResponse = response.getBody();
        // Assert
        // Assert Response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(bookResponse).isNotNull();
        assertAll(
                () -> assertThat(bookResponse.getIsbn()).isEqualTo(isbn),
                () -> assertThat(bookResponse.getTitle()).isEqualTo("The Little Prince"),
                () -> assertThat(bookResponse.getAuthor()).isEqualTo("Antoine de Saint-Exupéry"),
                () -> assertThat(bookResponse.getGenre()).isEqualTo("Fiction"),
                () -> assertThat(bookResponse.getPublishedDate()).isEqualTo("1943"),
                () -> assertThat(bookResponse.getDescription()).isEqualTo("Most famous French book of the twentieth century, The Little Prince, is a poetic tale, with watercolour illustrations by the author, in which a pilot stranded in the desert meets a young prince fallen to Earth from a tiny asteroid. Through the prince's adventures and his encounters with a cast of extraordinary characters, the story explores themes of loneliness, friendship, love, and loss. The book's philosophical insights and reflections on human nature have made it a beloved classic for both children and adults."),
                () -> assertThat(bookResponse.getLanguage()).isEqualTo("EN"),
                () -> assertThat(bookResponse.getPageCount()).isEqualTo(96),
                () -> assertThat(bookResponse.getPublisher()).isEqualTo("Reynal & Hitchcock")
        );

        // Assert Database state
        Optional<Book> savedBook = Optional.ofNullable(bookRepository.findByIsbn(isbn));
        assertThat(savedBook).isPresent();
        assertAll(
                () -> assertThat(savedBook.get().getIsbn()).isEqualTo(isbn),
                () -> assertThat(savedBook.get().getTitle()).isEqualTo("The Little Prince"),
                () -> assertThat(savedBook.get().getAuthor()).isEqualTo("Antoine de Saint-Exupéry"),
                () -> assertThat(savedBook.get().getGenre()).isEqualTo("Fiction"),
                () -> assertThat(savedBook.get().getPublishedDate()).isEqualTo("1943"),
                () -> assertThat(savedBook.get().getDescription()).isEqualTo("Most famous French book of the twentieth century, The Little Prince, is a poetic tale, with watercolour illustrations by the author, in which a pilot stranded in the desert meets a young prince fallen to Earth from a tiny asteroid. Through the prince's adventures and his encounters with a cast of extraordinary characters, the story explores themes of loneliness, friendship, love, and loss. The book's philosophical insights and reflections on human nature have made it a beloved classic for both children and adults."),
                () -> assertThat(savedBook.get().getLanguage()).isEqualTo("EN"),
                () -> assertThat(savedBook.get().getPageCount()).isEqualTo(96),
                () -> assertThat(savedBook.get().getPublisher()).isEqualTo("Reynal & Hitchcock")
        );
    }

    @Test
    void shouldReturnBadRequest_WhenUpdatingBook_WithMissingIsbn() {
        // Arrange
        BookDTO request = MockUtils.readBookDTOFromJson(fileBasePath + "MissingIsbn.json");
        JsonNode missingIsbnResponse = MockUtils.readJsonNodeFromFile(errorsBasePath + "MissingIsbn.json");

        // Act
        ResponseEntity<String> response = restTemplate.exchange("/api/v1/books", HttpMethod.PUT, new HttpEntity<>(request), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(missingIsbnResponse.toString());
    }

    @Test
    void shouldReturnBadRequest_WhenUpdatingBook_WithInvalidIsbn() {
        // Arrange
        BookDTO request = MockUtils.readBookDTOFromJson(fileBasePath + "InvalidIsbn.json");
        JsonNode invalidIsbnResponse = MockUtils.readJsonNodeFromFile(errorsBasePath + "InvalidIsbn.json");

        // Act
        ResponseEntity<String> response = restTemplate.exchange("/api/v1/books", HttpMethod.PUT, new HttpEntity<>(request), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(invalidIsbnResponse.toString());
    }

    @Test
    void shouldReturnNotFound_WhenUpdatingBook_ThatDoesNotExist() {
        // Arrange
        BookDTO request = MockUtils.readBookDTOFromJson(fileBasePath + "Success.json");
        JsonNode bookNotFoundResponse = MockUtils.readJsonNodeFromFile(errorsBasePath + "BookNotFoundInDatabase.json");

        // Act
        ResponseEntity<String> response = restTemplate.exchange("/api/v1/books", HttpMethod.PUT, new HttpEntity<>(request), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo(bookNotFoundResponse.toString());
    }

}
