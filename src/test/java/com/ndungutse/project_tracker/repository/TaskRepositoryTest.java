package com.ndungutse.project_tracker.repository;

import com.ndungutse.project_tracker.model.Project;
import com.ndungutse.project_tracker.model.Task;
import com.ndungutse.project_tracker.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private User user;
    private Project project;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        // Create test user
        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        entityManager.persist(user);

        // Create test project
        project = new Project();
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setStatus(false);
        entityManager.persist(project);

        // Create test tasks
        task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setStatus(false);
        task1.setProject(project);
        task1.setAssignedUser(user);
        entityManager.persist(task1);

        task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setStatus(true);
        task2.setProject(project);
        task2.setAssignedUser(user);
        entityManager.persist(task2);

        entityManager.flush();
    }

    @Test
    void findByAssignedUserId_ShouldReturnUserTasks() {
        // Act
        List<Task> tasks = taskRepository.findByAssignedUserId(user.getId());

        // Assert
        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().allMatch(task -> task.getAssignedUser().getId().equals(user.getId())));
    }

    @Test
    void findByProjectId_ShouldReturnProjectTasks() {
        // Act
        List<Task> tasks = taskRepository.findByProjectId(project.getId());

        // Assert
        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().allMatch(task -> task.getProject().getId().equals(project.getId())));
    }

    @Test
    void findByStatus_ShouldReturnTasksWithStatus() {
        // Act
        List<Task> completedTasks = taskRepository.findByStatus(true);
        List<Task> incompleteTasks = taskRepository.findByStatus(false);

        // Assert
        assertNotNull(completedTasks);
        assertNotNull(incompleteTasks);
        assertEquals(1, completedTasks.size());
        assertEquals(1, incompleteTasks.size());
        assertTrue(completedTasks.stream().allMatch(Task::isStatus));
        assertTrue(incompleteTasks.stream().noneMatch(Task::isStatus));
    }
}