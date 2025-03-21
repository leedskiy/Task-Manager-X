package io.leedsk1y.taskmanagerx_backend.services;

import io.leedsk1y.taskmanagerx_backend.dto.RegisterRequestDTO;
import io.leedsk1y.taskmanagerx_backend.dto.UserDetailedResponseDTO;
import io.leedsk1y.taskmanagerx_backend.models.EAuthProvider;
import io.leedsk1y.taskmanagerx_backend.models.ERole;
import io.leedsk1y.taskmanagerx_backend.models.Role;
import io.leedsk1y.taskmanagerx_backend.models.User;
import io.leedsk1y.taskmanagerx_backend.repositories.RoleRepository;
import io.leedsk1y.taskmanagerx_backend.repositories.UserRepository;
import io.leedsk1y.taskmanagerx_backend.security.jwt.CookieUtils;
import io.leedsk1y.taskmanagerx_backend.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Optional;
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
     * Authenticates the user by checking the provided email and password, and generates a JWT token for the authenticated user.
     * @param email The email of the user trying to authenticate.
     * @param password The password of the user.
     * @return The generated JWT token used for authenticating future requests.
     * @throws RuntimeException if authentication fails due to invalid credentials.
     */
    public String authenticateUser(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email.toLowerCase(), password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByEmail(email.toLowerCase())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            return jwtUtils.generateTokenFromUsername(user.getUsername());
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }

    /**
     * Logs out the user by blacklisting the JWT token and clearing the authentication cookie.
     * @param request The HTTP request containing the JWT token to be blacklisted.
     * @param response The HTTP response where the JWT cookie will be cleared.
     */
    public void logoutUser(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtUtils.getJwtFromCookies(request);

        if (token != null) {
            jwtUtils.blacklistToken(token);
        }

        CookieUtils.clearJwtCookie(response);
    }

    /**
     * Retrieves the details of the currently authenticated user based on the JWT token stored in the cookies.
     * Validates the token before fetching user details.
     * @param request The HTTP request containing the JWT token in cookies.
     * @param response The HTTP response where the JWT token will be validated or cleared if invalid.
     * @return UserDetailedResponseDTO containing the user's details.
     * @throws ResponseStatusException if the token is invalid, expired, or if the user is not found.
     */
    public UserDetailedResponseDTO getAuthenticatedUser(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtUtils.getJwtFromCookies(request);

        if (token == null || !jwtUtils.validateJwtToken(token, response)) {
            CookieUtils.clearJwtCookie(response);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        String email = jwtUtils.getUserNameFromJwtToken(token);

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        return new UserDetailedResponseDTO(userOptional.get());
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