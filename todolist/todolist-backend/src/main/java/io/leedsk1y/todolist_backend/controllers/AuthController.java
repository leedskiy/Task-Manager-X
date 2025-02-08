package io.leedsk1y.todolist_backend.controllers;

import io.leedsk1y.todolist_backend.dto.RegisterRequest;
import io.leedsk1y.todolist_backend.models.ERole;
import io.leedsk1y.todolist_backend.models.Role;
import io.leedsk1y.todolist_backend.models.User;
import io.leedsk1y.todolist_backend.repositories.RoleRepository;
import io.leedsk1y.todolist_backend.repositories.UserRepository;
import io.leedsk1y.todolist_backend.security.jwt.JwtUtils;
import io.leedsk1y.todolist_backend.dto.LoginRequest;
import io.leedsk1y.todolist_backend.dto.LoginResponse;
import io.leedsk1y.todolist_backend.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // creating user
        User newUser = authService.registerUser(request.getName(), request.getEmail(), request.getPassword());

        // giving a role
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        newUser.setRoles(roles);

        return ResponseEntity.ok(Map.of("message", "User registered successfully", "user", newUser));
    }
}
