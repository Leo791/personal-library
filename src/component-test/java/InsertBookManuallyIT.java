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

@SpringBootTest(classes = com.github.leo791.personal_library.PersonalLibraryApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class InsertBookManuallyIT {

    private static final String fileBasePath = "src/component-test/resources/InsertBookManuallyStubs/";
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
                () -> assertThat(bookResponse.getTitle()).isEqualTo("Mulher De Porto Pim"),
                () -> assertThat(bookResponse.getAuthor()).isEqualTo("Antonio Tabucchi"),
                () -> assertThat(bookResponse.getGenre()).isEqualTo("Fiction"),
                () -> assertThat(bookResponse.getPublishedDate()).isEqualTo("2016"),
                () -> assertThat(bookResponse.getDescription()).isEqualTo(""),
                () -> assertThat(bookResponse.getLanguage()).isEqualTo("PT"),
                () -> assertThat(bookResponse.getPageCount()).isEqualTo(128),
                () -> assertThat(bookResponse.getPublisher()).isEqualTo("Dom Quixote")
        );

        // Assert Database state
        Optional<Book> savedBook = Optional.ofNullable(bookRepository.findByIsbn(isbn));
        assertThat(savedBook).isPresent();
        assertAll(
                () -> assertThat(savedBook.get().getIsbn()).isEqualTo(isbn),
                () -> assertThat(savedBook.get().getTitle()).isEqualTo("Mulher De Porto Pim"),
                () -> assertThat(savedBook.get().getAuthor()).isEqualTo("Antonio Tabucchi"),
                () -> assertThat(savedBook.get().getGenre()).isEqualTo("Fiction"),
                () -> assertThat(savedBook.get().getPublishedDate()).isEqualTo("2016"),
                () -> assertThat(savedBook.get().getDescription()).isEqualTo(""),
                () -> assertThat(savedBook.get().getLanguage()).isEqualTo("PT"),
                () -> assertThat(savedBook.get().getPageCount()).isEqualTo(128),
                () -> assertThat(savedBook.get().getPublisher()).isEqualTo("Dom Quixote")
        );
    }
    @Test
    void shouldReturnBadRequest_WhenInsertingBookManually_WithMissingIsbn() {

        // Arrange
        BookDTO request = MockUtils.readBookDTOFromJson(fileBasePath + "MissingIsbn.json");
        // Act
        ResponseEntity<BookDTO> response = restTemplate.postForEntity("/api/v1/books/manual", request, BookDTO.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    
    @Test
    void shouldReturnBadRequest_WhenInsertingBookManually_WithInvalidIsbn() {

        // Arrange
        BookDTO request = MockUtils.readBookDTOFromJson(fileBasePath + "InvalidIsbn.json");

        // Act
        ResponseEntity<BookDTO> response = restTemplate.postForEntity("/api/v1/books/manual", request, BookDTO.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturnConflict_WhenInsertingBookManually_WhenBookExistsInDatabase()
    {
        // Arrange
        BookDTO request = MockUtils.readBookDTOFromJson(fileBasePath + "Success.json");
        BookMapper mapper = new BookMapper();
        Book existingBook = mapper.DTOtoBook(request);
        bookRepository.save(existingBook);

        // Act
        ResponseEntity<BookDTO> response = restTemplate.postForEntity("/api/v1/books/manual", request, BookDTO.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
