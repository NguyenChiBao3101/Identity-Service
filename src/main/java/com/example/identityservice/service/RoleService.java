package com.example.identityservice.service;

import com.example.identityservice.dto.request.RoleRequest;
import com.example.identityservice.dto.response.RoleResponse;
import com.example.identityservice.mapper.RoleMapper;
import com.example.identityservice.repository.PermissionRepository;
import com.example.identityservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleMapper mapper;

    @Autowired
    private PermissionRepository permissionRepository;

    public RoleResponse createRole(RoleRequest request) {
        var role = mapper.toRole(request);

        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        role = roleRepository.save(role);
        return mapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(mapper::toRoleResponse)
                .toList();
    }

    public void deleteRole(String role) {
        roleRepository.deleteById(role);
    }

}
