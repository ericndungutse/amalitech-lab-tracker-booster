package com.ndungutse.project_tracker.controller;

import com.ndungutse.project_tracker.dto.PageResponse;
import com.ndungutse.project_tracker.dto.ProjectDTO;
import com.ndungutse.project_tracker.dto.projection.ProjectIdNameStatusDto;
import com.ndungutse.project_tracker.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Project", description = "Project management APIs")
public class ProjectController {

        private final ProjectService projectService;

        public ProjectController(ProjectService projectService) {
                this.projectService = projectService;
        }

        // Create a new project
        @Operation(summary = "Create a new project", description = "Creates a new project with the provided details")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Project created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Access denied - Only MANAGER or ADMIN roles can create projects", content = @Content)
        })
        @PostMapping
        @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
        public ResponseEntity<ProjectDTO> createProject(
                        @Parameter(description = "Project data to create", required = true) @RequestBody ProjectDTO projectDTO) {
                ProjectDTO createdProject = projectService.create(projectDTO);
                return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
        }

        // Get all projects with pagination
        @Operation(summary = "Get all projects", description = "Returns a paginated list of all projects")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved projects", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class)))
        })
        @GetMapping
        public ResponseEntity<PageResponse<ProjectDTO>> getAllProjects(
                        @Parameter(description = "Page number (0-indexed, defaults to 0)") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Number of items per page (defaults to 10)") @RequestParam(defaultValue = "10") int size) {
                int pageToGet = page == 0 ? page : page - 1;
                Page<ProjectDTO> projects = projectService.getAll(pageToGet, size);
                PageResponse<ProjectDTO> response = new PageResponse<>(projects);
                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        // Get a project by ID
        @Operation(summary = "Get a project by ID", description = "Returns a project based on the provided ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved project", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
        })
        @GetMapping("/{id}")
        public ResponseEntity<ProjectDTO> getProjectById(
                        @Parameter(description = "ID of the project to retrieve", required = true) @PathVariable Long id) {
                Optional<ProjectDTO> project = projectService.getById(id);
                return project.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }

        // Update a project
        @Operation(summary = "Update a project", description = "Updates a project with the provided details")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Project updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Project not found", content = @Content),
                        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Access denied - Only MANAGER or ADMIN roles can update projects", content = @Content)
        })
        @PatchMapping("/{id}")
        @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
        public ResponseEntity<ProjectDTO> updateProject(
                        @Parameter(description = "ID of the project to update", required = true) @PathVariable Long id,
                        @Parameter(description = "Updated project data", required = true) @RequestBody ProjectDTO projectDTO) {

                ProjectDTO updatedProject = projectService.update(id, projectDTO);
                return new ResponseEntity<>(updatedProject, HttpStatus.OK);
        }

        // id name status
        @Operation(summary = "Get all projects with ID, name, and status", description = "Returns a paginated list of all projects with only ID, name, and status")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved projects", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class)))
        })
        @GetMapping("/id-name-status")
        public ResponseEntity<PageResponse<ProjectIdNameStatusDto>> getAllProjectsIdNameStatus(
                        @Parameter(description = "Page number (0-indexed, defaults to 1)") @RequestParam(defaultValue = "1") int page,
                        @Parameter(description = "Number of items per page (defaults to 10)") @RequestParam(defaultValue = "10") int size) {
                int pageToGet = page == 0 ? page : page - 1;
                Page<ProjectIdNameStatusDto> projects = projectService.getAllIdNameStatus(pageToGet, size);
                PageResponse<ProjectIdNameStatusDto> response = new PageResponse<>(projects);
                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        // Delete a project
        @Operation(summary = "Delete a project", description = "Deletes a project based on the provided ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Project deleted successfully", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Project not found", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Access denied - Only MANAGER or ADMIN roles can delete projects", content = @Content)
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
        public ResponseEntity<Void> deleteProject(
                        @Parameter(description = "ID of the project to delete", required = true) @PathVariable Long id) {

                projectService.delete(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
}
