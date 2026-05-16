package com.eulerity.taskmanager.task;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eulerity.taskmanager.ai.TaskAiClient;
import com.eulerity.taskmanager.task.dto.SuggestedTaskResponse;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TaskSuggestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskAiClient taskAiClient;

    @Test
    void suggestTaskReturnsStructuredTaskSuggestion() throws Exception {
        when(taskAiClient.suggestTask("remind me to submit the quarterly report before Friday"))
                .thenReturn(new SuggestedTaskResponse(
                        "Submit quarterly report",
                        "Submit the quarterly report before Friday.",
                        LocalDate.of(2026, 5, 22),
                        TaskPriority.MEDIUM,
                        TaskStatus.TODO
                ));

        mockMvc.perform(post("/tasks/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "remind me to submit the quarterly report before Friday"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Submit quarterly report"))
                .andExpect(jsonPath("$.description").value("Submit the quarterly report before Friday."))
                .andExpect(jsonPath("$.dueDate").value("2026-05-22"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    void suggestTaskWithBlankDescriptionReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/tasks/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("description must not be blank"));
    }
}
