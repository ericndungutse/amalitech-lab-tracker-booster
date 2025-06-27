package com.ndungutse.project_tracker.dto;

import com.ndungutse.project_tracker.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Long roleId;
    private String roleName;
    private String skills;
    private String fullName;

    // Exclude password for security reasons
    // Convert Entity to DTO
    public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roleId(user.getRole() != null ? user.getRole().getId() : null)
                .roleName(user.getRole() != null ? user.getRole().getRoleName() : null)
                .skills(user.getSkills())
                .fullName(user.getFullName())
                .build();
    }

    // Convert DTO to Entity
    public User toEntity() {
        return User.builder()
                .id(this.id)
                .username(this.username)
                .email(this.email)
                .skills(this.skills)
                .fullName(this.fullName)
                .build();
    }
}