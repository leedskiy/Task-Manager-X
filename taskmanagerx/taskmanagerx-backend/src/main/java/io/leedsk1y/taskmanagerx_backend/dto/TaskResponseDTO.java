package io.leedsk1y.taskmanagerx_backend.dto;

import io.leedsk1y.taskmanagerx_backend.models.ETaskStatus;
import io.leedsk1y.taskmanagerx_backend.models.Task;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class TaskResponseDTO {
    private final UUID id;
    private final String title;
    private final String description;
    private final ETaskStatus status;
    private final LocalDateTime dueDate;
    private final UUID userId;
    private final UserBasicDTO user; // included only if requested

    public TaskResponseDTO(Task task, boolean includeUser) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.dueDate = task.getDueDate();
        this.userId = task.getUser().getId();
        this.user = includeUser ? new UserBasicDTO(task.getUser()) : null;
    }
}
