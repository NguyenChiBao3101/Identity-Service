package com.example.identityservice.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.identityservice.Exception.NotFoundException;
import com.example.identityservice.dto.request.RoleRequest;
import com.example.identityservice.dto.response.RoleResponse;
import com.example.identityservice.entity.Role;
import com.example.identityservice.mapper.RoleMapper;
import com.example.identityservice.repository.PermissionRepository;
import com.example.identityservice.repository.RoleRepository;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionRepository permissionRepository;
    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse createRole(RoleRequest request) {
        var role = roleMapper.toRole(request);

        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse updateRole(String name, RoleRequest request) {
        Role role = roleRepository.findById(name).orElseThrow(() -> new NotFoundException("Role not found"));
        
        // Cập nhật description
        role.setDescription(request.getDescription());
        
        // Cập nhật permissions
        if (request.getPermissions() != null) {
            var permissions = permissionRepository.findAllById(request.getPermissions());
            role.setPermissions(new HashSet<>(permissions));
        }

        // Lưu lại role đã cập nhật
        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }
}
