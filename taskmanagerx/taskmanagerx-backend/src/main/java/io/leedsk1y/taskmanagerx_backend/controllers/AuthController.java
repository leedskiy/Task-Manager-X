package io.leedsk1y.taskmanagerx_backend.controllers;

import io.leedsk1y.taskmanagerx_backend.dto.LoginRequestDTO;
import io.leedsk1y.taskmanagerx_backend.dto.RegisterRequestDTO;
import io.leedsk1y.taskmanagerx_backend.dto.UserDetailedResponseDTO;
import io.leedsk1y.taskmanagerx_backend.security.jwt.JwtUtils;
import io.leedsk1y.taskmanagerx_backend.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtUtils jwtUtils;

    public AuthController(AuthService authService, JwtUtils jwtUtils) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Handles user registration.
     * @param request The registration request containing user details.
     * @return ResponseEntity with the registered user details or an error message.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        try {
            return ResponseEntity.ok(authService.registerUser(request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage(), "status", false));
        }
    }

    /**
     * Handles user login.
     * @param request The login request containing user email and password.
     * @return ResponseEntity with authentication token or an error message.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            return ResponseEntity.ok(authService.authenticateUser(request.getEmail(), request.getPassword()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Bad credentials", "status", false));
        }
    }

    /**
     * Handles user logout.
     * @param request The HTTP request containing authentication token.
     * @return ResponseEntity with logout confirmation message.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = jwtUtils.getJwtFromHeader(request);
        authService.logout(token);
        return ResponseEntity.ok(Map.of("message", "User logged out successfully", "status", true));
    }

    /**
     * Retrieves the authenticated user's details.
     * @return ResponseEntity with user details.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDetailedResponseDTO> getAuthenticatedUser() {
        return ResponseEntity.ok(authService.getAuthenticatedUser());
    }

    /**
     * Updates the authenticated user's password.
     * @param passwordData A map containing old and new passwords.
     * @return ResponseEntity with a success or error message.
     */
    @PutMapping("/me/password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> passwordData) {
        try {
            authService.updatePassword(passwordData.get("oldPassword"), passwordData.get("newPassword"));
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}