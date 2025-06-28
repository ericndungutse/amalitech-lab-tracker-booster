package com.ndungutse.project_tracker.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private boolean status;
    private LocalDate dueDate;
    private Long projectId;
    private Long userId;
}
