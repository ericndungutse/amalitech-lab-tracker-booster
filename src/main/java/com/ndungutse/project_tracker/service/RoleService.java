package com.ndungutse.project_tracker.service;

import com.ndungutse.project_tracker.dto.RoleDTO;
import com.ndungutse.project_tracker.model.Role;
import com.ndungutse.project_tracker.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // Create
    public RoleDTO create(RoleDTO roleDTO) {
        Role role = roleDTO.toEntity();
        Role savedRole = roleRepository.save(role);
        return RoleDTO.fromEntity(savedRole);
    }

    // Read
    public List<RoleDTO> getAll() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(RoleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<RoleDTO> getById(Long id) {
        Optional<Role> role = roleRepository.findById(id);
        return role.map(RoleDTO::fromEntity);
    }

    public Optional<RoleDTO> getByName(String roleName) {
        Optional<Role> role = roleRepository.findByRoleName(roleName);
        return role.map(RoleDTO::fromEntity);
    }

    // Update
    @Transactional
    public Optional<RoleDTO> update(Long id, RoleDTO updatedRoleDTO) {
        Optional<Role> existingRole = roleRepository.findById(id);
        if (existingRole.isPresent()) {
            Role role = existingRole.get();

            if (updatedRoleDTO.getRoleName() != null) {
                role.setRoleName(updatedRoleDTO.getRoleName());
            }

            Role savedRole = roleRepository.save(role);
            return Optional.of(RoleDTO.fromEntity(savedRole));
        }
        return Optional.empty();
    }

    // Delete
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

    public boolean exists(Long id) {
        return roleRepository.existsById(id);
    }

    public boolean existsByName(String roleName) {
        return roleRepository.existsByRoleName(roleName);
    }
}