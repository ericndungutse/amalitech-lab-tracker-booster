package com.ndungutse.project_tracker.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Repository;

import com.ndungutse.project_tracker.dto.projection.ProjectIdNameStatusDto;
import com.ndungutse.project_tracker.model.Project;

@Repository
@RedisHash("project")
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<ProjectIdNameStatusDto> findAllBy(org.springframework.data.domain.Pageable pageable);

}