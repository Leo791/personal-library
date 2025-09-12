import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leo791.personal_library.model.dto.BookDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
}
