import com.fasterxml.jackson.databind.JsonNode;
import com.github.leo791.personal_library.model.dto.BookDTO;
import com.github.leo791.personal_library.model.entity.Book;
import com.github.leo791.personal_library.repository.BookRepository;
import com.github.leo791.personal_library.service.BookMapper;
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

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * Integration Test for inserting a book manually.
 * Uses Testcontainers to spin up a temporary PostgreSQL database.
 * Tests the /api/v1/books/manual endpoint for various scenarios:
 * - Successfully inserting a book manually.
 * - Handling the case where the ISBN is missing.
 * - Handling invalid ISBN input.
 * - Handling the case where the book already exists in the database.
 */
@SpringBootTest(classes = com.github.leo791.personal_library.PersonalLibraryApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class InsertBookManuallyIT {

    private static final String fileBasePath = "src/component-test/resources/InsertBookManuallyStubs/";
    private static final String errorsBasePath = "src/component-test/resources/ErrorResponses/";
    private static final String descriptionEn = "For generations of enthralled readers, the mysterious millionaire Jay Gatsby has come to embody all the glamour and decadence of the Roaring Twenties. To F. Scott Fitzgerald’s bemused narrator, Nick Carraway, Gatsby appears to have emerged out of nowhere, evading questions about his murky past and throwing dazzling parties at his luxurious mansion. Nick finds something both appalling and appealing in the intensity of his new neighbor’s ambition, and his fascination grows when he discovers that Gatsby is obsessed by a long-lost love, Daisy Buchanan. But Daisy and her wealthy husband are cynical and careless people, and as Gatsby’s dream collides with reality, Nick is witness to the violence and tragedy that result. The Great Gatsby's remarkable staying power is owed to the lyrical freshness of its storytelling and to the way it illuminates the hollow core of the glittering American dream. With a new introduction by John Grisham.";
    private static final String isbn = "9780593311844";

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
    void shouldInsertBookManually() {

       // Arrange
       BookDTO requestAndResponse = MockUtils.readBookDTOFromJson(fileBasePath + "Success.json");

        // Act
        ResponseEntity<BookDTO> response = restTemplate.postForEntity("/api/v1/books/manual", requestAndResponse, BookDTO.class);

        BookDTO bookResponse = response.getBody();

        // Assert
        // Assert Response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(bookResponse).isNotNull();
        assertAll(
                () -> assertThat(bookResponse.getIsbn()).isEqualTo(isbn),
                () -> assertThat(bookResponse.getTitle()).isEqualTo("The Great Gatsby"),
                () -> assertThat(bookResponse.getAuthor()).isEqualTo("F. Scott Fitzgerald"),
                () -> assertThat(bookResponse.getGenre()).isEqualTo("Fiction"),
                () -> assertThat(bookResponse.getPublishedDate()).isEqualTo("2001"),
                () -> assertThat(bookResponse.getDescription()).isEqualTo(descriptionEn),
                () -> assertThat(bookResponse.getLanguage()).isEqualTo("EN"),
                () -> assertThat(bookResponse.getPageCount()).isEqualTo(194),
                () -> assertThat(bookResponse.getPublisher()).isEqualTo("Vintage")
        );

        // Assert Database state
        Optional<Book> savedBook = Optional.ofNullable(bookRepository.findByIsbn(isbn));
        assertThat(savedBook).isPresent();
        assertAll(
                () -> assertThat(savedBook.get().getIsbn()).isEqualTo(isbn),
                () -> assertThat(savedBook.get().getTitle()).isEqualTo("The Great Gatsby"),
                () -> assertThat(savedBook.get().getAuthor()).isEqualTo("F. Scott Fitzgerald"),
                () -> assertThat(savedBook.get().getGenre()).isEqualTo("Fiction"),
                () -> assertThat(savedBook.get().getPublishedDate()).isEqualTo("2001"),
                () -> assertThat(savedBook.get().getDescription()).isEqualTo(descriptionEn),
                () -> assertThat(savedBook.get().getLanguage()).isEqualTo("EN"),
                () -> assertThat(savedBook.get().getPageCount()).isEqualTo(194),
                () -> assertThat(savedBook.get().getPublisher()).isEqualTo("Vintage"));
    }
    @Test
    void shouldReturnBadRequest_WhenInsertingBookManually_WithMissingIsbn() {

        // Arrange
        BookDTO request = MockUtils.readBookDTOFromJson(fileBasePath + "MissingIsbn.json");
        JsonNode expectedResponse = MockUtils.readJsonNodeFromFile(errorsBasePath + "MissingIsbn.json");
        // Act
        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/books/manual", request, String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(expectedResponse.toString());
    }
    
    @Test
    void shouldReturnBadRequest_WhenInsertingBookManually_WithInvalidIsbn() {

        // Arrange
        BookDTO request = MockUtils.readBookDTOFromJson(fileBasePath + "InvalidIsbn.json");
        JsonNode expectedResponse = MockUtils.readJsonNodeFromFile(errorsBasePath + "InvalidIsbn.json");

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/books/manual", request, String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(expectedResponse.toString());
    }

    @Test
    void shouldReturnConflict_WhenInsertingBookManually_WhenBookExistsInDatabase()
    {
        // Arrange
        JsonNode expectedResponse = MockUtils.readJsonNodeFromFile(errorsBasePath + "BookExistsInLibrary.json");
        BookDTO request = MockUtils.readBookDTOFromJson(fileBasePath + "Success.json");
        BookMapper mapper = new BookMapper();
        Book existingBook = mapper.DTOtoBook(request);
        bookRepository.save(existingBook);

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/books/manual", request, String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isEqualTo(expectedResponse.toString());
    }
}
