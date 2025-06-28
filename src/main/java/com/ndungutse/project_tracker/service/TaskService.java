package com.ndungutse.project_tracker.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ndungutse.project_tracker.dto.TaskDTO;
import com.ndungutse.project_tracker.dto.TaskSummaryDTO;
import com.ndungutse.project_tracker.dto.UserDTO;
import com.ndungutse.project_tracker.dto.mapper.ProjectMapper;
import com.ndungutse.project_tracker.dto.mapper.TaskMapper;
import com.ndungutse.project_tracker.exception.ResourceNotFoundException;
import com.ndungutse.project_tracker.model.Project;
import com.ndungutse.project_tracker.model.Task;
import com.ndungutse.project_tracker.model.User;
import com.ndungutse.project_tracker.repository.TaskRepository;

import jakarta.transaction.Transactional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    private final UserService userService;
    private final TaskMapper taskMapper;
    private final ProjectMapper projectMapper;

    public TaskService(
            TaskRepository taskRepository,
            ProjectService projectService,
            UserService userService,
            TaskMapper taskMapper,
            ProjectMapper projectMapper) {
        this.taskRepository = taskRepository;
        this.projectService = projectService;
        this.userService = userService;
        this.taskMapper = taskMapper;
        this.projectMapper = projectMapper;
    }

    // Task Summery
    public TaskSummaryDTO getTaskSummaryById(Long taskId) {
        return Optional.ofNullable(taskRepository.findTaskSummaryDTOById(taskId))
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
    }

    // Create
    @Transactional
    public Optional<TaskDTO> create(TaskDTO taskDTO) {

        // Create and save the task
        Task newTask = taskMapper.toEntity(taskDTO);

        // Get the project entity
        Project project = projectMapper.toEntity(projectService.getById(taskDTO.getProjectId()));

        // Get the assigned user entity if provided
        User assignedUser = null;
        if (taskDTO.getUserId() != null && userService.exists(taskDTO.getUserId())) {
            assignedUser = userService.getUserById(taskDTO.getUserId()).get().toEntity();
            newTask.setAssignedUser(assignedUser);
        }

        if (project != null) {
            newTask.setProject(project);
        }

        newTask.setAssignedUser(assignedUser);
        Task savedTask = taskRepository.save(newTask);

        return Optional.of(taskMapper.toDto(savedTask));
    }

    // Read
    public List<TaskDTO> getAll() {
        return taskMapper.toDtoList(taskRepository.findAll());
    }

    public Optional<TaskDTO> getById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        return Optional.of(taskMapper.toDto(task));
    }

    // Get tasks by assigned user
    public List<TaskDTO> getTasksByUser(Long userId) {
        // First verify that the user exists
        if (!userService.exists(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return taskMapper.toDtoList(taskRepository.findByAssignedUserId(userId));
    }

    // Get tasks by project
    public List<TaskDTO> getTasksByProject(Long projectId) {
        // First verify that the project exists
        if (!projectService.exists(projectId)) {
            throw new ResourceNotFoundException("Project not found with id: " + projectId);
        }

        return taskMapper.toDtoList(taskRepository.findByProjectId(projectId));
    }

    // Get tasks by status
    public List<TaskDTO> getTasksByStatus(boolean status) {
        return taskMapper.toDtoList(taskRepository.findByStatus(status));
    }

    // Update
    @Transactional
    public Optional<TaskDTO> update(
            Long id,
            TaskDTO updatedTaskDTO) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID " + id + " does not exist."));

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

            Optional<Project> projectOpt = Optional.ofNullable(projectMapper
                    .toEntity(projectService.getById(updatedTaskDTO.getProjectId())));

            projectOpt.ifPresent(existingTask::setProject);
        }

        // Update assigned user if provided and exists
        if (updatedTaskDTO.getUserId() != null) {
            Optional<User> userOpt = userService.getUserById(updatedTaskDTO.getUserId())
                    .map(UserDTO::toEntity);

            userOpt.ifPresent(existingTask::setAssignedUser);
        }

        return Optional.of(taskMapper.toDto(taskRepository.save(existingTask)));
    }

    // Delete
    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task with ID " + id + " does not exist.");
        }
        taskRepository.deleteById(id);
    }

    public boolean exists(Long id) {
        return taskRepository.existsById(id);
    }

    // Get overdue tasks
    public List<TaskDTO> getOverdueTasks() {
        return taskMapper.toDtoList(taskRepository.findOverdueTasks(LocalDate.now()));
    }
}
