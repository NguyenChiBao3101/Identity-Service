package com.example.identityservice.controller;

import com.example.identityservice.dto.request.PermissionRequest;
import com.example.identityservice.dto.request.RoleRequest;
import com.example.identityservice.dto.response.PermissionResponse;
import com.example.identityservice.dto.response.RoleResponse;
import com.example.identityservice.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService service;

    @PostMapping("/add")
    public ResponseEntity<RoleResponse> createRole(@RequestBody RoleRequest request) {
        return ResponseEntity.ok(service.createRole(request));
    }

    @GetMapping("/all")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {

        return  ResponseEntity.ok(service.getAllRoles());
    }
    @PutMapping("/update/{name}")
    public ResponseEntity<RoleResponse> updateRole(
            @PathVariable String name,
            @RequestBody RoleRequest request) {
        return ResponseEntity.ok(service.updateRole(name, request));
    }
}
