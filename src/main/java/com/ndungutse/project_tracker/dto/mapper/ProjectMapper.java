package com.ndungutse.project_tracker.dto.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import com.ndungutse.project_tracker.dto.ProjectDTO;
import com.ndungutse.project_tracker.model.Project;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectDTO toDto(Project project);

    @Mapping(target = "tasks", ignore = true)
    Project toEntity(ProjectDTO projectDTO);

    List<ProjectDTO> toDtoList(List<Project> projects);

    default Page<ProjectDTO> toPageDto(Page<Project> projectPage) {
        return projectPage.map(this::toDto);
    }

}
