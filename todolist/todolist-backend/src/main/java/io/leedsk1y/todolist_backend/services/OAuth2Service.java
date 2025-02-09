package io.leedsk1y.todolist_backend.services;

import io.leedsk1y.todolist_backend.models.EAuthProvider;
import io.leedsk1y.todolist_backend.models.ERole;
import io.leedsk1y.todolist_backend.models.Role;
import io.leedsk1y.todolist_backend.models.User;
import io.leedsk1y.todolist_backend.repositories.RoleRepository;
import io.leedsk1y.todolist_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class OAuth2Service {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public  User loginRegisterByGoogleOAuth2(OAuth2AuthenticationToken auth2AuthenticationToken){
        OAuth2User oAuth2User = auth2AuthenticationToken.getPrincipal();
        
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setProfileImage(picture);
            user.setPassword(null);
            user.setAuthProvider(EAuthProvider.GOOGLE);

            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role not found"));
            user.setRoles(Set.of(userRole));

            userRepository.save(user);
        }

        return user;
    }
}
