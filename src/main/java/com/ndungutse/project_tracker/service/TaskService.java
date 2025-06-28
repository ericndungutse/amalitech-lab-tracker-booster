package com.ndungutse.project_tracker.service;

import com.ndungutse.project_tracker.dto.ProjectDTO;
import com.ndungutse.project_tracker.dto.TaskDTO;
import com.ndungutse.project_tracker.dto.UserDTO;
import com.ndungutse.project_tracker.model.Project;
import com.ndungutse.project_tracker.model.Task;
import com.ndungutse.project_tracker.model.User;
import com.ndungutse.project_tracker.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    private final UserService userService;

    public TaskService(
            TaskRepository taskRepository,
            ProjectService projectService,
            UserService userService) {
        this.taskRepository = taskRepository;
        this.projectService = projectService;
        this.userService = userService;
    }

    // Create
    @Transactional
    public Optional<TaskDTO> create(TaskDTO taskDTO) {
        // Validate that the project exists
        if (taskDTO.getProjectId() == null || !projectService.exists(taskDTO.getProjectId())) {
            return Optional.empty();
        }

        // Get the project entity
        Optional<Project> project = projectService.getById(taskDTO.getProjectId())
                .map(ProjectDTO::toEntity);

        if (project.isEmpty()) {
            return Optional.empty();
        }

        // Get the assigned user entity if provided
        User assignedUser = null;
        if (taskDTO.getAssignedUserId() != null) {
            assignedUser = userService.getUserById(taskDTO.getAssignedUserId())
                    .map(UserDTO::toEntity)
                    .orElse(null);
        }

        // Create and save the task
        Task task = taskDTO.toEntity(project.get(), assignedUser);
        Task savedTask = taskRepository.save(task);

        return Optional.of(TaskDTO.fromEntity(savedTask));
    }

    // Read
    public List<TaskDTO> getAll() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<TaskDTO> getById(Long id) {
        return Optional.ofNullable(taskRepository.findById(id).map(TaskDTO::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id)));
    }

    // Get tasks by assigned user
    public List<TaskDTO> getTasksByUser(Long userId) {
        // First verify that the user exists
        if (!userService.exists(userId)) {
            return List.of();
        }

        return taskRepository.findByAssignedUserId(userId).stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Get tasks by project
    public List<TaskDTO> getTasksByProject(Long projectId) {
        // First verify that the project exists
        if (!projectService.exists(projectId)) {
            return List.of();
        }

        return taskRepository.findByProjectId(projectId).stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Get tasks by status
    public List<TaskDTO> getTasksByStatus(boolean status) {
        return taskRepository.findByStatus(status).stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Update
    @Transactional
    public Optional<TaskDTO> update(
            Long id,
            TaskDTO updatedTaskDTO) {
        Optional<Task> existingTaskOpt = taskRepository.findById(id);

        if (existingTaskOpt.isEmpty()) {
            return Optional.empty();
        }

        Task existingTask = existingTaskOpt.get();

        // Update fields that are not null
        if (updatedTaskDTO.getTitle() != null) {
            existingTask.setTitle(updatedTaskDTO.getTitle());
        }

        if (updatedTaskDTO.getDescription() != null) {
            existingTask.setDescription(updatedTaskDTO.getDescription());
        }

        if (updatedTaskDTO.getDueDate() != null) {
            existingTask.setDueDate(updatedTaskDTO.getDueDate());
        }

        // Status is a primitive boolean, so we always update it
        existingTask.setStatus(updatedTaskDTO.isStatus());

        // Update project if provided and exists
        if (updatedTaskDTO.getProjectId() != null &&
                projectService.exists(updatedTaskDTO.getProjectId())) {

            Optional<Project> projectOpt = projectService.getById(updatedTaskDTO.getProjectId())
                    .map(ProjectDTO::toEntity);

            projectOpt.ifPresent(existingTask::setProject);
        }

        // Update assigned user if provided and exists
        if (updatedTaskDTO.getAssignedUserId() != null) {
            Optional<User> userOpt = userService.getUserById(updatedTaskDTO.getAssignedUserId())
                    .map(UserDTO::toEntity);

            userOpt.ifPresent(existingTask::setAssignedUser);
        }

        return Optional.of(TaskDTO.fromEntity(taskRepository.save(existingTask)));
    }

    // Delete
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    public boolean exists(Long id) {
        return taskRepository.existsById(id);
    }

    // Get overdue tasks
    public List<TaskDTO> getOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDate.now()).stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
