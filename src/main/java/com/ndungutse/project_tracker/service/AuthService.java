package com.ndungutse.project_tracker.service;

import com.ndungutse.project_tracker.dto.LoginRequest;
import com.ndungutse.project_tracker.dto.LoginResponse;
import com.ndungutse.project_tracker.dto.RegisterRequest;
import com.ndungutse.project_tracker.model.Role;
import com.ndungutse.project_tracker.model.User;
import com.ndungutse.project_tracker.repository.RoleRepository;
import com.ndungutse.project_tracker.repository.UserRepository;
import com.ndungutse.project_tracker.security.CustomUserDetails;
import com.ndungutse.project_tracker.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
            UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        // Authenticate user with username or email
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()));

        // Get authenticated user details
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Generate JWT token
        String token = jwtUtils.generateJwtTokenFromUsername(userDetails);

        // Build and return response
        return LoginResponse.builder()
                .token(token)
                .userId(userDetails.getUserId())
                .username(userDetails.getUsername())
                .email(userDetails.getUser().getEmail())
                .role(userDetails.getRoleName())
                .build();
    }

    @Transactional
    public LoginResponse register(RegisterRequest registerRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Generate username from email (take part before @)
        String username = registerRequest.getEmail().split("@")[0];

        // Get CONTRACTOR role
        Role contractorRole = roleRepository.findByRoleName("CONTRACTOR")
                .orElseThrow(() -> new IllegalArgumentException("CONTRACTOR role not found"));

        // Create new user
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(contractorRole);

        // Save user
        user = userRepository.save(user);

        // Authenticate the newly registered user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getEmail(),
                        registerRequest.getPassword()));

        // Get authenticated user details
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Generate JWT token
        String token = jwtUtils.generateJwtTokenFromUsername(userDetails);

        // Build and return response
        return LoginResponse.builder()
                .token(token)
                .userId(userDetails.getUserId())
                .username(userDetails.getUsername())
                .email(userDetails.getUser().getEmail())
                .role(userDetails.getRoleName())
                .build();
    }
}