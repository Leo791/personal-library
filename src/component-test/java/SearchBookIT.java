import com.github.leo791.personal_library.model.dto.BookDTO;
import com.github.leo791.personal_library.model.entity.Book;
import com.github.leo791.personal_library.repository.BookRepository;
import org.junit.jupiter.api.*;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
    Integration Test for searching books by title, author, or genre.
    Uses Testcontainers to spin up a temporary PostgreSQL database.
    Pre-populates the database with sample books before each test and cleans up after each test.
    Tests the /api/v1/books endpoint with different query parameters.
    - Successfully searching books by title.
    - Successfully searching books by author.
    - Successfully searching books by genre.
    - Returning all books when no search criteria is provided.
    - Returning an empty list when no books match the search criteria.
    The test library is created using MockUtils.createSampleLibrary() and contains:
    - "The Little Prince" by Antoine de Saint-Exupéry (Genre: Fiction)
    - "The Shining" by Stephen King (Genre: Horror)
    - "Mr. Mercedes" by Stephen King (Genre: Fiction)
*/
@SpringBootTest(classes = com.github.leo791.personal_library.PersonalLibraryApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class SearchBookIT {

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

    @BeforeEach
    void setUpLibrary(){
        List<Book> library = MockUtils.createSampleLibrary();
        bookRepository.saveAll(library);
        System.out.println("Library is: " + bookRepository.findAll());
    }

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
    }


    @Test
    void shouldSearchBooksByTitle(){

        // Act
        String title = "The Little Prince";
        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
        ResponseEntity<BookDTO[]> response = restTemplate.getForEntity("/api/v1/books?title=" + encodedTitle, BookDTO[].class);
        BookDTO[] booksResponse = response.getBody();

        // Assert Response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(booksResponse);
        Assertions.assertEquals(1, booksResponse.length);

        BookDTO bookResponse = booksResponse[0];
        assertThat(bookResponse).isNotNull();
        assertThat(bookResponse.getTitle()).isEqualTo("The Little Prince");
    }

    @Test
    void shouldSearchBooksByAuthor(){
        // Act
        String title = "Stephen King";
        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
        ResponseEntity<BookDTO[]> response = restTemplate.getForEntity("/api/v1/books?author=" + encodedTitle, BookDTO[].class);
        BookDTO[] booksResponse = response.getBody();

        // Assert Response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(booksResponse);
        Assertions.assertEquals(2, booksResponse.length);

        BookDTO book1 = booksResponse[0];
        BookDTO book2 = booksResponse[1];

        assertThat(book1).isNotNull();
        assertThat(book2).isNotNull();
        assertAll(
                () -> assertThat(book1.getAuthor()).isEqualTo("Stephen King"),
                () -> assertThat(book2.getAuthor()).isEqualTo("Stephen King")
        );
    }

    @Test
    void shouldSearchBooksByGenre(){
        // Act
        String genre = "Horror";
        String encodedGenre = URLEncoder.encode(genre, StandardCharsets.UTF_8);
        ResponseEntity<BookDTO[]> response = restTemplate.getForEntity("/api/v1/books?genre=" + encodedGenre, BookDTO[].class);
        BookDTO[] booksResponse = response.getBody();

        // Assert Response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(booksResponse);
        Assertions.assertEquals(1, booksResponse.length);

        BookDTO bookResponse = booksResponse[0];
        assertThat(bookResponse).isNotNull();
        assertThat(bookResponse.getGenre()).isEqualTo("Horror");
    }

    @Test
    void shouldReturnAllBooks_WhenNoSearchCriteriaProvided(){
        // Act
        ResponseEntity<BookDTO[]> response = restTemplate.getForEntity("/api/v1/books", BookDTO[].class);
        BookDTO[] booksResponse = response.getBody();

        // Assert Response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(booksResponse);
        Assertions.assertEquals(3, booksResponse.length);
    }

    @Test
    void shouldReturnEmptyList_WhenNoBooksMatchSearchCriteria(){
        // Act
        String title = "Nonexistent Book Title";
        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
        ResponseEntity<BookDTO[]> response = restTemplate.getForEntity("/api/v1/books?title=" + encodedTitle, BookDTO[].class);
        BookDTO[] booksResponse = response.getBody();

        // Assert Response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(booksResponse);
        Assertions.assertEquals(0, booksResponse.length);
    }
}
