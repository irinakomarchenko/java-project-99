package hexlet.code.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UserDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
        .JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private JwtRequestPostProcessor token;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;


    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();
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
        UserDto dto = buildTestUser();

        var response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(dto.getEmail()))
                .andReturn();

        String json = response.getResponse().getContentAsString();
        assertThat(json).contains(dto.getEmail());
    }

    @Test
    void testGetAllUsers() throws Exception {
        UserDto dto = buildTestUser();

        var createResponse = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        UserDto created = objectMapper.readValue(createResponse.getResponse().getContentAsString(), UserDto.class);
        var userToken = jwt().jwt(builder -> builder.subject(created.getEmail()));

        mockMvc.perform(get("/api/users").with(userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].email", hasItem(dto.getEmail())));
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDto dto = buildTestUser();

        var createResponse = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        UserDto created = objectMapper.readValue(createResponse.getResponse().getContentAsString(), UserDto.class);

        var userToken = jwt().jwt(builder -> builder.subject(created.getEmail()));

        created.setEmail("updated@example.com");
        mockMvc.perform(put("/api/users/" + created.getId()).with(userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void testDeleteUserWithoutTasks() throws Exception {
        UserDto dto = buildTestUser();

        var createResponse = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        UserDto created = objectMapper.readValue(createResponse.getResponse().getContentAsString(), UserDto.class);

        var userToken = jwt().jwt(builder -> builder.subject(created.getEmail()));

        mockMvc.perform(delete("/api/users/" + created.getId()).with(userToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/" + created.getId()).with(userToken))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testDeleteUserWithTasksFails() throws Exception {
        UserDto dto = buildTestUser();

        var createResponse = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        UserDto created = objectMapper.readValue(createResponse.getResponse().getContentAsString(), UserDto.class);
        var userToken = jwt().jwt(builder -> builder.subject(created.getEmail()));

        var statusDto = new TaskStatusDto();
        statusDto.setName("In Progress");
        statusDto.setSlug("in_progress");

        var statusResponse = mockMvc.perform(post("/api/task_statuses").with(userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDto)))
                .andExpect(status().isCreated())
                .andReturn();

        var createdStatus = objectMapper.readValue(statusResponse.getResponse().getContentAsString(),
                TaskStatusDto.class);

        var taskDto = new TaskDto();
        taskDto.setTitle("Test Task");
        taskDto.setContent("Task description");
        taskDto.setStatusId(createdStatus.getId());
        taskDto.setAssigneeId(created.getId());

        mockMvc.perform(post("/api/tasks").with(userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/users/" + created.getId()).with(userToken))
                .andExpect(status().isUnprocessableEntity());
    }

}
