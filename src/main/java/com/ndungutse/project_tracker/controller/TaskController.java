package com.ndungutse.project_tracker.controller;

import com.ndungutse.project_tracker.dto.TaskDTO;
import com.ndungutse.project_tracker.model.Task;
import com.ndungutse.project_tracker.service.TaskService;
import com.ndungutse.project_tracker.security.SecurityUtil;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Task", description = "Task management APIs")
public class TaskController {

        private final TaskService taskService;
        private final SecurityUtil securityUtil;

        public TaskController(TaskService taskService, SecurityUtil securityUtil) {
                this.taskService = taskService;
                this.securityUtil = securityUtil;
        }

        // Create a new task
        @Operation(summary = "Create a new task", description = "Creates a new task with the provided details")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Task created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
        })
        @PostMapping
        public ResponseEntity<TaskDTO> createTask(
                        @Parameter(description = "Task data to create", required = true) @Valid @RequestBody TaskDTO taskDTO) {

                Optional<TaskDTO> createdTask = taskService.create(taskDTO);

                return createdTask.map(value -> new ResponseEntity<>(value,
                                HttpStatus.CREATED))
                                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }

        // Get all tasks
        @Operation(summary = "Get all tasks", description = "Returns a list of all tasks")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDTO.class)))
        })
        @GetMapping
        public ResponseEntity<List<TaskDTO>> getAllTasks() {
                List<TaskDTO> tasks = taskService.getAll();
                return new ResponseEntity<>(tasks, HttpStatus.OK);
        }

        // Get tasks by assigned user
        @Operation(summary = "Get tasks by assigned user", description = "Returns a list of tasks assigned to a specific user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDTO.class))),
                        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
        })
        @GetMapping("/user/{userId}")
        public ResponseEntity<List<TaskDTO>> getTasksByUser(
                        @Parameter(description = "ID of the user whose tasks to retrieve", required = true) @PathVariable Long userId) {
                List<TaskDTO> tasks = taskService.getTasksByUser(userId);
                return new ResponseEntity<>(tasks, HttpStatus.OK);
        }

        // Get tasks by project
        @Operation(summary = "Get tasks by project", description = "Returns a list of tasks for a specific project")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
        })
        @GetMapping("/project/{projectId}")
        public ResponseEntity<List<TaskDTO>> getTasksByProject(
                        @Parameter(description = "ID of the project whose tasks to retrieve", required = true) @PathVariable Long projectId) {
                List<TaskDTO> tasks = taskService.getTasksByProject(projectId);
                return new ResponseEntity<>(tasks, HttpStatus.OK);
        }

        // Get tasks by status
        @Operation(summary = "Get tasks by status", description = "Returns a list of tasks with the specified status")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDTO.class)))
        })
        @GetMapping("/status/{status}")
        public ResponseEntity<List<TaskDTO>> getTasksByStatus(
                        @Parameter(description = "Status of tasks to retrieve (true for completed, false for not completed)", required = true) @PathVariable boolean status) {
                List<TaskDTO> tasks = taskService.getTasksByStatus(status);
                return new ResponseEntity<>(tasks, HttpStatus.OK);
        }

        // Get a task by ID
        @Operation(summary = "Get a task by ID", description = "Returns a task based on the provided ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved task", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
        })
        @GetMapping("/{id}")
        public ResponseEntity<TaskDTO> getTaskById(
                        @Parameter(description = "ID of the task to retrieve", required = true) @PathVariable Long id) {
                Optional<TaskDTO> task = taskService.getById(id);
                return task.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }

        // Update a task
        @Operation(summary = "Update a task", description = "Updates a task with the provided details")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Task updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
                        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Access denied - Only assigned developers can update their tasks", content = @Content)
        })
        @PatchMapping("/{id}")
        public ResponseEntity<TaskDTO> updateTask(
                        @Parameter(description = "ID of the task to update", required = true) @PathVariable Long id,
                        @Parameter(description = "Updated task data", required = true) @Valid @RequestBody TaskDTO taskDTO) {
                // Validate task update access
                securityUtil.validateTaskUpdateAccess(id);

                Optional<TaskDTO> updatedTask = taskService.update(id, taskDTO);
                return updatedTask.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }

        // Delete a task
        @Operation(summary = "Delete a task", description = "Deletes a task based on the provided ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Task deleted successfully", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteTask(
                        @Parameter(description = "ID of the task to delete", required = true) @PathVariable Long id) {
                if (!taskService.exists(id)) {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                taskService.delete(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Get overdue tasks
        @Operation(summary = "Get overdue tasks", description = "Returns a list of tasks that are past their due date and not completed")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved overdue tasks", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDTO.class)))
        })
        @GetMapping("/overdue")
        public ResponseEntity<List<TaskDTO>> getOverdueTasks() {
                List<TaskDTO> tasks = taskService.getOverdueTasks();
                return new ResponseEntity<>(tasks, HttpStatus.OK);
        }
}
