package com.ndungutse.project_tracker.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskSummaryDTO {
    private String title;
    private boolean status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    public TaskSummaryDTO(String title, boolean status, LocalDate dueDate) {
        this.title = title;
        this.status = status;
        this.dueDate = dueDate;
    }
}
