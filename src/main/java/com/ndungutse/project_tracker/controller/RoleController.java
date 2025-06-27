package com.ndungutse.project_tracker.controller;

import com.ndungutse.project_tracker.dto.RoleDTO;
import com.ndungutse.project_tracker.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "Role", description = "Role management APIs")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // Create a new role
    @Operation(summary = "Create a new role", description = "Creates a new role with the provided name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Role created successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    @PostMapping
    public ResponseEntity<RoleDTO> createRole(
            @Parameter(description = "Role data to create", required = true)
            @RequestBody RoleDTO roleDTO) {
        if (roleService.existsByName(roleDTO.getRoleName())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        RoleDTO createdRole = roleService.create(roleDTO);
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }

    // Get all roles
    @Operation(summary = "Get all roles", description = "Returns a list of all roles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved roles",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.getAll();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    // Get a role by ID
    @Operation(summary = "Get a role by ID", description = "Returns a role based on the provided ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved role",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class))),
        @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleById(
            @Parameter(description = "ID of the role to retrieve", required = true)
            @PathVariable Long id) {
        Optional<RoleDTO> role = roleService.getById(id);
        return role.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Update a role
    @Operation(summary = "Update a role", description = "Updates a role with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role updated successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class))),
        @ApiResponse(responseCode = "404", description = "Role not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<RoleDTO> updateRole(
            @Parameter(description = "ID of the role to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated role data", required = true)
            @RequestBody RoleDTO roleDTO
    ) {
        if (!roleService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<RoleDTO> updatedRole = roleService.update(id, roleDTO);
        return updatedRole.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    // Delete a role
    @Operation(summary = "Delete a role", description = "Deletes a role based on the provided ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Role deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(
            @Parameter(description = "ID of the role to delete", required = true)
            @PathVariable Long id) {
        if (!roleService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        roleService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}