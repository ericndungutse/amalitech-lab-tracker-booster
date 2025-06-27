package com.ndungutse.project_tracker.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndungutse.project_tracker.dto.LoginResponse;
import com.ndungutse.project_tracker.model.User;
import com.ndungutse.project_tracker.repository.UserRepository;
import com.ndungutse.project_tracker.security.CustomUserDetails;
import com.ndungutse.project_tracker.security.CustomUserDetailsService;
import com.ndungutse.project_tracker.security.JwtAuthenticationFilter;
import com.ndungutse.project_tracker.security.JwtUtils;
import com.ndungutse.project_tracker.service.CustomOidcUserService;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final AuthenticationEntryPoint customAuthenticationEntryPoint;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
            JwtAuthenticationFilter jwtAuthFilter,
            JwtUtils jwtUtils,
            UserRepository userRepository,
            ObjectMapper objectMapper,
            AuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            CustomOidcUserService customOidcUserService) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/oauth2/**",
                                "/login/oauth2/**")
                        .permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginProcessingUrl("/login/oauth2/code/google")
                        .userInfoEndpoint(userServiceInfo -> userServiceInfo.oidcUserService(customOidcUserService))
                        .successHandler((request, response, authentication) -> {
                            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
                            // Get the authenticated user's email
                            String email = oidcUser.getEmail();
                            // Find the user in our database
                            Optional<User> userOpt = userRepository.findByEmail(email);
                            if (userOpt.isPresent()) {
                                User user = userOpt.get();

                                // Create CustomUserDetails for JWT generation
                                CustomUserDetails userDetails = new CustomUserDetails(user);

                                // Generate JWT token
                                String token = jwtUtils.generateJwtTokenFromUsername(userDetails);

                                // Create LoginResponse
                                LoginResponse loginResponse = LoginResponse.builder()
                                        .token(token)
                                        .userId(user.getId())
                                        .username(user.getUsername())
                                        .email(user.getEmail())
                                        .role(user.getRole().getRoleName())
                                        .build();

                                // Send response
                                response.setContentType("application/json");
                                response.getWriter().write(objectMapper.writeValueAsString(loginResponse));
                            } else {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.getWriter().write("User not found");
                            }
                        }))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(entryPoint -> entryPoint.authenticationEntryPoint(customAuthenticationEntryPoint));

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
