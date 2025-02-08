package io.leedsk1y.todolist_backend.controllers;

import io.leedsk1y.todolist_backend.dto.LoginRequest;
import io.leedsk1y.todolist_backend.dto.LoginResponse;
import io.leedsk1y.todolist_backend.dto.RegisterRequest;
import io.leedsk1y.todolist_backend.models.User;
import io.leedsk1y.todolist_backend.repositories.UserRepository;
import io.leedsk1y.todolist_backend.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // creating user
        User newUser = authService.registerUser(request.getName(), request.getEmail(), request.getPassword());

        return ResponseEntity.ok(Map.of("message", "User registered successfully", "user", newUser));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.authenticateUser(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Bad credentials");
            errorResponse.put("status", false);
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }
}
