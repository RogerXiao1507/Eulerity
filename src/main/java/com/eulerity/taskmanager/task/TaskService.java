package com.eulerity.taskmanager.task;

import com.eulerity.taskmanager.common.NotFoundException;
import com.eulerity.taskmanager.task.dto.CreateTaskRequest;
import com.eulerity.taskmanager.task.dto.TaskResponse;
import com.eulerity.taskmanager.task.dto.UpdateTaskRequest;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponse createTask(CreateTaskRequest request) {
        Task task = new Task(
                request.title(),
                request.description(),
                request.dueDate(),
                request.priority(),
                request.status()
        );

        return toResponse(taskRepository.save(task));
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TaskResponse getTaskById(Long id) {
        return toResponse(findTaskById(id));
    }

    public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
        Task task = findTaskById(id);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        task.setPriority(request.priority());
        task.setStatus(request.status());

        return toResponse(taskRepository.save(task));
    }

    public void deleteTask(Long id) {
        Task task = findTaskById(id);
        taskRepository.delete(task);
    }

    private Task findTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + id));
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getPriority(),
                task.getStatus()
        );
    }
}
