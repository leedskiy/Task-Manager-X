package io.leedsk1y.taskmanagerx_backend.services;

import io.leedsk1y.taskmanagerx_backend.dto.UserDetailedResponseDTO;
import io.leedsk1y.taskmanagerx_backend.models.User;
import io.leedsk1y.taskmanagerx_backend.repositories.TaskRepository;
import io.leedsk1y.taskmanagerx_backend.repositories.UserRepository;
import io.leedsk1y.taskmanagerx_backend.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final JwtUtils jwtUtils;

    public UserService(UserRepository userRepository, TaskRepository taskRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Retrieves the currently authenticated user.
     * @return UserDetailedResponseDTO containing user details.
     */
    public UserDetailedResponseDTO getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDetailedResponseDTO(user);
    }

    /**
     * Updates the authenticated user's profile name.
     * @param name New name to be updated.
     * @return Updated user details as UserDetailedResponseDTO.
     */
    public UserDetailedResponseDTO updateUserProfileName(String name) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(name);
        userRepository.save(user);
        return new UserDetailedResponseDTO(user);
    }

    /**
     * Deletes the authenticated user's account, logs them out, and clears associated tasks.
     * @param request HTTP request containing authentication details.
     * @param response HTTP response to process logout.
     */
    public void deleteAuthenticatedUser(HttpServletRequest request, HttpServletResponse response) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtils.getJwtFromHeader(request);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        SecurityContextHolder.clearContext();

        if (token != null) {
            jwtUtils.blacklistToken(token);
        }

        taskRepository.deleteAll(taskRepository.findTasksByUserId(user.getId()));

        user.getRoles().clear();
        userRepository.save(user);
        userRepository.delete(user);
    }
}
