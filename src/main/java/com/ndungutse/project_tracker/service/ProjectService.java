package com.ndungutse.project_tracker.service;

import com.ndungutse.project_tracker.dto.ProjectDTO;
import com.ndungutse.project_tracker.model.Project;
import com.ndungutse.project_tracker.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final AuditService auditService;

    public ProjectService(
            ProjectRepository projectRepository,
            AuditService auditService
    ) {
        this.projectRepository = projectRepository;
        this.auditService = auditService;
    }

    // Create
    public ProjectDTO create(ProjectDTO projectDTO) {
        Project project = projectDTO.toEntity();
        Project savedProject = projectRepository.save(project);
        ProjectDTO savedProjectDTO = ProjectDTO.fromEntity(savedProject);

        // Log the create action
        auditService.logCreateAction("Project", savedProject.getId(), "dummy_user", savedProjectDTO);

        return savedProjectDTO;
    }

    // Read
    public List<ProjectDTO> getAll() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .map(ProjectDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Read with pagination
    public Page<ProjectDTO> getAll(
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Project> projectPage = projectRepository.findAll(pageable);
        return projectPage.map(ProjectDTO::fromEntity);
    }

    public Optional<ProjectDTO> getById(Long id) {
        Optional<Project> projectOpt = projectRepository.findById(id);
        return projectOpt.map(ProjectDTO::fromEntity);
    }

    @Transactional
    public ProjectDTO update(
            Long id,
            ProjectDTO updatedProjectDTO
    ) {
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

            ProjectDTO updatedDTO = ProjectDTO.fromEntity(project);

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
            ProjectDTO projectDTO = ProjectDTO.fromEntity(projectOpt.get());
            projectRepository.deleteById(id);

            // Log the delete action
            auditService.logDeleteAction("Project", id, "dummy_user", projectDTO);
        } else {
            projectRepository.deleteById(id);
        }
    }

    public boolean exists(Long id) {
        return !projectRepository.existsById(id);
    }
}
