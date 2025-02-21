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

    /**
     * Retrieves all tasks.
     * @return List of all tasks as TaskResponseDTO.
     */
    @GetMapping("/tasks")
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks() {
        return ResponseEntity.ok(adminService.getAllTasks());
    }

    /**
     * Retrieves a task by its ID.
     * @param id Task UUID.
     * @return Task details as TaskResponseDTO.
     */
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskByIdForAdmin(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.getTaskByIdForAdmin(id));
    }

    /**
     * Creates a new task.
     * @param taskRequest Task details in DTO format.
     * @return The created task as TaskResponseDTO.
     */
    @PostMapping("/tasks")
    public ResponseEntity<TaskResponseDTO> createTaskByAdmin(@RequestBody CreateTaskRequestDTO taskRequest) {
        return ResponseEntity.ok(adminService.createTaskByAdmin(taskRequest));
    }

    /**
     * Updates an existing task by ID.
     * @param id Task UUID.
     * @param updatedTask Updated task details.
     * @return Updated task as TaskResponseDTO.
     */
    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDTO> updateTaskByAdmin(@PathVariable UUID id, @RequestBody Task updatedTask) {
        return ResponseEntity.ok(adminService.updateTaskByAdmin(id, updatedTask));
    }

    /**
     * Reassigns a task to a different user.
     * @param id Task UUID.
     * @param requestData Request body containing new user ID.
     * @return Updated task as TaskResponseDTO.
     */
    @PutMapping("/tasks/{id}/reassign")
    public ResponseEntity<TaskResponseDTO> reassignTask(
            @PathVariable UUID id,
            @RequestBody Map<String, UUID> requestData) {

        UUID newUserId = requestData.get("userId");
        return ResponseEntity.ok(adminService.reassignTask(id, newUserId));
    }

    /**
     * Deletes a task by ID.
     * @param id Task UUID.
     * @return No content response.
     */
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable UUID id) {
        adminService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Filters tasks based on parameters.
     * @param userEmail User email to filter by.
     * @param status Task status to filter by.
     * @param dueDateBefore Filter tasks due before this date.
     * @param dueDateAfter Filter tasks due after this date.
     * @return List of filtered tasks as TaskResponseDTO.
     */
    @GetMapping("/tasks/filter")
    public ResponseEntity<List<TaskResponseDTO>> filterTasks(@RequestParam(required = false) String userEmail,
                                                             @RequestParam(required = false) ETaskStatus status,
                                                             @RequestParam(required = false) LocalDateTime dueDateBefore,
                                                             @RequestParam(required = false) LocalDateTime dueDateAfter) {
        return ResponseEntity.ok(adminService.filterTasksForAdmin(userEmail, status, dueDateBefore, dueDateAfter));
    }

    /**
     * Sorts tasks based on given criteria.
     * @param sortBy Sorting parameter (e.g., "dueDate").
     * @param order Sorting order ("asc" or "desc").
     * @return List of sorted tasks as TaskResponseDTO.
     */
    @GetMapping("/tasks/sort")
    public ResponseEntity<List<TaskResponseDTO>> sortTasks(@RequestParam(required = false) String sortBy,
                                                           @RequestParam(defaultValue = "asc") String order) {
        return ResponseEntity.ok(adminService.sortTasksForAdmin(sortBy, order));
    }

    /**
     * Retrieves all users.
     * @return List of all users as UserDetailedResponseDTO.
     */
    @GetMapping("users")
    public ResponseEntity<List<UserDetailedResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * Retrieves user details by ID.
     * @param id User UUID.
     * @return User details as UserDetailedResponseDTO.
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDetailedResponseDTO> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    /**
     * Deletes a user by ID.
     * @param id User UUID.
     * @return No content response.
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        adminService.deleteUserByAdmin(id);
        return ResponseEntity.noContent().build();
    }
}