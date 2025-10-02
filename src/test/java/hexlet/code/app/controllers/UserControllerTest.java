package hexlet.code.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private JwtRequestPostProcessor token;

    @BeforeEach
    void setUp() {
        token = jwt().jwt(builder -> builder.subject("test-user"));
    }

    private UserDto buildTestUser() {
        UserDto dto = new UserDto();
        dto.setEmail("test@example.com");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPassword("secret123");
        return dto;
    }

    @Test
    void testCreateUser() throws Exception {
        UserDto dto = buildTestUser();

        var response = mockMvc.perform(post("/api/users").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(dto.getEmail()))
                .andReturn();

        String json = response.getResponse().getContentAsString();
        assertThat(json).contains(dto.getEmail());
    }

    @Test
    void testGetAllUsers() throws Exception {
        UserDto dto = buildTestUser();

        mockMvc.perform(post("/api/users").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users").with(token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].email", hasItem(dto.getEmail())));
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDto dto = buildTestUser();

        var createResponse = mockMvc.perform(post("/api/users").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        UserDto created = objectMapper.readValue(createResponse.getResponse().getContentAsString(), UserDto.class);
        created.setEmail("updated@example.com");

        mockMvc.perform(put("/api/users/" + created.getId()).with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void testDeleteUserWithoutTasks() throws Exception {
        UserDto dto = buildTestUser();

        var createResponse = mockMvc.perform(post("/api/users").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        UserDto created = objectMapper.readValue(createResponse.getResponse().getContentAsString(), UserDto.class);

        mockMvc.perform(delete("/api/users/" + created.getId()).with(token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/" + created.getId()).with(token))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testDeleteUserWithTasksFails() throws Exception {
        UserDto dto = buildTestUser();


        var createResponse = mockMvc.perform(post("/api/users").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        UserDto created = objectMapper.readValue(createResponse.getResponse().getContentAsString(), UserDto.class);


        var statusDto = new hexlet.code.app.dto.TaskStatusDto();
        statusDto.setName("In Progress");
        statusDto.setSlug("in_progress");

        var statusResponse = mockMvc.perform(post("/api/task_statuses").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDto)))
                .andReturn();

        var createdStatus = objectMapper.readValue(statusResponse.getResponse().getContentAsString(),
                hexlet.code.app.dto.TaskStatusDto.class);

        var taskDto = new hexlet.code.app.dto.TaskDto();
        taskDto.setName("Test Task");
        taskDto.setStatusId(createdStatus.getId());
        taskDto.setAssigneeId(created.getId());

        mockMvc.perform(post("/api/tasks").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isOk());


        mockMvc.perform(delete("/api/users/" + created.getId()).with(token))
                .andExpect(status().isBadRequest());
    }
}
