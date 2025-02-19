package io.leedsk1y.taskmanagerx_backend.controllers;

import io.leedsk1y.taskmanagerx_backend.dto.LoginRequestDTO;
import io.leedsk1y.taskmanagerx_backend.dto.LoginResponseDTO;
import io.leedsk1y.taskmanagerx_backend.dto.RegisterRequestDTO;
import io.leedsk1y.taskmanagerx_backend.models.User;
import io.leedsk1y.taskmanagerx_backend.repositories.UserRepository;
import io.leedsk1y.taskmanagerx_backend.security.jwt.JwtUtils;
import io.leedsk1y.taskmanagerx_backend.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final JwtUtils jwtUtils;

    public AuthController(UserRepository userRepository, AuthService authService, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        // check if user exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Email is already in use", "status", false));
        }

        // register user
        User newUser = authService.registerUser(request.getName(), request.getEmail(), request.getPassword());

        return ResponseEntity.ok(Map.of("message", "User registered successfully", "user", newUser));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            // authenticate user
            LoginResponseDTO response = authService.authenticateUser(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Bad credentials");
            errorResponse.put("status", false);

            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = jwtUtils.getJwtFromHeader(request);

        // blacklist token and clear security context
        if (token != null) {
            jwtUtils.blacklistToken(token);
        }
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(Map.of("message", "User logged out successfully", "status", true));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .map(user -> ResponseEntity.ok(Map.of(
                        "id", user.getId(),
                        "name", user.getName(),
                        "email", user.getEmail(),
                        "profileImage", Optional.ofNullable(user.getProfileImage()).orElse(""),
                        "roles", user.getRoles(),
                        "authProvider", user.getAuthProvider()
                )))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "User not found", "status", false)));
    }
}
