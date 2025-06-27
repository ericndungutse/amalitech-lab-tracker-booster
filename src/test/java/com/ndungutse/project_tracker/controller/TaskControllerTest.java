package com.ndungutse.project_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndungutse.project_tracker.dto.TaskDTO;
import com.ndungutse.project_tracker.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private TaskDTO taskDTO;
    private List<TaskDTO> taskDTOList;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

        // Setup test data
        taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setTitle("Test Task");
        taskDTO.setDescription("Test Description");
        taskDTO.setStatus(false); // false = not completed
        taskDTO.setProjectId(1L);
        taskDTO.setAssignedUserId(1L);

        TaskDTO task2 = new TaskDTO();
        task2.setId(2L);
        task2.setTitle("Another Task");
        task2.setDescription("Another Description");
        task2.setStatus(true); // true = completed
        task2.setProjectId(1L);
        task2.setAssignedUserId(2L);

        taskDTOList = Arrays.asList(taskDTO, task2);
    }

    @Test
    void createTask_ShouldReturnCreatedTask() throws Exception {
        when(taskService.create(any(TaskDTO.class))).thenReturn(Optional.of(taskDTO));

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Task")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.status", is(false)))
                .andExpect(jsonPath("$.projectId", is(1)));

        verify(taskService, times(1)).create(any(TaskDTO.class));
    }

    @Test
    void createTask_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        when(taskService.create(any(TaskDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isBadRequest());

        verify(taskService, times(1)).create(any(TaskDTO.class));
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() throws Exception {
        when(taskService.getAll()).thenReturn(taskDTOList);

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Task")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Another Task")));

        verify(taskService, times(1)).getAll();
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() throws Exception {
        when(taskService.getById(1L)).thenReturn(Optional.of(taskDTO));

        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Task")));

        verify(taskService, times(1)).getById(1L);
    }

    @Test
    void getTaskById_WhenTaskDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(taskService.getById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/tasks/99"))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).getById(99L);
    }

    @Test
    void updateTask_WhenTaskExists_ShouldReturnUpdatedTask() throws Exception {
        TaskDTO updatedDTO = new TaskDTO();
        updatedDTO.setId(1L);
        updatedDTO.setTitle("Updated Task");
        updatedDTO.setDescription("Updated Description");
        updatedDTO.setStatus(true); // true = completed
        updatedDTO.setProjectId(1L);

        when(taskService.exists(1L)).thenReturn(true);
        when(taskService.update(eq(1L), any(TaskDTO.class))).thenReturn(Optional.of(updatedDTO));

        mockMvc.perform(patch("/api/v1/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Task")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.status", is(true)));

        verify(taskService, times(1)).exists(1L);
        verify(taskService, times(1)).update(eq(1L), any(TaskDTO.class));
    }

    @Test
    void updateTask_WhenTaskDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(taskService.exists(99L)).thenReturn(false);

        mockMvc.perform(patch("/api/v1/tasks/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).exists(99L);
        verify(taskService, never()).update(eq(99L), any(TaskDTO.class));
    }

    @Test
    void updateTask_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        when(taskService.exists(1L)).thenReturn(true);
        when(taskService.update(eq(1L), any(TaskDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/v1/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isBadRequest());

        verify(taskService, times(1)).exists(1L);
        verify(taskService, times(1)).update(eq(1L), any(TaskDTO.class));
    }

    @Test
    void deleteTask_WhenTaskExists_ShouldReturnNoContent() throws Exception {
        when(taskService.exists(1L)).thenReturn(true);
        doNothing().when(taskService).delete(1L);

        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).exists(1L);
        verify(taskService, times(1)).delete(1L);
    }

    @Test
    void deleteTask_WhenTaskDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(taskService.exists(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/tasks/99"))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).exists(99L);
        verify(taskService, never()).delete(99L);
    }

    @Test
    void getTasksByUser_ShouldReturnUserTasks() throws Exception {
        List<TaskDTO> userTasks = Arrays.asList(taskDTO);
        when(taskService.getTasksByUser(1L)).thenReturn(userTasks);

        mockMvc.perform(get("/api/v1/tasks/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Task")))
                .andExpect(jsonPath("$[0].assignedUserId", is(1)));

        verify(taskService, times(1)).getTasksByUser(1L);
    }

    @Test
    void getTasksByProject_ShouldReturnProjectTasks() throws Exception {
        when(taskService.getTasksByProject(1L)).thenReturn(taskDTOList);

        mockMvc.perform(get("/api/v1/tasks/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].projectId", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].projectId", is(1)));

        verify(taskService, times(1)).getTasksByProject(1L);
    }

    @Test
    void getTasksByStatus_ShouldReturnTasksWithStatus() throws Exception {
        List<TaskDTO> completedTasks = Arrays.asList(taskDTOList.get(1));
        when(taskService.getTasksByStatus(true)).thenReturn(completedTasks);

        mockMvc.perform(get("/api/v1/tasks/status/true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].status", is(true)));

        verify(taskService, times(1)).getTasksByStatus(true);
    }

    @Test
    void getTasksByStatus_ShouldReturnTasksWithDifferentStatus() throws Exception {
        List<TaskDTO> incompleteTasks = Arrays.asList(taskDTOList.get(0));
        when(taskService.getTasksByStatus(false)).thenReturn(incompleteTasks);

        mockMvc.perform(get("/api/v1/tasks/status/false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].status", is(false)));

        verify(taskService, times(1)).getTasksByStatus(false);
    }
}
