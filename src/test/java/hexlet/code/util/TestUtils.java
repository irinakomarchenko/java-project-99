package hexlet.code.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Utility helper for simplifying MockMvc tests.
 * Provides generic JSON parsing and reusable helpers for controller tests.
 */
@Component
public class TestUtils {

    private final ObjectMapper objectMapper;

    public TestUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Parses JSON string into a list of DTOs.
     *
     * @param json  the JSON response body
     * @param clazz the DTO class type
     * @param <T>   the type of objects inside the list
     * @return deserialized list of DTO objects
     * @throws Exception if JSON parsing fails
     */
    public <T> List<T> parseListResponse(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(
                json,
                objectMapper.getTypeFactory().constructCollectionType(List.class, clazz)
        );
    }

    /**
     * Parses JSON string into a single DTO.
     *
     * @param json  the JSON response body
     * @param clazz the DTO class type
     * @param <T>   the type of object
     * @return deserialized DTO object
     * @throws Exception if JSON parsing fails
     */
    public <T> T parseObjectResponse(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }
}
