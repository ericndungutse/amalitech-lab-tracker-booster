package com.ndungutse.project_tracker.controller;

import com.ndungutse.project_tracker.dto.AuditLogDTO;
import com.ndungutse.project_tracker.model.AuditLog;
import com.ndungutse.project_tracker.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/logs")
@Tag(name = "Audit Log", description = "Audit log management APIs")
public class LogController {
    private final AuditService auditService;

    public LogController(AuditService auditService) {
        this.auditService = auditService;
    }

    @Operation(summary = "Get audit logs", description = "Returns a list of audit logs with optional filtering by entity type and username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved audit logs",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLogDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<AuditLogDTO>> getLogs(
            @Parameter(description = "Filter logs by entity type (e.g., 'Project', 'Task', 'Developer')")
            @RequestParam(required = false) String entityType,
            @Parameter(description = "Filter logs by username")
            @RequestParam(required = false) String username
    ) {
        List<AuditLog> logs;

        if (entityType != null && username != null) {
            // Filter by both entity type and username
            logs = auditService.findByEntityTypeAndUsername(entityType, username);
        } else if (entityType != null) {
            // Filter by entity type only
            logs = auditService.findByEntityType(entityType);
        } else if (username != null) {
            // Filter by username only
            logs = auditService.findByUsername(username);
        } else {
            // No filters, return all logs
            logs = auditService.findAll();
        }

        // Convert entities to DTOs
        List<AuditLogDTO> logDTOs = logs.stream()
                .map(AuditLogDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(logDTOs);
    }
}
