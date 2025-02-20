package io.leedsk1y.taskmanagerx_backend.controllers;

import io.leedsk1y.taskmanagerx_backend.dto.LoginResponseDTO;
import io.leedsk1y.taskmanagerx_backend.dto.UserDetailedResponseDTO;
import io.leedsk1y.taskmanagerx_backend.services.OAuth2Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/oauth2/login")
public class OAuth2Controller {
    @Value("${spring.frontend.url}")
    private String frontendUrl;

    private final OAuth2Service oAuth2Service;

    public OAuth2Controller(OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
    }

    @GetMapping("/google")
    public void redirectToGoogleAuth(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    private Cookie createAuthCookie(String token) {
        Cookie jwtCookie = new Cookie("token", token);
        jwtCookie.setHttpOnly(false);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60);
        jwtCookie.setAttribute("SameSite", "None");
        return jwtCookie;
    }

    @GetMapping("/success")
    public void handleOAuth2Success(HttpServletResponse response, Authentication authentication) throws IOException {
        try {
            LoginResponseDTO loginResponse = oAuth2Service.handleOAuth2Authentication((OAuth2AuthenticationToken) authentication);

            response.addCookie(createAuthCookie(loginResponse.getToken()));

            response.sendRedirect(frontendUrl);
        } catch (RuntimeException e) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Authentication failed: " + e.getMessage());
        }
    }

    @GetMapping("/failure")
    public ResponseEntity<String> handleOAuth2Failure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OAuth2 login failed");
    }

    @GetMapping("/me")
    public ResponseEntity<UserDetailedResponseDTO> getOAuth2AuthenticatedUser(Authentication authentication) {
        return ResponseEntity.ok(oAuth2Service.getAuthenticatedOAuth2User(authentication));
    }
}