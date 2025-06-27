package com.ndungutse.project_tracker.security;

import com.ndungutse.project_tracker.model.Task;
import com.ndungutse.project_tracker.model.User;
import com.ndungutse.project_tracker.repository.TaskRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private final TaskRepository taskRepository;

    public SecurityUtil(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Get the currently authenticated user
     * 
     * @return The authenticated user
     * @throws AccessDeniedException if no user is authenticated
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }
        return ((CustomUserDetails) authentication.getPrincipal()).getUser();
    }

    /**
     * Check if the current user is assigned to a task
     * 
     * @param taskId The ID of the task to check
     * @return true if the user is assigned to the task, false otherwise
     * @throws AccessDeniedException if the task doesn't exist
     */
    public boolean isUserAssignedToTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AccessDeniedException("Task not found"));

        User currentUser = getCurrentUser();
        return task.getAssignedUser() != null &&
                task.getAssignedUser().getId().equals(currentUser.getId());
    }

    /**
     * Check if the current user has a specific role
     * 
     * @param role The role to check for
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }

    /**
     * Validate that the current user can update a task
     * 
     * @param taskId The ID of the task to validate
     * @throws AccessDeniedException if the user cannot update the task
     */
    public void validateTaskUpdateAccess(Long taskId) {
        // Admin and Manager can update any task
        if (hasRole("ADMIN") || hasRole("MANAGER") || hasRole("CONTRACTOR")) {
            // Other roles cannot update tasks
            throw new AccessDeniedException("You don't have permission to update this task");
        }

        // Developers can only update tasks assigned to them
        if (hasRole("DEVELOPER") && !isUserAssignedToTask(taskId)) {
            throw new AccessDeniedException("You can only update tasks assigned to you");
        }
    }
}