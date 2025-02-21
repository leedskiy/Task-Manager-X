package io.leedsk1y.taskmanagerx_backend.services;

import io.leedsk1y.taskmanagerx_backend.dto.LoginResponseDTO;
import io.leedsk1y.taskmanagerx_backend.dto.RegisterRequestDTO;
import io.leedsk1y.taskmanagerx_backend.dto.UserDetailedResponseDTO;
import io.leedsk1y.taskmanagerx_backend.models.EAuthProvider;
import io.leedsk1y.taskmanagerx_backend.models.ERole;
import io.leedsk1y.taskmanagerx_backend.models.Role;
import io.leedsk1y.taskmanagerx_backend.models.User;
import io.leedsk1y.taskmanagerx_backend.repositories.RoleRepository;
import io.leedsk1y.taskmanagerx_backend.repositories.UserRepository;
import io.leedsk1y.taskmanagerx_backend.security.jwt.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                       JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Registers a new user.
     * @param request The registration request containing user details.
     * @return The registered user as UserDetailedResponseDTO.
     */
    public UserDetailedResponseDTO registerUser(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAuthProvider(EAuthProvider.DEFAULT);

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role not found"));
        roles.add(userRole);
        user.setRoles(roles);

        return new UserDetailedResponseDTO(userRepository.save(user));
    }

    /**
     * Authenticates a user and generates a JWT token.
     * @param email The user's email.
     * @param password The user's password.
     * @return LoginResponseDTO containing the JWT token and user details.
     */
    public LoginResponseDTO authenticateUser(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByEmail(email.toLowerCase())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

            return new LoginResponseDTO(jwtToken, user.getId(), user.getEmail(),
                    user.getProfileImage(), userDetails.getAuthorities(), user.getAuthProvider());
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }

    /**
     * Logs out the user by blacklisting the JWT token.
     * @param token The JWT token to be blacklisted.
     */
    public void logout(String token) {
        if (token != null) {
            jwtUtils.blacklistToken(token);
        }
        SecurityContextHolder.clearContext();
    }

    /**
     * Retrieves the currently authenticated user.
     * @return UserDetailedResponseDTO containing the user details.
     */
    public UserDetailedResponseDTO getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDetailedResponseDTO(user);
    }

    /**
     * Updates the password of the authenticated user.
     * @param oldPassword The user's current password.
     * @param newPassword The new password to be set.
     */
    public void updatePassword(String oldPassword, String newPassword) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Incorrect old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}