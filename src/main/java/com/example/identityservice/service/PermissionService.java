package com.example.identityservice.service;

import com.example.identityservice.dto.request.PermissionRequest;
import com.example.identityservice.dto.response.PermissionResponse;
import com.example.identityservice.entity.Permission;
import com.example.identityservice.mapper.PermissionMapper;
import com.example.identityservice.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    private final PermissionMapper mapper;
    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = mapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return mapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAllPermission() {
        var permissions = permissionRepository.findAll();
         return permissions.stream().map(mapper::toPermissionResponse).toList();
    }

    public void deletePermission(String permission) {
        permissionRepository.deleteById(permission);
    }

}
