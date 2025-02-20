package io.leedsk1y.taskmanagerx_backend.controllers;

import io.leedsk1y.taskmanagerx_backend.dto.TaskResponseDTO;
import io.leedsk1y.taskmanagerx_backend.models.ETaskStatus;
import io.leedsk1y.taskmanagerx_backend.models.Task;
import io.leedsk1y.taskmanagerx_backend.services.TaskService;
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
    public ResponseEntity<List<TaskResponseDTO>> getUserTasks() {
        return ResponseEntity.ok(taskService.getTasksForAuthenticatedUser());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getTaskByIdForAuthenticatedUser(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskService.createTask(task));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable UUID id, @RequestBody Task updatedTask) {
        return ResponseEntity.ok(taskService.updateTaskForAuthenticatedUser(id, updatedTask));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/status")
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(@PathVariable UUID id, @RequestBody Map<String, String> statusUpdate) {
        return ResponseEntity.ok(taskService.updateTaskStatusForAuthenticatedUser(id, statusUpdate.get("status")));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable UUID id) {
        taskService.deleteTaskForAuthenticatedUserOrAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/filter")
    public ResponseEntity<List<TaskResponseDTO>> filterTasks(@RequestParam(required = false) ETaskStatus status,
                                                             @RequestParam(required = false) LocalDateTime dueDateBefore,
                                                             @RequestParam(required = false) LocalDateTime dueDateAfter) {
        return ResponseEntity.ok(taskService.filterTasksForAuthenticatedUser(status, dueDateBefore, dueDateAfter));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/sort")
    public ResponseEntity<List<TaskResponseDTO>> sortTasks(@RequestParam(defaultValue = "asc") String order) {
        return ResponseEntity.ok(taskService.sortTasksForAuthenticatedUser(order));
    }
}