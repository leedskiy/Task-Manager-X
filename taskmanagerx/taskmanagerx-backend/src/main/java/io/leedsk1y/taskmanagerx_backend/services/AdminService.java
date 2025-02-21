package io.leedsk1y.taskmanagerx_backend.services;

import io.leedsk1y.taskmanagerx_backend.dto.CreateTaskRequestDTO;
import io.leedsk1y.taskmanagerx_backend.dto.TaskResponseDTO;
import io.leedsk1y.taskmanagerx_backend.dto.UserDetailedResponseDTO;
import io.leedsk1y.taskmanagerx_backend.models.ERole;
import io.leedsk1y.taskmanagerx_backend.models.ETaskStatus;
import io.leedsk1y.taskmanagerx_backend.models.Task;
import io.leedsk1y.taskmanagerx_backend.models.User;
import io.leedsk1y.taskmanagerx_backend.repositories.TaskRepository;
import io.leedsk1y.taskmanagerx_backend.repositories.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public AdminService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all tasks.
     * @return List of all tasks as TaskResponseDTO.
     */
    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(task -> new TaskResponseDTO(task, true))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a task by its ID.
     * @param taskId Task UUID.
     * @return Task details as TaskResponseDTO.
     */
    public TaskResponseDTO getTaskByIdForAdmin(UUID taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        return new TaskResponseDTO(task, true);
    }

    /**
     * Creates a new task.
     * @param taskRequest Task details in DTO format.
     * @return The created task as TaskResponseDTO.
     */
    public TaskResponseDTO createTaskByAdmin(CreateTaskRequestDTO taskRequest) {
        User user = userRepository.findById(taskRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task newTask = new Task();
        newTask.setTitle(taskRequest.getTitle());
        newTask.setDescription(taskRequest.getDescription());
        newTask.setDueDate(taskRequest.getDueDate());
        newTask.setUser(user);
        newTask.setStatus(ETaskStatus.PENDING);
        newTask.setCreatedAt(LocalDateTime.now());

        return new TaskResponseDTO(taskRepository.save(newTask), true);
    }

    /**
     * Updates an existing task by ID.
     * @param taskId Task UUID.
     * @param updatedTask Updated task details.
     * @return Updated task as TaskResponseDTO.
     */
    public TaskResponseDTO updateTaskByAdmin(UUID taskId, Task updatedTask) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setStatus(updatedTask.getStatus());
        task.setDueDate(updatedTask.getDueDate());

        return new TaskResponseDTO(taskRepository.save(task), true);
    }

    /**
     * Reassigns a task to a different user.
     * @param taskId Task UUID.
     * @param newUserId New user UUID.
     * @return Updated task as TaskResponseDTO.
     */
    public TaskResponseDTO reassignTask(UUID taskId, UUID newUserId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User newUser = userRepository.findById(newUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        task.setUser(newUser);

        return new TaskResponseDTO(taskRepository.save(task), true);
    }

    /**
     * Deletes a task by ID.
     * @param taskId Task UUID.
     */
    public void deleteTask(UUID taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);
    }

    /**
     * Filters tasks based on parameters.
     * @param userEmail User email to filter by.
     * @param status Task status to filter by.
     * @param dueDateBefore Filter tasks due before this date.
     * @param dueDateAfter Filter tasks due after this date.
     * @return List of filtered tasks as TaskResponseDTO.
     */
    public List<TaskResponseDTO> filterTasksForAdmin(String userEmail, ETaskStatus status, LocalDateTime dueDateBefore, LocalDateTime dueDateAfter) {
        UUID userId = null;

        if (userEmail != null && !userEmail.isEmpty()) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            userId = user.getId();
        }

        List<Task> tasks = taskRepository.findTasksByFiltersForAdminAndUser(userId, status, dueDateBefore, dueDateAfter);

        return tasks.stream().map(task -> new TaskResponseDTO(task, true)).collect(Collectors.toList());
    }

    /**
     * Sorts tasks based on given criteria.
     * @param sortBy Sorting parameter (e.g., "dueDate").
     * @param order Sorting order ("asc" or "desc").
     * @return List of sorted tasks as TaskResponseDTO.
     */
    public List<TaskResponseDTO> sortTasksForAdmin(String sortBy, String order) {
        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort;

        if ("userEmail".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(direction, "user.email");
        } else {
            sort = Sort.by(direction, "dueDate");
        }

        return taskRepository.findAll(sort).stream()
                .map(task -> new TaskResponseDTO(task, true))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all users.
     * @return List of all users as UserDetailedResponseDTO.
     */
    public List<UserDetailedResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDetailedResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves user details by ID.
     * @param userId User UUID.
     * @return User details as UserDetailedResponseDTO.
     */
    public UserDetailedResponseDTO getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDetailedResponseDTO(user);
    }

    /**
     * Deletes a user by ID.
     * @param userId User UUID.
     */
    public void deleteUserByAdmin(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(ERole.ROLE_ADMIN));

        if (isAdmin) {
            throw new RuntimeException("Cannot delete an admin user.");
        }

        taskRepository.deleteAll(taskRepository.findTasksByUserId(userId));

        user.getRoles().clear();
        userRepository.save(user);

        userRepository.delete(user);
    }
}