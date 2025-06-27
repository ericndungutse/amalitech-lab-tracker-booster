package com.ndungutse.project_tracker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndungutse.project_tracker.model.AuditLog;
import com.ndungutse.project_tracker.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService {
    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public AuditService(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    public void logCreateAction(String entityType, Long entityId, String username, Object data) {
        logAction(entityType, entityId, "CREATE", username, data);
    }

    public void logUpdateAction(String entityType, Long entityId, String username, Object data) {
        logAction(entityType, entityId, "UPDATE", username, data);
    }

    public void logDeleteAction(String entityType, Long entityId, String username, Object data) {
        logAction(entityType, entityId, "DELETE", username, data);
    }

    private void logAction(String entityType, Long entityId, String action, String username, Object data) {
        try {
            String dataSnapshot = objectMapper.writeValueAsString(data);
            AuditLog auditLog = new AuditLog(entityType, entityId, action, username, dataSnapshot);
            auditLogRepository.save(auditLog);
        } catch (JsonProcessingException e) {
            // Log the error but don't fail the main operation
            System.err.println("Error serializing data for audit log: " + e.getMessage());
        }
    }

    public List<AuditLog> findByEntityType(String entityType) {
        return auditLogRepository.findByEntityType(entityType);
    }

    public List<AuditLog> findByUsername(String username) {
        return auditLogRepository.findByUsername(username);
    }

    public List<AuditLog> findByEntityTypeAndUsername(String entityType, String username) {
        return auditLogRepository.findByEntityTypeAndUsername(entityType, username);
    }

    public List<AuditLog> findAll() {
        return auditLogRepository.findAll();
    }
}