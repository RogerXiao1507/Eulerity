package com.eulerity.taskmanager.task;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crudEndpointsWorkEndToEnd() throws Exception {
        String createResponse = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Finish Eulerity project",
                                  "description": "Build CRUD API and AI suggestion endpoint",
                                  "dueDate": "2026-05-22",
                                  "priority": "HIGH",
                                  "status": "TODO"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/tasks/1"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Finish Eulerity project"))
                .andExpect(jsonPath("$.description").value("Build CRUD API and AI suggestion endpoint"))
                .andExpect(jsonPath("$.dueDate").value("2026-05-22"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long taskId = objectMapper.readValue(createResponse, JsonNode.class)
                .path("id")
                .longValue();

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(taskId))
                .andExpect(jsonPath("$[0].title").value("Finish Eulerity project"));

        mockMvc.perform(get("/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Finish Eulerity project"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("TODO"));

        mockMvc.perform(put("/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Finish Eulerity task manager",
                                  "description": "Complete CRUD flow before AI tests",
                                  "dueDate": "2026-05-25",
                                  "priority": "MEDIUM",
                                  "status": "IN_PROGRESS"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Finish Eulerity task manager"))
                .andExpect(jsonPath("$.description").value("Complete CRUD flow before AI tests"))
                .andExpect(jsonPath("$.dueDate").value("2026-05-25"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        mockMvc.perform(delete("/tasks/{id}", taskId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/tasks/{id}", taskId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Task not found with id: " + taskId));
    }
}
