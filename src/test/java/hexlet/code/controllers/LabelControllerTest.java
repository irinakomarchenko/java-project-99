package hexlet.code.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.repository.LabelRepository;
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

import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class LabelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUtils testUtils;

    private JwtRequestPostProcessor token;

    @BeforeEach
    void setUp() {

        taskRepository.deleteAll();
        labelRepository.deleteAll();
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();

        token = jwt().jwt(builder -> builder.subject("test-user"));
    }

    private LabelDto buildTestLabel() {
        LabelDto dto = new LabelDto();
        dto.setName("Bug-" + System.currentTimeMillis()); // уникальное имя для каждого теста
        return dto;
    }

    @Test
    void testCreateLabel() throws Exception {
        LabelDto dto = buildTestLabel();

        var response = mockMvc.perform(post("/api/labels").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
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
                .andExpect(status().isCreated());

        var response = mockMvc.perform(get("/api/labels").with(token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        List<LabelDto> labelsFromApi = testUtils.parseListResponse(response.getContentAsString(), LabelDto.class);

        var labelsFromDb = labelRepository.findAll();

        assertThat(labelsFromApi)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id", "createdAt")
                .containsExactlyInAnyOrderElementsOf(
                        labelsFromDb.stream()
                                .map(label -> {
                                    LabelDto labelDto = new LabelDto();
                                    labelDto.setName(label.getName());
                                    return labelDto;
                                })
                                .toList()
                );
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
                .andExpect(status().isCreated())
                .andReturn();
        LabelDto createdLabel = objectMapper.readValue(labelResp.getResponse().getContentAsString(), LabelDto.class);

        TaskStatusDto status = new TaskStatusDto();
        status.setName("Open");
        status.setSlug("open");
        var statusResp = mockMvc.perform(post("/api/task_statuses").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(status)))
                .andExpect(status().isCreated())
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
                .andExpect(status().isCreated())
                .andReturn();
        UserDto createdUser = objectMapper.readValue(userResp.getResponse().getContentAsString(), UserDto.class);

        TaskDto task = new TaskDto();
        task.setTitle("Bugfix");
        task.setContent("Task description");
        task.setStatusId(createdStatus.getId());
        task.setAssigneeId(createdUser.getId());
        task.setLabelIds(Set.of(createdLabel.getId()));

        mockMvc.perform(post("/api/tasks").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/labels/" + createdLabel.getId()).with(token))
                .andExpect(status().isUnprocessableEntity());
    }
}
