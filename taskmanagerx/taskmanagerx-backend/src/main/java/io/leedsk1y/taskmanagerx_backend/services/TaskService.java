package io.leedsk1y.taskmanagerx_backend.services;

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

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<Task> getTasksForAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .map(user -> taskRepository.findByUserId(user.getId()))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Task getTaskByIdForAuthenticatedUser(UUID taskId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findById(taskId)
                .filter(task -> task.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));
    }

    public Task createTask(Task task) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        task.setUser(user);
        task.setStatus(ETaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());

        return taskRepository.save(task);
    }

    public Task updateTaskForAuthenticatedUser(UUID taskId, Task updatedTask) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task existingTask = taskRepository.findById(taskId)
                .filter(task -> task.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setDueDate(updatedTask.getDueDate());

        return taskRepository.save(existingTask);
    }

    public Task updateTaskStatusForAuthenticatedUser(UUID taskId, String status) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task existingTask = taskRepository.findById(taskId)
                .filter(task -> task.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));

        existingTask.setStatus(ETaskStatus.valueOf(status));
        return taskRepository.save(existingTask);
    }

    public void deleteTaskForAuthenticatedUserOrAdmin(UUID taskId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        boolean isOwner = task.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRoles().stream()
                .map(role -> role.getName().name())
                .anyMatch(name -> name.equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Access denied: You can only delete your own tasks or must be an admin.");
        }

        taskRepository.delete(task);
    }

    public List<Task> filterTasksForAuthenticatedUser(ETaskStatus status, LocalDateTime dueDateBefore, LocalDateTime dueDateAfter) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findTasksByFilters(user.getId(), status, dueDateBefore, dueDateAfter);
    }

    public List<Task> sortTasksForAuthenticatedUser(String order) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Sort sort = order.equalsIgnoreCase("desc") ?
                Sort.by(Sort.Direction.DESC, "dueDate")
                : Sort.by(Sort.Direction.ASC, "dueDate");

        return taskRepository.findByUserId(user.getId(), sort);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public void deleteTaskByAdmin(UUID taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        taskRepository.delete(task);
    }
}
