package io.leedsk1y.todolist_backend.controllers;

import io.leedsk1y.todolist_backend.services.OAuth2Service;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/oauth2/login")
public class OAuth2Controller {
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
    public ResponseEntity<? > handleGoogleSuccess(){
        // get authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: OAuth2 token is missing.");
        }
        OAuth2AuthenticationToken auth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;

        // register or login
        oAuthService.loginRegisterByGoogleOAuth2(auth2AuthenticationToken);

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("http://localhost:3000/dashboard")).build();
    }

    @GetMapping("/failure")
    public ResponseEntity<String> handleLoginFailure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
    }
}
