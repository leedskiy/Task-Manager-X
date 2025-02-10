package io.leedsk1y.todolist_backend.controllers;

import io.leedsk1y.todolist_backend.dto.TaskResponseDTO;
import io.leedsk1y.todolist_backend.models.Task;
import io.leedsk1y.todolist_backend.services.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @RequestBody Task task,
            @RequestParam(required = false) String expand) {

        Task createdTask = taskService.createTask(task);
        boolean includeUser = "user".equals(expand);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TaskResponseDTO(createdTask, includeUser));
    }
}
