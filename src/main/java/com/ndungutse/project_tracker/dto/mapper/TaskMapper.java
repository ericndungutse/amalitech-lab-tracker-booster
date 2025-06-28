package com.ndungutse.project_tracker.dto.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ndungutse.project_tracker.dto.TaskDTO;
import com.ndungutse.project_tracker.model.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "userId", source = "assignedUser.id")
    TaskDTO toDto(Task task);

    @Mapping(target = "assignedUser", ignore = true)
    @Mapping(target = "project", ignore = true)
    Task toEntity(TaskDTO taskDTO);

    List<TaskDTO> toDtoList(List<Task> tasks);
}
