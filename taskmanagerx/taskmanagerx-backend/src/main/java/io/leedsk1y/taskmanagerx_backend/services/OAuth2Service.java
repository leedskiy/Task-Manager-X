package io.leedsk1y.taskmanagerx_backend.services;

import io.leedsk1y.taskmanagerx_backend.dto.UserDetailedResponseDTO;
import io.leedsk1y.taskmanagerx_backend.models.EAuthProvider;
import io.leedsk1y.taskmanagerx_backend.models.ERole;
import io.leedsk1y.taskmanagerx_backend.models.Role;
import io.leedsk1y.taskmanagerx_backend.models.User;
import io.leedsk1y.taskmanagerx_backend.repositories.RoleRepository;
import io.leedsk1y.taskmanagerx_backend.repositories.UserRepository;
import io.leedsk1y.taskmanagerx_backend.security.jwt.JwtUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class OAuth2Service {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtils jwtUtils;

    public OAuth2Service(UserRepository userRepository, RoleRepository roleRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Handles OAuth2 authentication by retrieving or creating a user, then generating a JWT token.
     * @param auth2AuthenticationToken The authentication token from the OAuth2 provider.
     * @return LoginResponseDTO containing the JWT token and user details.
     */
    public String handleOAuth2Authentication(OAuth2AuthenticationToken auth2AuthenticationToken) {
        OAuth2User oAuth2User = auth2AuthenticationToken.getPrincipal();
        String email = Optional.ofNullable((String) oAuth2User.getAttribute("email"))
                .orElseThrow(() -> new RuntimeException("OAuth2 authentication failed: Email not found"));

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(oAuth2User));

        String profileImageUrl = oAuth2User.getAttribute("picture");
        if (profileImageUrl != null && !profileImageUrl.equals(user.getProfileImage())) {
            user.setProfileImage(profileImageUrl);
            userRepository.save(user);
        }

        return jwtUtils.generateTokenFromUsername(user.getUsername());
    }

    /**
     * Creates a new user from OAuth2 authentication data.
     * @param oAuth2User The OAuth2 user data.
     * @return The newly created user.
     */
    private User createNewUser(OAuth2User oAuth2User) {
        User user = new User();
        user.setEmail(oAuth2User.getAttribute("email"));
        user.setName(oAuth2User.getAttribute("name"));
        user.setProfileImage(oAuth2User.getAttribute("picture"));
        user.setPassword(null);
        user.setAuthProvider(EAuthProvider.GOOGLE);

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role not found"));
        user.setRoles(Set.of(userRole));

        return userRepository.save(user);
    }

    /**
     * Retrieves the authenticated OAuth2 user details.
     * @param authentication The authentication object containing user details.
     * @return UserDetailedResponseDTO containing the authenticated user's details.
     */
    public UserDetailedResponseDTO getAuthenticatedOAuth2User(Authentication authentication) {
        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            throw new RuntimeException("Unauthorized: OAuth2 token is missing");
        }

        String email = ((OAuth2AuthenticationToken) authentication).getPrincipal().getAttribute("email");
        return userRepository.findByEmail(email)
                .map(UserDetailedResponseDTO::new)
                .orElseThrow(() -> new RuntimeException("OAuth2 user not found"));
    }
}