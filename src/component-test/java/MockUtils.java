import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leo791.personal_library.model.dto.BookDTO;
import com.github.leo791.personal_library.model.entity.Book;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class MockUtils {

    public static BookDTO readBookDTOFromJson(String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = Files.readString(Paths.get(fileName));
            return objectMapper.readValue(json, BookDTO.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode readJsonNodeFromFile(String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = Files.readString(Paths.get(fileName));
            return objectMapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readStringFromFile(String fileName) {
        try {
            return Files.readString(Paths.get(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Book createSampleBook() {
        return new Book(
                "9789722060172",
                "The Little Prince",
                "Antoine de Saint-Exupéry",
                "Fiction",
                "A poetic tale of a young prince who travels from planet to planet, learning about life, love, and human nature.",
                "ENG",
                96,
                "Reynal & Hitchcock",
                "1943"
        );
    }

    public static List<Book> createSampleLibrary() {
        return List.of(
                new Book(
                        "9789722060172",
                        "The Little Prince",
                        "Antoine de Saint-Exupéry",
                        "Fiction",
                        "A poetic tale of a young prince who travels from planet to planet, learning about life, love, and human nature.",
                        "ENG",
                        96,
                        "Reynal & Hitchcock",
                        "1943"
                ),
                new Book(
                        "9781476754475",
                         "Mr. Mercedes",
                         "Stephen King",
                         "Fiction",
                         "A maniac accelerates a Mercedes into hundreds of unemployed applicants lined up at a job fair killing eight and wounding 15. Det. Bill Hodges, a streetwise inspector, searches unsuccessfully for the Mercedes killer, a lunatic named Brady Hartfield, who promises to strike again in an even more diabolical manner.",
                         "EN",
                         448,
                         "Simon And Schuster",
                         "2014"
                ),
                new Book(
                        "9780552164933",
                        "The Shining",
                        "Stephen King",
                        "Horror",
                        "Jack Torrance's new job at the Overlook Hotel is the perfect chance for a fresh start. As the off-season caretaker at the isolated hotel, he'll have plenty of time to work on his writing and reconnect with his family. But as the harsh winter weather sets in, the idyllic location feels ever more remote... and a sinister presence within the hotel begins to affect Jack's sanity.",
                        "EN",
                        447,
                        "Doubleday",
                        "1977"
                ));
    }
}
