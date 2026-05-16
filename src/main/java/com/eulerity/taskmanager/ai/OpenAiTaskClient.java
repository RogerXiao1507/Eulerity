package com.eulerity.taskmanager.ai;

import com.eulerity.taskmanager.task.dto.SuggestedTaskResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class OpenAiTaskClient implements TaskAiClient {

    private static final URI RESPONSES_API_URI = URI.create("https://api.openai.com/v1/responses");

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String model;

    public OpenAiTaskClient(ObjectMapper objectMapper,
                            @Value("${openai.model:gpt-4.1-mini}") String model) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
        this.model = model;
    }

    @Override
    public SuggestedTaskResponse suggestTask(String description) {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new AiTaskSuggestionException("OPENAI_API_KEY is not configured");
        }

        try {
            HttpRequest request = HttpRequest.newBuilder(RESPONSES_API_URI)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(buildRequestBody(description)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new AiTaskSuggestionException("OpenAI request failed");
            }

            return parseSuggestedTask(response.body());
        } catch (IOException exception) {
            throw new AiTaskSuggestionException("OpenAI request failed", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AiTaskSuggestionException("OpenAI request was interrupted", exception);
        }
    }

    private String buildRequestBody(String description) throws JacksonException {
        Map<String, Object> body = Map.of(
                "model", model,
                "instructions", """
                        You create task drafts from plain language reminders.
                        Return only structured JSON matching the provided schema.
                        Use ISO-8601 dates in yyyy-MM-dd format.
                        Use TODO as the default status unless the user clearly indicates progress or completion.
                        Today's date is %s.
                        """.formatted(LocalDate.now()),
                "input", "Create a task draft from this description: " + description,
                "text", Map.of(
                        "format", Map.of(
                                "type", "json_schema",
                                "name", "task_suggestion",
                                "strict", true,
                                "schema", taskSuggestionSchema()
                        )
                )
        );

        return objectMapper.writeValueAsString(body);
    }

    private Map<String, Object> taskSuggestionSchema() {
        return Map.of(
                "type", "object",
                "additionalProperties", false,
                "properties", Map.of(
                        "title", Map.of(
                                "type", "string",
                                "description", "A short task title"
                        ),
                        "description", Map.of(
                                "type", "string",
                                "description", "A concise task description"
                        ),
                        "dueDate", Map.of(
                                "anyOf", List.of(
                                        Map.of("type", "string"),
                                        Map.of("type", "null")
                                ),
                                "description", "The due date as yyyy-MM-dd, or null if no due date is implied"
                        ),
                        "priority", Map.of(
                                "type", "string",
                                "enum", List.of("LOW", "MEDIUM", "HIGH")
                        ),
                        "status", Map.of(
                                "type", "string",
                                "enum", List.of("TODO", "IN_PROGRESS", "DONE")
                        )
                ),
                "required", List.of("title", "description", "dueDate", "priority", "status")
        );
    }

    private SuggestedTaskResponse parseSuggestedTask(String responseBody) {
        try {
            String outputText = extractOutputText(responseBody);
            return objectMapper.readValue(outputText, SuggestedTaskResponse.class);
        } catch (JacksonException exception) {
            throw new AiTaskSuggestionException("Unable to parse AI response", exception);
        }
    }

    private String extractOutputText(String responseBody) throws JacksonException {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode error = root.path("error");
        if (!error.isMissingNode() && !error.isNull()) {
            throw new AiTaskSuggestionException("OpenAI request failed");
        }

        for (JsonNode outputItem : root.path("output")) {
            for (JsonNode contentItem : outputItem.path("content")) {
                if ("output_text".equals(contentItem.path("type").asText())) {
                    String text = contentItem.path("text").asText();
                    if (!text.isBlank()) {
                        return text;
                    }
                }
            }
        }

        throw new AiTaskSuggestionException("Unable to parse AI response");
    }
}
