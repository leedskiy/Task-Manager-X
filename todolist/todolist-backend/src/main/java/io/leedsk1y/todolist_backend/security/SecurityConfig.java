package io.leedsk1y.todolist_backend.security;

import io.leedsk1y.todolist_backend.repositories.UserRepository;
import io.leedsk1y.todolist_backend.security.jwt.AuthEntryPointJwt;
import io.leedsk1y.todolist_backend.security.jwt.AuthTokenFilter;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final AuthEntryPointJwt unauthorizedHandler;
    private final UserRepository userRepository;

    public SecurityConfig(AuthEntryPointJwt unauthorizedHandler, UserRepository userRepository) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.userRepository = userRepository;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder)
            throws Exception {
        return builder.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                        .requestMatchers("/h2-console", "/h2-console/**").permitAll() // (temp) allows all requests coming to h2-console
                        .requestMatchers("/auth/register", "/auth/login", "/oauth2/**").permitAll() // security
                        .anyRequest().authenticated());

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)); // cookies

        http.exceptionHandling(exception ->
                exception.authenticationEntryPoint(unauthorizedHandler)); // choosing custom exception handler JWT

        http.headers(headers ->
                headers.frameOptions(frameOptions ->
                        frameOptions.sameOrigin())); // to enable frames in h2-console

        http.csrf(csrf -> csrf.disable()); // to disable password in h2-console

        http.addFilterBefore(authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class); // add custom filter for JWT

        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/oauth2/login/google")
                .defaultSuccessUrl("/oauth2/login/success", true)
                .failureUrl("/oauth2/login/failure"));

        return http.build();
    }
}
