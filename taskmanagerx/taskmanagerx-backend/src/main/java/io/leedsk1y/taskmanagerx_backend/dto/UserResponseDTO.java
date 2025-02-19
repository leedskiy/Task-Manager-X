package io.leedsk1y.taskmanagerx_backend.dto;

import io.leedsk1y.taskmanagerx_backend.models.User;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserResponseDTO {
    private final UUID id;
    private final String name;
    private final String email;
    private final String profileImage;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.profileImage = user.getProfileImage();
    }
}
