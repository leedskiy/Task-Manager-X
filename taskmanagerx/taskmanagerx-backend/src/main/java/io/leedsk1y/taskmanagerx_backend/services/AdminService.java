package io.leedsk1y.taskmanagerx_backend.services;

import io.leedsk1y.taskmanagerx_backend.dto.TaskResponseDTO;
import io.leedsk1y.taskmanagerx_backend.dto.UserDetailedResponseDTO;
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

    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(task -> new TaskResponseDTO(task, true))
                .collect(Collectors.toList());
    }

    public TaskResponseDTO getTaskByIdForAdmin(UUID taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        return new TaskResponseDTO(task, true);
    }

    public TaskResponseDTO updateTaskByAdmin(UUID taskId, Task updatedTask) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setStatus(updatedTask.getStatus());
        task.setDueDate(updatedTask.getDueDate());

        return new TaskResponseDTO(taskRepository.save(task), true);
    }

    public void deleteTask(UUID taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);
    }

    public List<TaskResponseDTO> filterTasksForAdmin(ETaskStatus status, LocalDateTime dueDateBefore, LocalDateTime dueDateAfter) {
        return taskRepository.findTasksByFiltersForAdmin(status, dueDateBefore, dueDateAfter)
                .stream()
                .map(task -> new TaskResponseDTO(task, true))
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> sortTasksForAdmin(String order) {
        Sort sort = order.equalsIgnoreCase("desc") ?
                Sort.by(Sort.Direction.DESC, "dueDate") :
                Sort.by(Sort.Direction.ASC, "dueDate");

        return taskRepository.findAll(sort).stream()
                .map(task -> new TaskResponseDTO(task, true))
                .collect(Collectors.toList());
    }

    public List<UserDetailedResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDetailedResponseDTO::new)
                .collect(Collectors.toList());
    }

    public UserDetailedResponseDTO getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDetailedResponseDTO(user);
    }

    public void deleteUserByAdmin(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }
}