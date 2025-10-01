package hexlet.code.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.util.ModelGenerator;
import org.instancio.Instancio;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasItem;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelGenerator generator;

    @Autowired
    private ObjectMapper objectMapper;

    private JwtRequestPostProcessor token;

    @BeforeEach
    void setUp() {
        token = jwt().jwt(builder -> builder.subject("test-user"));
    }

    @Test
    void testCreateUser() throws Exception {
        User user = Instancio.create(generator.getUserModel());

        var response = mockMvc.perform(post("/api/users").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andReturn();

        String json = response.getResponse().getContentAsString();
        assertThat(json).contains(user.getEmail());
    }


    @Test
    void testGetAllUsers() throws Exception {
        User user = Instancio.create(generator.getUserModel());

        mockMvc.perform(post("/api/users").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users").with(token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].email", hasItem(user.getEmail())));
    }


    @Test
    void testUpdateUser() throws Exception {
        User user = Instancio.create(generator.getUserModel());

        var createResponse = mockMvc.perform(post("/api/users").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andReturn();

        User createdUser = objectMapper.readValue(createResponse.getResponse().getContentAsString(), User.class);
        createdUser.setEmail("updated@example.com");

        mockMvc.perform(put("/api/users/" + createdUser.getId()).with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void testDeleteUser() throws Exception {
        User user = Instancio.create(generator.getUserModel());

        var createResponse = mockMvc.perform(post("/api/users").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andReturn();

        User createdUser = objectMapper.readValue(createResponse.getResponse().getContentAsString(), User.class);

        mockMvc.perform(delete("/api/users/" + createdUser.getId()).with(token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/" + createdUser.getId()).with(token))
                .andExpect(status().is4xxClientError());
    }
}
