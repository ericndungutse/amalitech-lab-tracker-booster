package com.ndungutse.project_tracker.dto;

import com.ndungutse.project_tracker.model.Project;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDate deadline;
    private boolean status;

    // Default constructor is handled by @NoArgsConstructor

    // Constructor with parameters
    public ProjectDTO(
            Long id,
            String name,
            String description,
            LocalDate deadline,
            boolean status
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
    }

    // Constructor without ID (for creating new projects)
    public ProjectDTO(
            String name,
            String description,
            LocalDate deadline,
            boolean status
    ) {
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
    }

    // Constructor from Project entity
    public ProjectDTO(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
        this.deadline = project.getDeadline();
        this.status = project.isStatus();
    }

    // Convert Entity to DTO
    public static ProjectDTO fromEntity(Project project) {
        return new ProjectDTO(project);
    }

    // Getters and Setters are handled by @Data

    // Convert DTO to Entity
    public Project toEntity() {
        return Project.builder()
                .name(this.name)
                .description(this.description)
                .deadline(this.deadline)
                .status(this.status)
                .id(this.id)
                .build();
    }
}
