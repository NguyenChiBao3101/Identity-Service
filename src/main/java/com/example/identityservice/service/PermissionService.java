package com.example.identityservice.service;

import com.example.identityservice.dto.request.PermissionRequest;
import com.example.identityservice.dto.response.PermissionResponse;
import com.example.identityservice.entity.Permission;
import com.example.identityservice.mapper.PermissionMapper;
import com.example.identityservice.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    @Autowired
    private final PermissionRepository permissionRepository;
    @Autowired
    private final PermissionMapper mapper;
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = mapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return mapper.toPermissionResponse(permission);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<PermissionResponse> getAllPermission() {
        var permissions = permissionRepository.findAll();
         return permissions.stream().map(mapper::toPermissionResponse).toList();
    }

}
