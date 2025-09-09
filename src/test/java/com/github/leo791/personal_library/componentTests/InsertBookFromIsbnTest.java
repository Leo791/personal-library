package com.github.leo791.personal_library.componentTests;

import com.github.leo791.personal_library.model.dto.BookDTO;
import com.github.leo791.personal_library.model.entity.Book;
import com.github.leo791.personal_library.repository.BookRepository;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class InsertBookFromIsbnTest {

    private static final String isbn = "9780593311844"; // ISBN (for Great Gatsby)
    private static final String descriptionPt = "Para gerações de leitores fascinados, o misterioso milionário Jay Gatsby personificou todo o glamour e a decadência dos Loucos Anos Vinte. Para o narrador perplexo de F. Scott Fitzgerald, Nick Carraway, Gatsby parece ter surgido do nada, fugindo às perguntas sobre o seu passado obscuro e dando festas deslumbrantes na sua luxuosa mansão. Nick encontra algo ao mesmo tempo assustador e atraente na intensidade da ambição da sua nova vizinha, e o seu fascínio aumenta quando descobre que Gatsby é obcecado por um amor há muito perdido, Daisy Buchanan. Mas Daisy e o seu marido rico são pessoas cínicas e descuidadas e, à medida que o sonho de Gatsby colide com a realidade, Nick testemunha a violência e a tragédia resultantes. A notável permanência de O Grande Gatsby deve-se à frescura lírica da sua narrativa e à forma como ilumina o cerne vazio do brilhante sonho americano. Com uma nova introdução de John Grisham.";
    private static final String descriptionEn = "For generations of enthralled readers, the mysterious millionaire Jay Gatsby has come to embody all the glamour and decadence of the Roaring Twenties. To F. Scott Fitzgerald’s bemused narrator, Nick Carraway, Gatsby appears to have emerged out of nowhere, evading questions about his murky past and throwing dazzling parties at his luxurious mansion. Nick finds something both appalling and appealing in the intensity of his new neighbor’s ambition, and his fascination grows when he discovers that Gatsby is obsessed by a long-lost love, Daisy Buchanan. But Daisy and her wealthy husband are cynical and careless people, and as Gatsby’s dream collides with reality, Nick is witness to the violence and tragedy that result. The Great Gatsby's remarkable staying power is owed to the lyrical freshness of its storytelling and to the way it illuminates the hollow core of the glittering American dream. With a new introduction by John Grisham.";
    // Mock database
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    // Mock External APIs
    @RegisterExtension
    static WireMockExtension googleBooksMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @RegisterExtension
    static WireMockExtension openLibraryMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @RegisterExtension
    static WireMockExtension libreTranslateMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("google.books.api.base-url", googleBooksMock::baseUrl);
        registry.add("google.books.api.key", () -> "dummy-key");

        registry.add("openlibrary.api.base-url", openLibraryMock::baseUrl);
        registry.add("libretranslate.api.base-url", libreTranslateMock::baseUrl);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void shouldInsertBookFromIsbn_whenFoundInGoogleBooks_DescriptionTranslationNotRequired() {

        // Get the expected response
        String googleAPIResponse = null;
        try {
            googleAPIResponse = Files.readString(Paths.get("src/test/resources/GoogleApiBookFound.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Mock the responses from Google API using WireMock
        googleBooksMock.stubFor(WireMock.get(urlPathEqualTo("/books/v1/volumes"))
                .withQueryParam("q", equalTo("isbn:" + isbn))
                .withQueryParam("key", equalTo("dummy-key"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(googleAPIResponse)));

        // Mock the response from LibreTranslate API using WireMock
        libreTranslateMock.stubFor(WireMock.post(urlPathEqualTo("/detect"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"language\":\"en\",\"confidence\":1}]")));

        // Act
        ResponseEntity<BookDTO> response = restTemplate.postForEntity("/api/v1/books?isbn=" + isbn, null, BookDTO.class);

        BookDTO bookResponse = response.getBody();

        // Assert Response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(bookResponse).isNotNull();
        assertAll(
                () -> assertThat(bookResponse.getIsbn()).isEqualTo(isbn),
                () -> assertThat(bookResponse.getTitle()).isEqualTo("The Great Gatsby"),
                () -> assertThat(bookResponse.getAuthor()).isEqualTo("F. Scott Fitzgerald"),
                () -> assertThat(bookResponse.getGenre()).isEqualTo("Fiction"),
                () -> assertThat(bookResponse.getPublishedDate()).isEqualTo("2021"),
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
                () -> assertThat(savedBook.get().getPublishedDate()).isEqualTo("2021"),
                () -> assertThat(savedBook.get().getDescription()).isEqualTo(descriptionEn),
                () -> assertThat(savedBook.get().getLanguage()).isEqualTo("EN"),
                () -> assertThat(savedBook.get().getPageCount()).isEqualTo(194),
                () -> assertThat(savedBook.get().getPublisher()).isEqualTo("Vintage")
        );
    }

    @Test
    void shouldInsertBookFromIsbn_whenFoundInGoogleBooks_DescriptionTranslationRequired() {

        // Get the expected response
        String googleAPIResponse = null;
        try {
            googleAPIResponse = Files.readString(Paths.get("src/test/resources/GoogleApiBookFoundTranslationRequired.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Mock the responses from Google API using WireMock
        googleBooksMock.stubFor(WireMock.get(urlPathEqualTo("/books/v1/volumes"))
                .withQueryParam("q", equalTo("isbn:" + isbn))
                .withQueryParam("key", equalTo("dummy-key"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(googleAPIResponse)));

        // Mock the response from LibreTranslate API using WireMock
        libreTranslateMock.stubFor(WireMock.post(urlPathEqualTo("/detect"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"language\":\"pt\",\"confidence\":1}]")));

        libreTranslateMock.stubFor(WireMock.post(urlPathEqualTo("/translate"))
                .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded"))
                .withRequestBody(matching(".*q=.*source=pt.*target=(?i)en.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"translatedText\":\"" + descriptionEn + "\"}")));

        // Act
        ResponseEntity<BookDTO> response = restTemplate.postForEntity("/api/v1/books?isbn=" + isbn, null, BookDTO.class);

        BookDTO bookResponse = response.getBody();

        // Assert Response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(bookResponse).isNotNull();
        assertAll(
                () -> assertThat(bookResponse.getIsbn()).isEqualTo(isbn),
                () -> assertThat(bookResponse.getTitle()).isEqualTo("The Great Gatsby"),
                () -> assertThat(bookResponse.getAuthor()).isEqualTo("F. Scott Fitzgerald"),
                () -> assertThat(bookResponse.getGenre()).isEqualTo("Fiction"),
                () -> assertThat(bookResponse.getPublishedDate()).isEqualTo("2021"),
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
                () -> assertThat(savedBook.get().getPublishedDate()).isEqualTo("2021"),
                () -> assertThat(savedBook.get().getDescription()).isEqualTo(descriptionEn),
                () -> assertThat(savedBook.get().getLanguage()).isEqualTo("EN"),
                () -> assertThat(savedBook.get().getPageCount()).isEqualTo(194),
                () -> assertThat(savedBook.get().getPublisher()).isEqualTo("Vintage")
        );
    }

    @Test
    void shouldInsertBookFromIsbn_whenNotFoundInGoogleBooks_FoundInOpenLibrary(){
        // Get the expected responses
        String googleAPIResponse = null;
        String openLibraryBookResponse = null;
        String openLibraryAuthorResponse = null;
        try {
            googleAPIResponse = Files.readString(Paths.get("src/test/resources/GoogleApiBookNotFound.json"));
            openLibraryBookResponse = Files.readString(Paths.get("src/test/resources/OpenLibraryBookFound.json"));
            openLibraryAuthorResponse = Files.readString(Paths.get("src/test/resources/OpenLibraryAuthorFound.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Mock the responses from Google API using WireMock
        googleBooksMock.stubFor(WireMock.get(urlPathEqualTo("/books/v1/volumes"))
                .withQueryParam("q", equalTo("isbn:" + isbn))
                .withQueryParam("key", equalTo("dummy-key"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(googleAPIResponse)));

        // Mock the responses from OpenLibrary API using WireMock
        openLibraryMock.stubFor(WireMock.get(urlPathEqualTo("/isbn/" + isbn + ".json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(openLibraryBookResponse)));

        openLibraryMock.stubFor(WireMock.get(urlPathEqualTo("/authors/OL27349A.json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(openLibraryAuthorResponse)));

        // Act
        ResponseEntity<BookDTO> response = restTemplate.postForEntity("/api/v1/books?isbn=" + isbn, null, BookDTO.class);
        BookDTO bookResponse = response.getBody();

        // Assert Response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(bookResponse).isNotNull();
        assertAll(
                () -> assertThat(bookResponse.getIsbn()).isEqualTo(isbn),
                () -> assertThat(bookResponse.getTitle()).isEqualTo("The Great Gatsby"),
                () -> assertThat(bookResponse.getAuthor()).isEqualTo("F. Scott Fitzgerald"),
                () -> assertThat(bookResponse.getGenre()).isEmpty(),
                () -> assertThat(bookResponse.getPublishedDate()).isEqualTo("2021"),
                () -> assertThat(bookResponse.getDescription()).isEmpty(),
                () -> assertThat(bookResponse.getLanguage()).isEqualTo(""),
                () -> assertThat(bookResponse.getPageCount()).isEqualTo(192),
                () -> assertThat(bookResponse.getPublisher()).isEqualTo("Vintage"));

        // Assert Database state
        Optional<Book> savedBook = Optional.ofNullable(bookRepository.findByIsbn(isbn));
        assertThat(savedBook).isPresent();
        assertAll(
                () -> assertThat(savedBook.get().getIsbn()).isEqualTo(isbn),
                () -> assertThat(savedBook.get().getTitle()).isEqualTo("The Great Gatsby"),
                () -> assertThat(savedBook.get().getAuthor()).isEqualTo("F. Scott Fitzgerald"),
                () -> assertThat(savedBook.get().getGenre()).isEmpty(),
                () -> assertThat(savedBook.get().getPublishedDate()).isEqualTo("2021"),
                () -> assertThat(savedBook.get().getDescription()).isEmpty(),
                () -> assertThat(savedBook.get().getLanguage()).isEqualTo(""),
                () -> assertThat(savedBook.get().getPageCount()).isEqualTo(192),
                () -> assertThat(savedBook.get().getPublisher()).isEqualTo("Vintage"));


    }
}
