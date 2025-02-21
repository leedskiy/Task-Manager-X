package io.leedsk1y.taskmanagerx_backend.controllers;

import io.leedsk1y.taskmanagerx_backend.dto.CreateTaskRequestDTO;
import io.leedsk1y.taskmanagerx_backend.dto.TaskResponseDTO;
import io.leedsk1y.taskmanagerx_backend.dto.UserDetailedResponseDTO;
import io.leedsk1y.taskmanagerx_backend.models.ETaskStatus;
import io.leedsk1y.taskmanagerx_backend.models.Task;
import io.leedsk1y.taskmanagerx_backend.services.AdminService;
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
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks() {
        return ResponseEntity.ok(adminService.getAllTasks());
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskByIdForAdmin(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.getTaskByIdForAdmin(id));
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponseDTO> createTaskByAdmin(@RequestBody CreateTaskRequestDTO taskRequest) {
        return ResponseEntity.ok(adminService.createTaskByAdmin(taskRequest));
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDTO> updateTaskByAdmin(@PathVariable UUID id, @RequestBody Task updatedTask) {
        return ResponseEntity.ok(adminService.updateTaskByAdmin(id, updatedTask));
    }

    @PutMapping("/tasks/{id}/reassign")
    public ResponseEntity<TaskResponseDTO> reassignTask(
            @PathVariable UUID id,
            @RequestBody Map<String, UUID> requestData) {

        UUID newUserId = requestData.get("userId");
        return ResponseEntity.ok(adminService.reassignTask(id, newUserId));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable UUID id) {
        adminService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tasks/filter")
    public ResponseEntity<List<TaskResponseDTO>> filterTasks(@RequestParam(required = false) String userEmail,
                                                             @RequestParam(required = false) ETaskStatus status,
                                                             @RequestParam(required = false) LocalDateTime dueDateBefore,
                                                             @RequestParam(required = false) LocalDateTime dueDateAfter) {
        return ResponseEntity.ok(adminService.filterTasksForAdmin(userEmail, status, dueDateBefore, dueDateAfter));
    }

    @GetMapping("/tasks/sort")
    public ResponseEntity<List<TaskResponseDTO>> sortTasks(@RequestParam(required = false) String sortBy,
                                                           @RequestParam(defaultValue = "asc") String order) {
        return ResponseEntity.ok(adminService.sortTasksForAdmin(sortBy, order));
    }

    @GetMapping("users")
    public ResponseEntity<List<UserDetailedResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDetailedResponseDTO> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        adminService.deleteUserByAdmin(id);
        return ResponseEntity.noContent().build();
    }
}