package io.leedsk1y.taskmanagerx_backend.controllers;

import io.leedsk1y.taskmanagerx_backend.dto.TaskResponseDTO;
import io.leedsk1y.taskmanagerx_backend.models.ETaskStatus;
import io.leedsk1y.taskmanagerx_backend.models.Task;
import io.leedsk1y.taskmanagerx_backend.services.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getUserTasks(@RequestParam(required = false) String expand) {
        List<Task> tasks = taskService.getTasksForAuthenticatedUser();
        boolean includeUser = "user".equals(expand);

        List<TaskResponseDTO> responseDTOs = tasks.stream()
                .map(task -> new TaskResponseDTO(task, includeUser))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable UUID id, @RequestParam(required = false) String expand) {
        Task task = taskService.getTaskByIdForAuthenticatedUser(id);
        boolean includeUser = "user".equals(expand);
        return ResponseEntity.ok(new TaskResponseDTO(task, includeUser));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @RequestBody Task task,
            @RequestParam(required = false) String expand) {

        Task createdTask = taskService.createTask(task);
        boolean includeUser = "user".equals(expand);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TaskResponseDTO(createdTask, includeUser));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable UUID id,
            @RequestBody Task updatedTask,
            @RequestParam(required = false) String expand) {

        Task task = taskService.updateTaskForAuthenticatedUser(id, updatedTask);
        boolean includeUser = "user".equals(expand);

        return ResponseEntity.ok(new TaskResponseDTO(task, includeUser));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/status")
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> statusUpdate,
            @RequestParam(required = false) String expand) {

        String status = statusUpdate.get("status");
        Task task = taskService.updateTaskStatusForAuthenticatedUser(id, status);
        boolean includeUser = "user".equals(expand);

        return ResponseEntity.ok(new TaskResponseDTO(task, includeUser));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable UUID id) {
        taskService.deleteTaskForAuthenticatedUserOrAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/filter")
    public ResponseEntity<List<TaskResponseDTO>> filterTasks(
            @RequestParam(required = false) ETaskStatus status,
            @RequestParam(required = false) LocalDateTime dueDateBefore,
            @RequestParam(required = false) LocalDateTime dueDateAfter,
            @RequestParam(required = false) String expand) {

        List<Task> filteredTasks = taskService.filterTasksForAuthenticatedUser(status, dueDateBefore, dueDateAfter);
        boolean includeUser = "user".equals(expand);

        List<TaskResponseDTO> responseDTOs = filteredTasks.stream()
                .map(task -> new TaskResponseDTO(task, includeUser))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/sort")
    public ResponseEntity<List<TaskResponseDTO>> sortTasks(
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String expand) {

        List<Task> sortedTasks = taskService.sortTasksForAuthenticatedUser(order);
        boolean includeUser = "user".equals(expand);

        List<TaskResponseDTO> responseDTOs = sortedTasks.stream()
                .map(task -> new TaskResponseDTO(task, includeUser))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }
}
