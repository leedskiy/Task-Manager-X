package io.leedsk1y.todolist_backend.services;

import io.leedsk1y.todolist_backend.models.ETaskStatus;
import io.leedsk1y.todolist_backend.models.Task;
import io.leedsk1y.todolist_backend.models.User;
import io.leedsk1y.todolist_backend.repositories.TaskRepository;
import io.leedsk1y.todolist_backend.repositories.UserRepository;
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

    public void deleteTaskForAuthenticatedUserOrAdmin(UUID taskId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        boolean isOwner = task.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Access denied: You can only delete your own tasks or must be an admin.");
        }

        taskRepository.delete(task);
    }
}
