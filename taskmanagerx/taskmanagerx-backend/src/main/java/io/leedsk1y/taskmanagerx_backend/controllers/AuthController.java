package io.leedsk1y.taskmanagerx_backend.controllers;

import io.leedsk1y.taskmanagerx_backend.dto.LoginRequestDTO;
import io.leedsk1y.taskmanagerx_backend.dto.RegisterRequestDTO;
import io.leedsk1y.taskmanagerx_backend.dto.UserDetailedResponseDTO;
import io.leedsk1y.taskmanagerx_backend.security.jwt.CookieUtils;
import io.leedsk1y.taskmanagerx_backend.security.jwt.JwtUtils;
import io.leedsk1y.taskmanagerx_backend.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
     * Authenticates the user with the provided email and password, generates a JWT token,
     * and sets the token in a cookie for further authentication.
     * @param request The login request containing user credentials (email and password).
     * @param response The HTTP response where the JWT token will be set in a cookie.
     * @return ResponseEntity with a success message if login is successful, or an error message with unauthorized status if credentials are incorrect.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request, HttpServletResponse response) {
        try {
            String jwtToken = authService.authenticateUser(request.getEmail(), request.getPassword());
            CookieUtils.setJwtCookie(response, jwtToken);

            return ResponseEntity.ok(Map.of("message", "Login successful", "status", true));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Bad credentials", "status", false));
        }
    }

    /**
     * Logs out the current user by invalidating the JWT token and clearing the authentication cookie.
     * @param request The HTTP request containing the JWT token in cookies.
     * @param response The HTTP response where the JWT cookie will be cleared.
     * @return ResponseEntity with a confirmation message and status.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = jwtUtils.getJwtFromCookies(request);

            if (token != null && jwtUtils.validateJwtToken(token, response)) {
                jwtUtils.blacklistToken(token);
            }

            CookieUtils.clearJwtCookie(response);
            authService.logoutUser(request, response);
            return ResponseEntity.ok(Map.of("message", "User logged out successfully", "status", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An error occurred during logout", "status", false));
        }
    }

    /**
     * Retrieves the details of the currently authenticated user based on the valid JWT token present in the request.
     * @param request The HTTP request containing the JWT token in cookies.
     * @param response The HTTP response where the JWT token may be validated or cleared.
     * @return ResponseEntity containing the user details if the token is valid,
     *         or an error message if the token is invalid or expired.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            UserDetailedResponseDTO user = authService.getAuthenticatedUser(request, response);
            return ResponseEntity.ok(user);
        } catch (ResponseStatusException e) {
            CookieUtils.clearJwtCookie(response);
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", e.getReason(), "status", false));
        }
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