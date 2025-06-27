package com.ndungutse.project_tracker.dto;

import com.ndungutse.project_tracker.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private Long id;
    private String roleName;

    // Convert Entity to DTO
    public static RoleDTO fromEntity(Role role) {
        return new RoleDTO(
                role.getId(),
                role.getRoleName());
    }

    // Convert DTO to Entity
    public Role toEntity() {
        return Role.builder()
                .id(this.id)
                .roleName(this.roleName)
                .build();
    }
}