package com.eulerity.taskmanager.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eulerity.taskmanager.task.dto.CreateTaskRequest;
import com.eulerity.taskmanager.task.dto.TaskResponse;
import com.eulerity.taskmanager.task.dto.UpdateTaskRequest;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository);
    }

    @Test
    void createTaskSavesTaskAndReturnsResponse() {
        CreateTaskRequest request = new CreateTaskRequest(
                "Finish project",
                "Complete the task manager",
                LocalDate.of(2026, 5, 22),
                TaskPriority.HIGH,
                TaskStatus.TODO
        );
        Task savedTask = taskWithId(
                1L,
                "Finish project",
                "Complete the task manager",
                LocalDate.of(2026, 5, 22),
                TaskPriority.HIGH,
                TaskStatus.TODO
        );

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskResponse response = taskService.createTask(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Finish project");
        assertThat(response.description()).isEqualTo("Complete the task manager");
        assertThat(response.dueDate()).isEqualTo(LocalDate.of(2026, 5, 22));
        assertThat(response.priority()).isEqualTo(TaskPriority.HIGH);
        assertThat(response.status()).isEqualTo(TaskStatus.TODO);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getTitle()).isEqualTo("Finish project");
    }

    @Test
    void getAllTasksReturnsResponsesForAllTasks() {
        Task firstTask = taskWithId(
                1L,
                "First task",
                "First description",
                LocalDate.of(2026, 5, 22),
                TaskPriority.LOW,
                TaskStatus.TODO
        );
        Task secondTask = taskWithId(
                2L,
                "Second task",
                "Second description",
                LocalDate.of(2026, 5, 23),
                TaskPriority.MEDIUM,
                TaskStatus.IN_PROGRESS
        );

        when(taskRepository.findAll()).thenReturn(List.of(firstTask, secondTask));

        List<TaskResponse> responses = taskService.getAllTasks();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).id()).isEqualTo(1L);
        assertThat(responses.get(0).title()).isEqualTo("First task");
        assertThat(responses.get(1).id()).isEqualTo(2L);
        assertThat(responses.get(1).title()).isEqualTo("Second task");
    }

    @Test
    void getTaskByIdReturnsMatchingTaskResponse() {
        Task task = taskWithId(
                1L,
                "Find task",
                "Find this task",
                LocalDate.of(2026, 5, 22),
                TaskPriority.MEDIUM,
                TaskStatus.TODO
        );

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.getTaskById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Find task");
        assertThat(response.description()).isEqualTo("Find this task");
        assertThat(response.dueDate()).isEqualTo(LocalDate.of(2026, 5, 22));
        assertThat(response.priority()).isEqualTo(TaskPriority.MEDIUM);
        assertThat(response.status()).isEqualTo(TaskStatus.TODO);
    }

    @Test
    void updateTaskUpdatesExistingTaskAndReturnsResponse() {
        Task existingTask = taskWithId(
                1L,
                "Old title",
                "Old description",
                LocalDate.of(2026, 5, 20),
                TaskPriority.LOW,
                TaskStatus.TODO
        );
        UpdateTaskRequest request = new UpdateTaskRequest(
                "Updated title",
                "Updated description",
                LocalDate.of(2026, 5, 25),
                TaskPriority.HIGH,
                TaskStatus.IN_PROGRESS
        );

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        TaskResponse response = taskService.updateTask(1L, request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Updated title");
        assertThat(response.description()).isEqualTo("Updated description");
        assertThat(response.dueDate()).isEqualTo(LocalDate.of(2026, 5, 25));
        assertThat(response.priority()).isEqualTo(TaskPriority.HIGH);
        assertThat(response.status()).isEqualTo(TaskStatus.IN_PROGRESS);
        verify(taskRepository).save(existingTask);
    }

    @Test
    void deleteTaskDeletesExistingTask() {
        Task task = taskWithId(
                1L,
                "Delete task",
                "Delete this task",
                LocalDate.of(2026, 5, 22),
                TaskPriority.LOW,
                TaskStatus.DONE
        );

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteTask(1L);

        verify(taskRepository).delete(task);
    }

    private static Task taskWithId(Long id,
                                   String title,
                                   String description,
                                   LocalDate dueDate,
                                   TaskPriority priority,
                                   TaskStatus status) {
        Task task = new Task(title, description, dueDate, priority, status);
        setId(task, id);
        return task;
    }

    private static void setId(Task task, Long id) {
        try {
            Field idField = Task.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(task, id);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new IllegalStateException("Unable to set task id for test", exception);
        }
    }
}
