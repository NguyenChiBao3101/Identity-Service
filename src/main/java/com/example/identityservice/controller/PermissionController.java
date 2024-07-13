package com.example.identityservice.controller;

import com.example.identityservice.dto.request.PermissionRequest;
import com.example.identityservice.dto.response.PermissionResponse;
import com.example.identityservice.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService service;

    @PostMapping("/add")
    public ResponseEntity<PermissionResponse> create (@RequestBody PermissionRequest request) {
        return ResponseEntity.ok(service.createPermission(request));
    }

    @GetMapping("/all")
    public ResponseEntity<List<PermissionResponse>> getAllPermission() {

        return  ResponseEntity.ok(service.getAllPermission());
    }

    @DeleteMapping("/{name}")
    public void deletePermission(@PathVariable String name) {
        service.deletePermission(name);
    }
}