package com.ndungutse.project_tracker.repository;

import com.ndungutse.project_tracker.dto.TaskSummaryDTO;
import com.ndungutse.project_tracker.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedUserId(Long userId);

    List<Task> findByProjectId(Long projectId);

    List<Task> findByStatus(boolean status);

    @Query("SELECT t FROM Task t WHERE t.dueDate < :currentDate AND t.status = false")
    List<Task> findOverdueTasks(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT new com.ndungutse.project_tracker.dto.TaskSummaryDTO(t.title, t.status, t.dueDate) " +
            "FROM Task t WHERE t.id = :taskId")
    TaskSummaryDTO findTaskSummaryDTOById(Long taskId);
}