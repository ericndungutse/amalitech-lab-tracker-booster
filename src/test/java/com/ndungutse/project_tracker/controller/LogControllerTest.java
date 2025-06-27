package com.ndungutse.project_tracker.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndungutse.project_tracker.model.AuditLog;
import com.ndungutse.project_tracker.service.AuditService;

@ExtendWith(MockitoExtension.class)
public class LogControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private LogController logController;

    private AuditLog auditLog1;
    private AuditLog auditLog2;
    private List<AuditLog> auditLogs;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(logController).build();

        // Setup test data
        auditLog1 = new AuditLog();
        auditLog1.setId("1");
        auditLog1.setAction("CREATE");
        auditLog1.setEntityType("Project");
        auditLog1.setEntityId(1L);
        auditLog1.setUsername("user1");
        auditLog1.setTimestamp(LocalDateTime.now());
        auditLog1.setDataSnapshot("Created project with ID 1");

        auditLog2 = new AuditLog();
        auditLog2.setId("2");
        auditLog2.setAction("UPDATE");
        auditLog2.setEntityType("Task");
        auditLog2.setEntityId(2L);
        auditLog2.setUsername("user2");
        auditLog2.setTimestamp(LocalDateTime.now().minusHours(1));
        auditLog2.setDataSnapshot("Updated task with ID 2");

        auditLogs = Arrays.asList(auditLog1, auditLog2);
    }

    @Test
    void getLogs_WithNoFilters_ShouldReturnAllLogs() throws Exception {
        when(auditService.findAll()).thenReturn(auditLogs);

        mockMvc.perform(get("/api/v1/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].action", is("CREATE")))
                .andExpect(jsonPath("$[0].entityType", is("Project")))
                .andExpect(jsonPath("$[1].id", is("2")))
                .andExpect(jsonPath("$[1].action", is("UPDATE")))
                .andExpect(jsonPath("$[1].entityType", is("Task")));

        verify(auditService, times(1)).findAll();
        verify(auditService, never()).findByEntityType(anyString());
        verify(auditService, never()).findByUsername(anyString());
        verify(auditService, never()).findByEntityTypeAndUsername(anyString(), anyString());
    }

    @Test
    void getLogs_WithEntityTypeFilter_ShouldReturnFilteredLogs() throws Exception {
        when(auditService.findByEntityType("Project")).thenReturn(Arrays.asList(auditLog1));

        mockMvc.perform(get("/api/v1/logs")
                .param("entityType", "Project"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].entityType", is("Project")));

        verify(auditService, times(1)).findByEntityType("Project");
        verify(auditService, never()).findAll();
        verify(auditService, never()).findByUsername(anyString());
        verify(auditService, never()).findByEntityTypeAndUsername(anyString(), anyString());
    }

    @Test
    void getLogs_WithUsernameFilter_ShouldReturnFilteredLogs() throws Exception {
        when(auditService.findByUsername("user2")).thenReturn(Arrays.asList(auditLog2));

        mockMvc.perform(get("/api/v1/logs")
                .param("username", "user2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("2")))
                .andExpect(jsonPath("$[0].username", is("user2")));

        verify(auditService, times(1)).findByUsername("user2");
        verify(auditService, never()).findAll();
        verify(auditService, never()).findByEntityType(anyString());
        verify(auditService, never()).findByEntityTypeAndUsername(anyString(), anyString());
    }

    @Test
    void getLogs_WithBothFilters_ShouldReturnFilteredLogs() throws Exception {
        when(auditService.findByEntityTypeAndUsername("Project", "user1")).thenReturn(Arrays.asList(auditLog1));

        mockMvc.perform(get("/api/v1/logs")
                .param("entityType", "Project")
                .param("username", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].entityType", is("Project")))
                .andExpect(jsonPath("$[0].username", is("user1")));

        verify(auditService, times(1)).findByEntityTypeAndUsername("Project", "user1");
        verify(auditService, never()).findAll();
        verify(auditService, never()).findByEntityType(anyString());
        verify(auditService, never()).findByUsername(anyString());
    }
}
