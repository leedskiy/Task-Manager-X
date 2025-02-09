package io.leedsk1y.todolist_backend.services;

import io.leedsk1y.todolist_backend.models.EAuthProvider;
import io.leedsk1y.todolist_backend.models.ERole;
import io.leedsk1y.todolist_backend.models.Role;
import io.leedsk1y.todolist_backend.models.User;
import io.leedsk1y.todolist_backend.repositories.RoleRepository;
import io.leedsk1y.todolist_backend.repositories.UserRepository;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class OAuth2Service {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public OAuth2Service(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    private String getEmail(OAuth2User oAuth2User) {
        return oAuth2User.getAttribute("email");
    }

    public User loginRegisterByGoogleOAuth2(OAuth2AuthenticationToken auth2AuthenticationToken) {
        OAuth2User oAuth2User = auth2AuthenticationToken.getPrincipal();
        String email = getEmail(oAuth2User);

        return userRepository.findByEmail(email)
                .orElseGet(() -> createNewOAuthUser(oAuth2User));
    }

    private User createNewOAuthUser(OAuth2User oAuth2User) {
        User user = new User();
        user.setEmail(getEmail(oAuth2User));
        user.setName(oAuth2User.getAttribute("name"));
        user.setProfileImage(oAuth2User.getAttribute("picture"));
        user.setPassword(null);
        user.setAuthProvider(EAuthProvider.GOOGLE);

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role not found"));
        user.setRoles(Set.of(userRole));

        return userRepository.save(user);
    }
}
