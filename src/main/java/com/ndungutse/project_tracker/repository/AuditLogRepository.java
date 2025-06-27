package com.ndungutse.project_tracker.repository;

import com.ndungutse.project_tracker.model.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    List<AuditLog> findByEntityType(String entityType);
    List<AuditLog> findByUsername(String username);
    List<AuditLog> findByEntityTypeAndUsername(String entityType, String username);
}