package com.ndungutse.project_tracker.service;

import com.ndungutse.project_tracker.dto.ProjectDTO;
import com.ndungutse.project_tracker.dto.TaskDTO;
import com.ndungutse.project_tracker.dto.UserDTO;
import com.ndungutse.project_tracker.model.Project;
import com.ndungutse.project_tracker.model.Task;
import com.ndungutse.project_tracker.model.User;
import com.ndungutse.project_tracker.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskDTO taskDTO;
    private User user;
    private Project project;
    private ProjectDTO projectDTO;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");

        project = new Project();
        project.setId(1L);
        project.setName("Test Project");

        projectDTO = new ProjectDTO();
        projectDTO.setId(1L);
        projectDTO.setName("Test Project");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(false);
        task.setProject(project);
        task.setAssignedUser(user);

        taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setTitle("Test Task");
        taskDTO.setDescription("Test Description");
        taskDTO.setStatus(false);
        taskDTO.setProjectId(1L);
        taskDTO.setAssignedUserId(1L);
    }

    @Test
    void getTasksByUser_ShouldReturnUserTasks() {
        // Arrange
        List<Task> tasks = Arrays.asList(task);
        when(taskRepository.findByAssignedUserId(1L)).thenReturn(tasks);

        // Act
        List<TaskDTO> result = taskService.getTasksByUser(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(task.getId(), result.get(0).getId());
        assertEquals(task.getTitle(), result.get(0).getTitle());
        assertEquals(task.getAssignedUser().getId(), result.get(0).getAssignedUserId());
        verify(taskRepository, times(1)).findByAssignedUserId(1L);
    }

    @Test
    void getTasksByProject_ShouldReturnProjectTasks() {
        // Arrange
        List<Task> tasks = Arrays.asList(task);
        when(taskRepository.findByProjectId(1L)).thenReturn(tasks);
        when(projectService.exists(1L)).thenReturn(true);

        // Act
        List<TaskDTO> result = taskService.getTasksByProject(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(task.getId(), result.get(0).getId());
        assertEquals(task.getTitle(), result.get(0).getTitle());
        assertEquals(task.getProject().getId(), result.get(0).getProjectId());
        verify(taskRepository, times(1)).findByProjectId(1L);
    }

    @Test
    void getTasksByStatus_ShouldReturnTasksWithStatus() {
        // Arrange
        List<Task> tasks = Arrays.asList(task);
        when(taskRepository.findByStatus(false)).thenReturn(tasks);

        // Act
        List<TaskDTO> result = taskService.getTasksByStatus(false);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(task.getId(), result.get(0).getId());
        assertEquals(task.getTitle(), result.get(0).getTitle());
        assertEquals(task.isStatus(), result.get(0).isStatus());
        verify(taskRepository, times(1)).findByStatus(false);
    }

    @Test
    void createTask_WithValidData_ShouldCreateTask() {
        // Arrange
        when(projectService.exists(1L)).thenReturn(true);
        when(userService.exists(1L)).thenReturn(true);
        when(projectService.getById(1L)).thenReturn(Optional.of(projectDTO));
        when(userService.getUserById(1L)).thenReturn(Optional.of(userDTO));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        Optional<TaskDTO> result = taskService.create(taskDTO);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(task.getId(), result.get().getId());
        assertEquals(task.getTitle(), result.get().getTitle());
        assertEquals(task.getAssignedUser().getId(), result.get().getAssignedUserId());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_WithValidData_ShouldUpdateTask() {
        // Arrange
        when(taskRepository.existsById(1L)).thenReturn(true);
        when(projectService.exists(1L)).thenReturn(true);
        when(userService.exists(1L)).thenReturn(true);
        when(projectService.getById(1L)).thenReturn(Optional.of(projectDTO));
        when(userService.getUserById(1L)).thenReturn(Optional.of(userDTO));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        Optional<TaskDTO> result = taskService.update(1L, taskDTO);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(task.getId(), result.get().getId());
        assertEquals(task.getTitle(), result.get().getTitle());
        assertEquals(task.getAssignedUser().getId(), result.get().getAssignedUserId());
        verify(taskRepository, times(1)).save(any(Task.class));
    }
}