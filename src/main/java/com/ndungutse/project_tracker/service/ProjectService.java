package com.ndungutse.project_tracker.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ndungutse.project_tracker.dto.ProjectDTO;
import com.ndungutse.project_tracker.dto.mapper.ProjectMapper;
import com.ndungutse.project_tracker.dto.projection.ProjectIdNameStatusDto;
import com.ndungutse.project_tracker.exception.ResourceNotFoundException;
import com.ndungutse.project_tracker.model.Project;
import com.ndungutse.project_tracker.repository.ProjectRepository;

import jakarta.transaction.Transactional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final AuditService auditService;
    ProjectMapper projectMapper;

    public ProjectService(
            ProjectRepository projectRepository,
            AuditService auditService,
            ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.auditService = auditService;
        this.projectMapper = projectMapper;
    }

    // Create
    public ProjectDTO create(ProjectDTO projectDTO) {
        Project project = projectMapper.toEntity(projectDTO);
        Project savedProject = projectRepository.save(project);
        ProjectDTO savedProjectDTO = projectMapper.toDto(savedProject);

        // Log the create action
        auditService.logCreateAction("Project", savedProject.getId(), "dummy_user", savedProjectDTO);

        return savedProjectDTO;
    }

    // Read
    public List<ProjectDTO> getAll() {
        List<Project> projects = projectRepository.findAll();
        return projectMapper.toDtoList(projects);
    }

    // Read with pagination
    @Cacheable(value = "projects", key = "#page + '-' + #size")
    public Page<ProjectDTO> getAll(
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Project> projectPage = projectRepository.findAll(pageable);
        return projectMapper.toPageDto(projectPage);
    }

    @Cacheable(value = "projectById", key = "#id")
    public ProjectDTO getById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID " + id + " does not exist."));
        return projectMapper.toDto(project);
    }

    @Transactional
    @CachePut(value = "projectById", key = "#id")
    public ProjectDTO update(
            Long id,
            ProjectDTO updatedProjectDTO) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID " + id + " does not exist."));

        // Only update fields that are not null to leverage @DynamicUpdate
        if (updatedProjectDTO.getName() != null) {
            existingProject.setName(updatedProjectDTO.getName());
        }

        if (updatedProjectDTO.getDescription() != null) {
            existingProject.setDescription(updatedProjectDTO.getDescription());
        }

        if (updatedProjectDTO.getDeadline() != null) {
            existingProject.setDeadline(updatedProjectDTO.getDeadline());
        }

        // Status is a primitive boolean, so we always update it
        existingProject.setStatus(updatedProjectDTO.isStatus());

        ProjectDTO updatedDTO = projectMapper.toDto(existingProject);

        // Log the update action
        auditService.logUpdateAction("Project", id, "dummy_user", updatedDTO);

        return updatedDTO;
    }

    // project only id, name, and status
    public Page<ProjectIdNameStatusDto> getAllIdNameStatus(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProjectIdNameStatusDto> projectPage = projectRepository.findAllBy(pageable);
        return projectPage;
    }

    // Delete
    @CacheEvict(value = "projectById", key = "#id")
    public void delete(Long id) {
        if (!exists(id)) {
            throw new ResourceNotFoundException("Project with ID " + id + " does not exist.");
        }

        projectRepository.deleteById(id);
    }

    public boolean exists(Long id) {
        return projectRepository.existsById(id);
    }
}
