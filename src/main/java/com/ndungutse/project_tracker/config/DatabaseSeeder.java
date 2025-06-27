package com.ndungutse.project_tracker.config;

import com.ndungutse.project_tracker.model.*;
import com.ndungutse.project_tracker.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DatabaseSeeder {

        @Bean
        CommandLineRunner initDatabase(
                        RoleRepository roleRepository,
                        UserRepository userRepository,
                        ProjectRepository projectRepository,
                        TaskRepository taskRepository,
                        PasswordEncoder passwordEncoder) {
                return args -> {
                        // Create roles if they don't exist
                        List<Role> roles = Arrays.asList(
                                        Role.builder().roleName("ADMIN").build(),
                                        Role.builder().roleName("MANAGER").build(),
                                        Role.builder().roleName("DEVELOPER").build(),
                                        Role.builder().roleName("CONTRACTOR").build());

                        roles.forEach(role -> {
                                if (!roleRepository.existsByRoleName(role.getRoleName())) {
                                        roleRepository.save(role);
                                }
                        });

                        // Get roles
                        Role adminRole = roleRepository.findByRoleName("ADMIN").orElseThrow();
                        Role managerRole = roleRepository.findByRoleName("MANAGER").orElseThrow();
                        Role developerRole = roleRepository.findByRoleName("DEVELOPER").orElseThrow();
                        Role contractorRole = roleRepository.findByRoleName("CONTRACTOR").orElseThrow();

                        // Create admin user
                        if (!userRepository.existsByEmail("dav.ndungutse@gmail.com")) {
                                User admin = User.builder()
                                                .username("admin")
                                                .email("dav.ndungutse@gmail.com")
                                                .password(passwordEncoder.encode("admin123"))
                                                .role(adminRole)
                                                .fullName("Admin User")
                                                .skills("System Administration")
                                                .build();
                                userRepository.save(admin);
                        }

                        // Create manager user
                        if (!userRepository.existsByEmail("manager@example.com")) {
                                User manager = User.builder()
                                                .username("manager")
                                                .email("manager@example.com")
                                                .password(passwordEncoder.encode("manager123"))
                                                .role(managerRole)
                                                .fullName("Project Manager")
                                                .skills("Project Management")
                                                .build();
                                userRepository.save(manager);
                        }

                        // Create developer users
                        for (int i = 1; i <= 20; i++) {
                                String email = String.format("dev%d@example.com", i);
                                if (!userRepository.existsByEmail(email)) {
                                        User developer = User.builder()
                                                        .username(String.format("dev%d", i))
                                                        .email(email)
                                                        .password(passwordEncoder.encode("dev123"))
                                                        .role(developerRole)
                                                        .fullName(String.format("Developer %d", i))
                                                        .skills("Java, Spring Boot, React")
                                                        .build();
                                        userRepository.save(developer);
                                }
                        }

                        // Create contractor users
                        for (int i = 1; i <= 5; i++) {
                                String email = String.format("contractor%d@example.com", i);
                                if (!userRepository.existsByEmail(email)) {
                                        User contractor = User.builder()
                                                        .username(String.format("contractor%d", i))
                                                        .email(email)
                                                        .password(passwordEncoder.encode("contractor123"))
                                                        .role(contractorRole)
                                                        .fullName(String.format("Contractor %d", i))
                                                        .skills("Contract Work")
                                                        .build();
                                        userRepository.save(contractor);
                                }
                        }

                        // Get manager and developers for project and task creation
                        User manager = userRepository.findByEmail("manager@example.com").orElseThrow();
                        User dev1 = userRepository.findByEmail("dev1@example.com").orElseThrow();
                        User dev2 = userRepository.findByEmail("dev2@example.com").orElseThrow();

                        // Create projects
                        Project project1 = Project.builder()
                                        .name("E-commerce Platform")
                                        .description("Build a modern e-commerce platform with Spring Boot and React")
                                        .deadline(LocalDate.now().plusMonths(3))
                                        .status(true)
                                        .build();

                        Project project2 = Project.builder()
                                        .name("Task Management System")
                                        .description("Develop a comprehensive task management system")
                                        .deadline(LocalDate.now().plusMonths(2))
                                        .status(true)
                                        .build();

                        projectRepository.save(project1);
                        projectRepository.save(project2);

                        // Create tasks for project 1
                        Task task1 = Task.builder()
                                        .title("Implement User Authentication")
                                        .description("Create secure user authentication system with JWT")
                                        .status(false)
                                        .dueDate(LocalDate.now().plusWeeks(2))
                                        .project(project1)
                                        .assignedUser(dev1)
                                        .build();

                        Task task2 = Task.builder()
                                        .title("Design Database Schema")
                                        .description("Create database schema for e-commerce platform")
                                        .status(false)
                                        .dueDate(LocalDate.now().plusWeeks(1))
                                        .project(project1)
                                        .assignedUser(dev2)
                                        .build();

                        // Create tasks for project 2
                        Task task3 = Task.builder()
                                        .title("Create Task CRUD Operations")
                                        .description("Implement CRUD operations for task management")
                                        .status(false)
                                        .dueDate(LocalDate.now().plusWeeks(2))
                                        .project(project2)
                                        .assignedUser(dev1)
                                        .build();

                        Task task4 = Task.builder()
                                        .title("Implement Task Assignment")
                                        .description("Create functionality to assign tasks to users")
                                        .status(false)
                                        .dueDate(LocalDate.now().plusWeeks(1))
                                        .project(project2)
                                        .assignedUser(dev2)
                                        .build();

                        taskRepository.saveAll(Arrays.asList(task1, task2, task3, task4));
                };
        }
}