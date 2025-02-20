package io.leedsk1y.taskmanagerx_backend.dto;

import io.leedsk1y.taskmanagerx_backend.models.User;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class UserDetailedResponseDTO {
    private final UUID id;
    private final String name;
    private final String email;
    private final String profileImage;
    private final Set<String> roles;
    private final String authProvider;

    public UserDetailedResponseDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.profileImage = user.getProfileImage();
        this.roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
        this.authProvider = user.getAuthProvider().name();
    }
}
