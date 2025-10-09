package hexlet.code.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.UserDto;
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
import java.util.UUID;
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
class TaskStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;


    private JwtRequestPostProcessor token;

    @BeforeEach
    void setUp() {

        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        token = jwt().jwt(builder -> builder.subject("test-user"));
    }

    private TaskStatusDto buildTestStatus() {
        TaskStatusDto dto = new TaskStatusDto();
        dto.setName("In Progress " +  UUID.randomUUID());
        dto.setSlug("in_progress_" + UUID.randomUUID());
        return dto;
    }

    @Test
    void testCreateTaskStatus() throws Exception {
        TaskStatusDto dto = buildTestStatus();

        var response = mockMvc.perform(post("/api/task_statuses").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andReturn();

        String json = response.getResponse().getContentAsString();
        assertThat(json).contains(dto.getSlug());
    }

    @Test
    void testGetAllStatuses() throws Exception {
        TaskStatusDto dto = buildTestStatus();

        mockMvc.perform(post("/api/task_statuses").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/task_statuses").with(token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", hasItem(dto.getName())));
    }

    @Test
    void testUpdateTaskStatus() throws Exception {
        TaskStatusDto dto = buildTestStatus();

        var createResponse = mockMvc.perform(post("/api/task_statuses").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        TaskStatusDto created = objectMapper.readValue(createResponse.getResponse().getContentAsString(),
                TaskStatusDto.class);
        created.setName("Done");

        mockMvc.perform(put("/api/task_statuses/" + created.getId()).with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Done"));
    }

    @Test
    void testDeleteTaskStatusWithoutTasks() throws Exception {
        TaskStatusDto dto = buildTestStatus();

        var createResponse = mockMvc.perform(post("/api/task_statuses").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        TaskStatusDto created = objectMapper.readValue(createResponse.getResponse().getContentAsString(),
                TaskStatusDto.class);

        mockMvc.perform(delete("/api/task_statuses/" + created.getId()).with(token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/task_statuses/" + created.getId()).with(token))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testDeleteTaskStatusWithTasksFails() throws Exception {
        TaskStatusDto dto = buildTestStatus();

        var createResponse = mockMvc.perform(post("/api/task_statuses").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        TaskStatusDto createdStatus = objectMapper.readValue(createResponse.getResponse().getContentAsString(),
                TaskStatusDto.class);

        var userDto = new UserDto();
        userDto.setEmail("user@example.com");
        userDto.setFirstName("Alice");
        userDto.setLastName("Smith");
        userDto.setPassword("secret123");

        var userResponse = mockMvc.perform(post("/api/users").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn();

        var createdUser = objectMapper.readValue(userResponse.getResponse().getContentAsString(),
                UserDto.class);

        var taskDto = new TaskDto();
        taskDto.setTitle("Linked Task");
        taskDto.setContent("Task description");
        taskDto.setStatusId(createdStatus.getId());
        taskDto.setAssigneeId(createdUser.getId());

        mockMvc.perform(post("/api/tasks").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/task_statuses/" + createdStatus.getId()).with(token))
                .andExpect(status().isUnprocessableEntity());
    }
}
