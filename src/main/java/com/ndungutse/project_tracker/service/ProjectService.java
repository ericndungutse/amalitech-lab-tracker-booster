package com.ndungutse.project_tracker.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ndungutse.project_tracker.dto.ProjectDTO;
import com.ndungutse.project_tracker.dto.mapper.ProjectMapper;
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
    public Page<ProjectDTO> getAll(
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Project> projectPage = projectRepository.findAll(pageable);
        return projectMapper.toPageDto(projectPage);
    }

    public Optional<ProjectDTO> getById(Long id) {
        Optional<Project> projectOpt = projectRepository.findById(id);
        return Optional.ofNullable(projectMapper.toDto(projectOpt.get()));
    }

    @Transactional
    public ProjectDTO update(
            Long id,
            ProjectDTO updatedProjectDTO) {
        Optional<Project> existingProject = projectRepository.findById(id);
        if (existingProject.isPresent()) {
            Project project = existingProject.get();

            // Only update fields that are not null to leverage @DynamicUpdate
            if (updatedProjectDTO.getName() != null) {
                project.setName(updatedProjectDTO.getName());
            }

            if (updatedProjectDTO.getDescription() != null) {
                project.setDescription(updatedProjectDTO.getDescription());
            }

            if (updatedProjectDTO.getDeadline() != null) {
                project.setDeadline(updatedProjectDTO.getDeadline());
            }

            // Status is a primitive boolean, so we always update it
            project.setStatus(updatedProjectDTO.isStatus());

            ProjectDTO updatedDTO = projectMapper.toDto(project);

            // Log the update action
            auditService.logUpdateAction("Project", id, "dummy_user", updatedDTO);

            return updatedDTO;
        }
        return null;
    }

    // Delete
    public void delete(Long id) {
        // Get the project before deleting it to log its data
        Optional<Project> projectOpt = projectRepository.findById(id);
        if (projectOpt.isPresent()) {
            ProjectDTO projectDTO = projectMapper.toDto(projectOpt.get());
            projectRepository.deleteById(id);

            // Log the delete action
            auditService.logDeleteAction("Project", id, "dummy_user", projectDTO);
        }
    }

    public boolean exists(Long id) {
        return projectRepository.existsById(id);
    }
}
