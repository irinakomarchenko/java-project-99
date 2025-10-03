package hexlet.code.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.LabelDto;
import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.dto.TaskStatusDto;
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

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LabelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private JwtRequestPostProcessor token;

    @BeforeEach
    void setUp() {
        token = jwt().jwt(builder -> builder.subject("test-user"));
    }

    private LabelDto buildTestLabel() {
        LabelDto dto = new LabelDto();
        dto.setName("Bug");
        return dto;
    }

    @Test
    void testCreateLabel() throws Exception {
        LabelDto dto = buildTestLabel();

        var response = mockMvc.perform(post("/api/labels").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andReturn();

        String json = response.getResponse().getContentAsString();
        assertThat(json).contains(dto.getName());
    }

    @Test
    void testGetAllLabels() throws Exception {
        LabelDto dto = buildTestLabel();

        mockMvc.perform(post("/api/labels").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/labels").with(token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", hasItem(dto.getName())));
    }

    @Test
    void testUpdateLabel() throws Exception {
        LabelDto dto = buildTestLabel();

        var createResponse = mockMvc.perform(post("/api/labels").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        LabelDto created = objectMapper.readValue(createResponse.getResponse().getContentAsString(),
                LabelDto.class);
        created.setName("Feature");

        mockMvc.perform(put("/api/labels/" + created.getId()).with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Feature"));
    }

    @Test
    void testDeleteLabelWithoutTasks() throws Exception {
        LabelDto dto = buildTestLabel();

        var createResponse = mockMvc.perform(post("/api/labels").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        LabelDto created = objectMapper.readValue(createResponse.getResponse().getContentAsString(),
                LabelDto.class);

        mockMvc.perform(delete("/api/labels/" + created.getId()).with(token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/labels/" + created.getId()).with(token))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testDeleteLabelWithTasksFails() throws Exception {

        LabelDto label = buildTestLabel();
        var labelResp = mockMvc.perform(post("/api/labels").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(label)))
                .andReturn();
        LabelDto createdLabel = objectMapper.readValue(labelResp.getResponse().getContentAsString(), LabelDto.class);

        TaskStatusDto status = new TaskStatusDto();
        status.setName("Open");
        status.setSlug("open");
        var statusResp = mockMvc.perform(post("/api/task_statuses").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(status)))
                .andReturn();
        TaskStatusDto createdStatus = objectMapper.readValue(statusResp.getResponse().getContentAsString(),
                TaskStatusDto.class);

        UserDto user = new UserDto();
        user.setEmail("user@example.com");
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setPassword("secret123");
        var userResp = mockMvc.perform(post("/api/users").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andReturn();
        UserDto createdUser = objectMapper.readValue(userResp.getResponse().getContentAsString(), UserDto.class);

        TaskDto task = new TaskDto();
        task.setName("Bugfix");
        task.setStatusId(createdStatus.getId());
        task.setAssigneeId(createdUser.getId());
        task.setLabelIds(Set.of(createdLabel.getId()));

        mockMvc.perform(post("/api/tasks").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/labels/" + createdLabel.getId()).with(token))
                .andExpect(status().isConflict());
    }
}
