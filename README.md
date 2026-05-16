# Eulerity Task Manager API

This is a Java 17 Spring Boot take home project for a personal task manager API. The app supports full CRUD operations for tasks, a simple browser UI, an H2 in memory database, and one AI powered endpoint that uses the OpenAI API to turn a plain language task description into a structured task draft.

The goal of this project was to keep the implementation simple, easy to run, and easy to review.

## Tech Stack

Java 17  
Spring Boot 4.0.6  
Maven  
Spring Web  
Spring Data JPA  
Spring Validation  
H2 in memory database  
OpenAI API  
JUnit, Mockito, and MockMvc for testing  
Plain HTML, CSS, and JavaScript for the frontend

## Requirements

Before running the project, make sure you have:

Java 17 installed  
Internet access  
An OpenAI API key if you want to use the AI powered endpoint

The normal CRUD endpoints and browser UI can still run without an OpenAI API key. The AI endpoint will return a readable error if `OPENAI_API_KEY` is not configured.

## How to Run the App

From the project root, run:

```bash
./mvnw spring-boot:run
```

Then open the browser UI at:

```text
http://localhost:8080/
```

The app uses an H2 in memory database, so task data resets when the application stops.

## How to Run Tests

From the project root, run:

```bash
./mvnw test
```

The test suite includes service layer unit tests, CRUD integration tests, and an AI endpoint test with the external OpenAI call mocked.

## OpenAI API Key Setup

The AI powered endpoint reads the API key from the `OPENAI_API_KEY` environment variable.

On macOS or Linux, you can run:

```bash
export OPENAI_API_KEY="your_api_key_here"
./mvnw spring-boot:run
```

Or in one command:

```bash
OPENAI_API_KEY="your_api_key_here" ./mvnw spring-boot:run
```

Do not commit your API key. If you use a local `.env` file, keep it out of git.

## Task Model

A task includes:

```json
{
  "id": 1,
  "title": "Finish Eulerity project",
  "description": "Build CRUD API and AI suggestion endpoint",
  "dueDate": "2026-05-22",
  "priority": "HIGH",
  "status": "TODO"
}
```

Priority values:

```text
LOW
MEDIUM
HIGH
```

Status values:

```text
TODO
IN_PROGRESS
DONE
```

## CRUD Endpoints

### Create a Task

```http
POST /tasks
```

Example request:

```json
{
  "title": "Finish Eulerity project",
  "description": "Build CRUD API and AI suggestion endpoint",
  "dueDate": "2026-05-22",
  "priority": "HIGH",
  "status": "TODO"
}
```

Example response:

```json
{
  "id": 1,
  "title": "Finish Eulerity project",
  "description": "Build CRUD API and AI suggestion endpoint",
  "dueDate": "2026-05-22",
  "priority": "HIGH",
  "status": "TODO"
}
```

Successful creates return `201 Created`.

### List All Tasks

```http
GET /tasks
```

Example response:

```json
[
  {
    "id": 1,
    "title": "Finish Eulerity project",
    "description": "Build CRUD API and AI suggestion endpoint",
    "dueDate": "2026-05-22",
    "priority": "HIGH",
    "status": "TODO"
  }
]
```

### Get One Task

```http
GET /tasks/{id}
```

Example:

```http
GET /tasks/1
```

If the task exists, the API returns `200 OK`.

If the task does not exist, the API returns `404 Not Found`.

### Update a Task

```http
PUT /tasks/{id}
```

Example request:

```json
{
  "title": "Finish Eulerity task manager",
  "description": "Complete CRUD flow and AI suggestion endpoint",
  "dueDate": "2026-05-25",
  "priority": "MEDIUM",
  "status": "IN_PROGRESS"
}
```

Successful updates return `200 OK`.

### Delete a Task

```http
DELETE /tasks/{id}
```

Successful deletes return `204 No Content`.

## AI Powered Endpoint

The AI powered endpoint turns a plain language task description into a structured task draft.

```http
POST /tasks/suggest
```

Example request:

```json
{
  "description": "remind me to submit the quarterly report before Friday"
}
```

Example response:

```json
{
  "title": "Submit Quarterly Report",
  "description": "Submit the quarterly report before Friday.",
  "dueDate": "2026-05-22",
  "priority": "HIGH",
  "status": "TODO"
}
```

This endpoint is stateless. It does not save the suggested task to the database. A user can review the suggestion first, then create the task separately with `POST /tasks`.

If `OPENAI_API_KEY` is not configured, the endpoint returns a readable JSON error response.

Example:

```json
{
  "error": "OPENAI_API_KEY is not configured"
}
```

## Browser UI

The project includes a simple frontend at:

```text
http://localhost:8080/
```

The UI supports:

Viewing all tasks  
Creating a task  
Editing a task  
Deleting a task  
Generating an AI task suggestion  

The UI is intentionally minimal and uses plain HTML, CSS, and JavaScript.

## Validation and Error Handling

The API validates required fields such as task title, priority, status, and AI suggestion description.

Examples:

A blank task title returns `400 Bad Request`.

A missing task ID returns `404 Not Found`.

An AI suggestion failure returns a simple JSON error response.

## Project Structure

```text
src/main/java/com/eulerity/taskmanager
  TaskManagerApplication.java

  task
    Task.java
    TaskPriority.java
    TaskStatus.java
    TaskRepository.java
    TaskService.java
    TaskController.java
    dto
      CreateTaskRequest.java
      UpdateTaskRequest.java
      TaskResponse.java
      SuggestTaskRequest.java
      SuggestedTaskResponse.java

  ai
    TaskAiClient.java
    OpenAiTaskClient.java
    AiTaskSuggestionService.java
    AiTaskSuggestionException.java

  common
    ApiExceptionHandler.java
    NotFoundException.java

src/main/resources
  application.properties
  static/index.html
```

## Design Notes

The project uses a simple layered structure:

```text
Controller to Service to Repository to H2 Database
Controller to AI Service to AI Client to OpenAI API
```

The OpenAI integration is behind the `TaskAiClient` interface so tests can mock the external model call. This keeps the AI endpoint testable without making real network requests.

DTOs are used for request and response objects instead of exposing the JPA entity directly. This keeps the API contract separate from the database model and gives validation a clear place to live.

The frontend is intentionally basic because the assignment focuses on backend behavior and AI assisted development workflow, not frontend styling.

## Submission Notes

Do not commit:

API keys  
`.env` files  
Maven build output such as `target/`  
IDE specific files