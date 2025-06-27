package com.ndungutse.project_tracker.dto;

import com.ndungutse.project_tracker.model.AuditLog;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDTO {
    private String id;
    private String entityType;
    private Long entityId;
    private String action;
    private LocalDateTime timestamp;
    private String username;
    private String dataSnapshot;

    // Default constructor is handled by @NoArgsConstructor
    // Constructor with parameters is handled by @AllArgsConstructor

    // Convert Entity to DTO
    public static AuditLogDTO fromEntity(AuditLog auditLog) {
        return new AuditLogDTO(
                auditLog.getId(),
                auditLog.getEntityType(),
                auditLog.getEntityId(),
                auditLog.getAction(),
                auditLog.getTimestamp(),
                auditLog.getUsername(),
                auditLog.getDataSnapshot()
        );
    }

    // Getters and Setters are handled by @Data
}
