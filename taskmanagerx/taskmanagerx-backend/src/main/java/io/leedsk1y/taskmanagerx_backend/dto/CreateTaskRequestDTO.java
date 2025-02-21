package io.leedsk1y.taskmanagerx_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateTaskRequestDTO {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private UUID userId;
}