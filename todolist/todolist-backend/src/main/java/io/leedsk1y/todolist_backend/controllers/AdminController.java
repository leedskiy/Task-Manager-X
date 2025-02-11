package io.leedsk1y.todolist_backend.controllers;

import io.leedsk1y.todolist_backend.dto.TaskResponseDTO;
import io.leedsk1y.todolist_backend.dto.UserResponseDTO;
import io.leedsk1y.todolist_backend.models.Task;
import io.leedsk1y.todolist_backend.models.User;
import io.leedsk1y.todolist_backend.services.TaskService;
import io.leedsk1y.todolist_backend.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final TaskService taskService;
    private final UserService userService;

    public AdminController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/tasks")
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        List<TaskResponseDTO> responseDTOs = tasks.stream()
                .map(task -> new TaskResponseDTO(task, true))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable UUID id) {
        taskService.deleteTaskByAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponseDTO> responseDTOs = users.stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        try {
            userService.deleteUserByAdmin(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage(), "status", false));
        }
    }
}
