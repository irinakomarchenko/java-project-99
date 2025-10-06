package hexlet.code.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.service.TaskStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
        .JwtRequestPostProcessor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskStatusService statusService;

    private JwtRequestPostProcessor token;

    private Long defaultStatusId;

    @BeforeEach
    void setUp() {
        token = jwt().jwt(builder -> builder.subject("test-user"));
        TaskStatusDto status = new TaskStatusDto();
        status.setName("Draft");
        status.setSlug("draft");
        defaultStatusId = statusService.create(status).getId();
    }

    private TaskDto buildTestTask() {
        TaskDto dto = new TaskDto();
        dto.setTitle("Test Task");
        dto.setContent("Some description");
        dto.setStatusId(defaultStatusId);
        return dto;
    }

    @Test
    void testCreateTask() throws Exception {
        TaskDto dto = buildTestTask();
        var response = mockMvc.perform(post("/api/tasks").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(dto.getTitle()))
                .andReturn();
        String json = response.getResponse().getContentAsString();
        assertThat(json).contains(dto.getTitle());
    }

    @Test
    void testGetAllTasks() throws Exception {
        TaskDto dto = buildTestTask();
        mockMvc.perform(post("/api/tasks").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/tasks").with(token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", hasItem(dto.getTitle())));
    }

    @Test
    void testUpdateTask() throws Exception {
        TaskDto dto = buildTestTask();
        var createResponse = mockMvc.perform(post("/api/tasks").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();
        TaskDto created = objectMapper.readValue(createResponse.getResponse().getContentAsString(), TaskDto.class);
        created.setTitle("Updated Task");
        mockMvc.perform(put("/api/tasks/" + created.getId()).with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"));
    }

    @Test
    void testDeleteTask() throws Exception {
        TaskDto dto = buildTestTask();
        var createResponse = mockMvc.perform(post("/api/tasks").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();
        TaskDto created = objectMapper.readValue(createResponse.getResponse().getContentAsString(), TaskDto.class);
        mockMvc.perform(delete("/api/tasks/" + created.getId()).with(token))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/tasks/" + created.getId()).with(token))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testFilterTasksByTitle() throws Exception {
        TaskDto dto1 = buildTestTask();
        dto1.setTitle("First Task");
        TaskDto dto2 = buildTestTask();
        dto2.setTitle("Second Task");
        mockMvc.perform(post("/api/tasks").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/tasks").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2)))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/tasks")
                        .param("titleCont", "First")
                        .with(token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", hasItem("First Task")))
                .andExpect(jsonPath("$[*].title", not(hasItem("Second Task"))));
    }
}
