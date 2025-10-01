package hexlet.code.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.TaskStatusDto;
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
class TaskStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private JwtRequestPostProcessor token;

    @BeforeEach
    void setUp() {
        token = jwt().jwt(builder -> builder.subject("test-user"));
    }

    private TaskStatusDto buildTestStatus() {
        TaskStatusDto dto = new TaskStatusDto();
        dto.setName("In Progress");
        dto.setSlug("in_progress");
        return dto;
    }

    @Test
    void testCreateTaskStatus() throws Exception {
        TaskStatusDto dto = buildTestStatus();

        var response = mockMvc.perform(post("/api/task_statuses").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
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
                .andExpect(status().isOk());

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

        TaskStatusDto created = objectMapper.readValue(createResponse.getResponse().getContentAsString(), TaskStatusDto.class);
        created.setName("Done");

        mockMvc.perform(put("/api/task_statuses/" + created.getId()).with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Done"));
    }

    @Test
    void testDeleteTaskStatus() throws Exception {
        TaskStatusDto dto = buildTestStatus();

        var createResponse = mockMvc.perform(post("/api/task_statuses").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        TaskStatusDto created = objectMapper.readValue(createResponse.getResponse().getContentAsString(), TaskStatusDto.class);

        mockMvc.perform(delete("/api/task_statuses/" + created.getId()).with(token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/task_statuses/" + created.getId()).with(token))
                .andExpect(status().is4xxClientError());
    }
}
