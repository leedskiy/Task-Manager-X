package io.leedsk1y.todolist_backend.controllers;

import io.leedsk1y.todolist_backend.dto.LoginResponseDTO;
import io.leedsk1y.todolist_backend.services.OAuth2Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final OAuth2Service oAuthService;

    public OAuth2Controller(OAuth2Service oAuthService) {
        this.oAuthService = oAuthService;
    }

    @GetMapping("/google")
    public ResponseEntity<String > loginGoogleAuth(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
        return ResponseEntity.ok("Redirecting...");
    }

    @GetMapping("/success")
    public void handleGoogleSuccess(HttpServletResponse response) throws IOException{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized: OAuth2 token is missing.");
            return;
        }
        OAuth2AuthenticationToken auth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;

        LoginResponseDTO loginResponse = oAuthService.loginRegisterByGoogleOAuth2(auth2AuthenticationToken);

        Cookie jwtCookie = new Cookie("token", loginResponse.getToken());
        jwtCookie.setHttpOnly(false);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60);
        jwtCookie.setAttribute("SameSite", "None");

        response.addCookie(jwtCookie);

        response.sendRedirect(frontendUrl);
    }

    @GetMapping("/failure")
    public ResponseEntity<String> handleLoginFailure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
    }
}
