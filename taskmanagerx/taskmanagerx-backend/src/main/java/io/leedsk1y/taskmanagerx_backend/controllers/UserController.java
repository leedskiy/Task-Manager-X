package io.leedsk1y.taskmanagerx_backend.controllers;

import io.leedsk1y.taskmanagerx_backend.dto.UserDetailedResponseDTO;
import io.leedsk1y.taskmanagerx_backend.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves the currently authenticated user's details.
     * @return ResponseEntity containing user details.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDetailedResponseDTO> getAuthenticatedUser() {
        return ResponseEntity.ok(userService.getAuthenticatedUser());
    }

    /**
     * Updates the authenticated user's profile name.
     * @param updateData Map containing the new name.
     * @return ResponseEntity containing updated user details.
     */
    @PutMapping("/me/name")
    public ResponseEntity<UserDetailedResponseDTO> updateProfileName(@RequestBody Map<String, String> updateData) {
        return ResponseEntity.ok(userService.updateUserProfileName(updateData.get("name")));
    }

    /**
     * Deletes the authenticated user's account and logs them out.
     * @param request HTTP request containing authentication details.
     * @param response HTTP response to process logout.
     * @return ResponseEntity with no content.
     */
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteAuthenticatedUser(HttpServletRequest request, HttpServletResponse response) {
        userService.deleteAuthenticatedUser(request, response);
        return ResponseEntity.noContent().build();
    }
}