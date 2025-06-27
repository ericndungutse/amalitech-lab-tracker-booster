package com.ndungutse.project_tracker.controller;

import com.ndungutse.project_tracker.dto.CreateUserRequest;
import com.ndungutse.project_tracker.dto.UpdateUserRequest;
import com.ndungutse.project_tracker.dto.UserDTO;
import com.ndungutse.project_tracker.exception.ResourceNotFoundException;
import com.ndungutse.project_tracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin/users")
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Create a new user
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - Only ADMIN role can create users", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UserDTO> createUser(
            @Parameter(description = "User data to create", required = true) @Valid @RequestBody CreateUserRequest request) {
        try {
            UserDTO createdUser = userService.createUser(request);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Get all users
    @Operation(summary = "Get all users", description = "Returns a list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - Only ADMIN role can view all users", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Get a user by ID
    @Operation(summary = "Get a user by ID", description = "Returns a user based on the provided ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "ID of the user to retrieve", required = true) @PathVariable Long id) {
        Optional<UserDTO> user = userService.getUserById(id);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Get a user by username
    @Operation(summary = "Get a user by username", description = "Returns a user based on the provided username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(
            @Parameter(description = "Username of the user to retrieve", required = true) @PathVariable String username) {
        Optional<UserDTO> user = userService.getUserByUsername(username);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Update a user
    @Operation(summary = "Update a user", description = "Updates a user with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - Only ADMIN role can update users", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "ID of the user to update", required = true) @PathVariable Long id,
            @Parameter(description = "Updated user data", required = true) @Valid @RequestBody UpdateUserRequest request) {
        try {
            Optional<UserDTO> updatedUser = userService.updateUser(id, request);
            return updatedUser.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Delete a user
    @Operation(summary = "Delete a user", description = "Deletes a user based on the provided ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - Only ADMIN role can delete users", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to delete", required = true) @PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}