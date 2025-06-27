package com.ndungutse.project_tracker.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id
    private String id;
    private String entityType;
    private Long entityId;
    private String action;
    private LocalDateTime timestamp;
    private String username;
    private String dataSnapshot;

    // Default constructor is handled by @NoArgsConstructor

    // Constructor with parameters
    public AuditLog(String entityType, Long entityId, String action, String username, String dataSnapshot) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.action = action;
        this.timestamp = LocalDateTime.now();
        this.username = username;
        this.dataSnapshot = dataSnapshot;
    }

    // Getters and Setters are handled by @Data
}
