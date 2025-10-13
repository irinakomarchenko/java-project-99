package hexlet.code.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UserDto;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
        .JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TestUtils testUtils;

    private JwtRequestPostProcessor token;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        token = jwt().jwt(builder -> builder.subject("test-user"));

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
    }

    private UserDto buildTestUser() {
        UserDto dto = new UserDto();
        dto.setEmail("test@simple.com");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPassword("secret123");
        return dto;
    }

    @Test
    void testCreateUser() throws Exception {
        var dto = buildTestUser();

        var response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        var body = response.getResponse().getContentAsString();
        assertThat(body).contains(dto.getEmail());
    }

    @Test
    void testGetAllUsers() throws Exception {
        var dto = buildTestUser();

        mockMvc.perform(post("/api/users").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        var response = mockMvc.perform(get("/api/users").with(token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        List<UserDto> usersFromApi = testUtils.parseListResponse(response.getContentAsString(), UserDto.class);
        var usersFromDb = userRepository.findAll();

        assertThat(usersFromApi)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id", "password", "createdAt", "updatedAt")
                .containsExactlyInAnyOrderElementsOf(
                        usersFromDb.stream()
                                .map(u -> {
                                    UserDto dto2 = new UserDto();
                                    dto2.setEmail(u.getEmail());
                                    dto2.setFirstName(u.getFirstName());
                                    dto2.setLastName(u.getLastName());
                                    return dto2;
                                })
                                .toList()
                );
    }

    @Test
    void testUpdateUser() throws Exception {
        var dto = buildTestUser();

        var createResponse = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        var created = objectMapper.readValue(createResponse.getResponse().getContentAsString(), UserDto.class);
        token = jwt().jwt(builder -> builder.subject(created.getEmail()));

        created.setEmail("updated@example.com");

        mockMvc.perform(put("/api/users/" + created.getId()).with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk());

        var updatedUser = userRepository.findById(created.getId()).orElseThrow();
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void testDeleteUserWithoutTasks() throws Exception {
        var dto = buildTestUser();

        var createResponse = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        var created = objectMapper.readValue(createResponse.getResponse().getContentAsString(), UserDto.class);
        token = jwt().jwt(builder -> builder.subject(created.getEmail()));

        mockMvc.perform(delete("/api/users/" + created.getId()).with(token))
                .andExpect(status().isNoContent());

        assertThat(userRepository.existsById(created.getId())).isFalse();
    }
}
