package com.ndungutse.project_tracker.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "projects")
@DynamicUpdate
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    private String description;
    private LocalDate deadline;
    private boolean status;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude  // Prevent circular reference in toString()
    @Builder.Default   // Use this default value when using the builder pattern
    private List<Task> tasks = new ArrayList<>();

    // Custom methods for managing bidirectional relationship

    public void addTask(Task task) {
        tasks.add(task);
        task.setProject(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setProject(null);
    }
}
