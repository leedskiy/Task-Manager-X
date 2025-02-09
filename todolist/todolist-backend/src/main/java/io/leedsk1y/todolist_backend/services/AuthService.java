package io.leedsk1y.todolist_backend.services;

import io.leedsk1y.todolist_backend.dto.LoginResponse;
import io.leedsk1y.todolist_backend.models.EAuthProvider;
import io.leedsk1y.todolist_backend.models.ERole;
import io.leedsk1y.todolist_backend.models.Role;
import io.leedsk1y.todolist_backend.models.User;
import io.leedsk1y.todolist_backend.repositories.RoleRepository;
import io.leedsk1y.todolist_backend.repositories.UserRepository;
import io.leedsk1y.todolist_backend.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    public User registerUser(String name, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already in use");
        }

        // create user
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setAuthProvider(EAuthProvider.DEFAULT);

        // assign user role
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role not found"));
        roles.add(userRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public LoginResponse authenticateUser(String email, String password) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            // Set authentication in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Retrieve authenticated user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Fetch user from DB
            User user = userRepository.findByEmail(email.toLowerCase())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate JWT Token
            String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

            return new LoginResponse(jwtToken, user.getId(), user.getEmail(), userDetails.getAuthorities());
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }
}
