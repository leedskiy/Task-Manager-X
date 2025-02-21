package io.leedsk1y.taskmanagerx_backend.services;

import io.leedsk1y.taskmanagerx_backend.dto.TaskResponseDTO;
import io.leedsk1y.taskmanagerx_backend.models.ETaskStatus;
import io.leedsk1y.taskmanagerx_backend.models.Task;
import io.leedsk1y.taskmanagerx_backend.models.User;
import io.leedsk1y.taskmanagerx_backend.repositories.TaskRepository;
import io.leedsk1y.taskmanagerx_backend.repositories.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves the currently authenticated user.
     * @return The authenticated User object.
     */
    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Retrieves all tasks for the authenticated user.
     * @return List of tasks as TaskResponseDTO.
     */
    public List<TaskResponseDTO> getTasksForAuthenticatedUser() {
        User user = getAuthenticatedUser();
        return taskRepository.findTasksByUserId(user.getId())
                .stream()
                .map(task -> new TaskResponseDTO(task, false))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific task by ID for the authenticated user.
     * @param taskId Task UUID.
     * @return TaskResponseDTO containing task details.
     */
    public TaskResponseDTO getTaskByIdForAuthenticatedUser(UUID taskId) {
        User user = getAuthenticatedUser();
        Task task = taskRepository.findById(taskId)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));
        return new TaskResponseDTO(task, false);
    }

    /**
     * Creates a new task for the authenticated user.
     * @param task Task details.
     * @return TaskResponseDTO containing the created task details.
     */
    public TaskResponseDTO createTask(Task task) {
        User user = getAuthenticatedUser();
        task.setUser(user);
        task.setStatus(ETaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        return new TaskResponseDTO(taskRepository.save(task), false);
    }

    /**
     * Updates an existing task for the authenticated user.
     * @param taskId Task UUID.
     * @param updatedTask Updated task details.
     * @return TaskResponseDTO containing the updated task details.
     */
    public TaskResponseDTO updateTaskForAuthenticatedUser(UUID taskId, Task updatedTask) {
        User user = getAuthenticatedUser();
        Task existingTask = taskRepository.findById(taskId)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setDueDate(updatedTask.getDueDate());

        return new TaskResponseDTO(taskRepository.save(existingTask), false);
    }

    /**
     * Updates the status of a task for the authenticated user.
     * @param taskId Task UUID.
     * @param status New task status.
     * @return TaskResponseDTO containing the updated task details.
     */
    public TaskResponseDTO updateTaskStatusForAuthenticatedUser(UUID taskId, String status) {
        User user = getAuthenticatedUser();
        Task existingTask = taskRepository.findById(taskId)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));

        existingTask.setStatus(ETaskStatus.valueOf(status));
        return new TaskResponseDTO(taskRepository.save(existingTask), false);
    }

    /**
     * Deletes a task for the authenticated user.
     * @param taskId Task UUID.
     */
    public void deleteTaskForAuthenticatedUser(UUID taskId) {
        User user = getAuthenticatedUser();
        Task task = taskRepository.findById(taskId)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));

        taskRepository.delete(task);
    }

    /**
     * Filters tasks for the authenticated user based on status and due date range.
     * @param status Task status.
     * @param dueDateBefore Filter tasks due before this date.
     * @param dueDateAfter Filter tasks due after this date.
     * @return List of filtered tasks as TaskResponseDTO.
     */
    public List<TaskResponseDTO> filterTasksForAuthenticatedUser(ETaskStatus status, LocalDateTime dueDateBefore, LocalDateTime dueDateAfter) {
        User user = getAuthenticatedUser();
        return taskRepository.findTasksByFilters(user.getId(), status, dueDateBefore, dueDateAfter)
                .stream()
                .map(task -> new TaskResponseDTO(task, false))
                .collect(Collectors.toList());
    }

    /**
     * Sorts tasks for the authenticated user based on due date.
     * @param order Sorting order ("asc" or "desc").
     * @return List of sorted tasks as TaskResponseDTO.
     */
    public List<TaskResponseDTO> sortTasksForAuthenticatedUser(String order) {
        User user = getAuthenticatedUser();
        Sort sort = order.equalsIgnoreCase("desc") ?
                Sort.by(Sort.Direction.DESC, "dueDate") :
                Sort.by(Sort.Direction.ASC, "dueDate");

        return taskRepository.findTasksByUserId(user.getId(), sort)
                .stream()
                .map(task -> new TaskResponseDTO(task, false))
                .collect(Collectors.toList());
    }
}