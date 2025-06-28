package com.ndungutse.project_tracker.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ndungutse.project_tracker.dto.CreateUserRequest;
import com.ndungutse.project_tracker.dto.UpdateUserRequest;
import com.ndungutse.project_tracker.dto.UserDTO;
import com.ndungutse.project_tracker.exception.ResourceNotFoundException;
import com.ndungutse.project_tracker.model.Role;
import com.ndungutse.project_tracker.model.User;
import com.ndungutse.project_tracker.repository.RoleRepository;
import com.ndungutse.project_tracker.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Create a new user
    @Transactional
    public UserDTO createUser(CreateUserRequest request) {
        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Get role
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + request.getRoleId()));

        // Create and save user
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(role)
                .build();

        User savedUser = userRepository.save(user);
        return UserDTO.fromEntity(savedUser);
    }

    // Get all users
    public List<UserDTO> getAllUsers() {
        // List<User> users = userRepository.findAll();
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Get user by ID
    public Optional<UserDTO> getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(UserDTO::fromEntity);
    }

    // Get user by username
    public Optional<UserDTO> getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(UserDTO::fromEntity);
    }

    // Update user
    @Transactional
    public Optional<UserDTO> updateUser(Long id, UpdateUserRequest request) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            return Optional.empty();
        }

        User user = existingUser.get();

        // Update username if provided and not already taken
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
            user.setUsername(request.getUsername());
        }

        // Update email if provided and not already taken
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Update role if provided
        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + request.getRoleId()));
            user.setRole(role);
        }

        User updatedUser = userRepository.save(user);
        return Optional.of(UserDTO.fromEntity(updatedUser));
    }

    // Delete user
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    // Check if user exists
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public boolean exists(Long userId) {
        return userRepository.existsById(userId);
    }
}