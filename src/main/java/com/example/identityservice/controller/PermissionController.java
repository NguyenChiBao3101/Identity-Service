package com.example.identityservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.identityservice.dto.request.PermissionRequest;
import com.example.identityservice.dto.response.PermissionResponse;
import com.example.identityservice.service.PermissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/permission")
@Tag(name = "Permission Management", description = "APIs quản lý quyền hạn")
@SecurityRequirement(name = "bearerAuth")
public class PermissionController {

    @Autowired
    private PermissionService service;

    @PostMapping("/add")
    @Operation(
        summary = "Tạo quyền hạn mới",
        description = "Tạo một quyền hạn mới trong hệ thống"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Tạo quyền hạn thành công",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PermissionResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Dữ liệu đầu vào không hợp lệ"
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Tên quyền hạn đã tồn tại"
        )
    })
    public ResponseEntity<PermissionResponse> create(
            @Parameter(description = "Thông tin quyền hạn mới", required = true)
            @RequestBody PermissionRequest request) {
        return ResponseEntity.ok(service.createPermission(request));
    }

    @GetMapping("/all")
    @Operation(
        summary = "Lấy danh sách tất cả quyền hạn",
        description = "Trả về danh sách tất cả quyền hạn trong hệ thống"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lấy danh sách thành công",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PermissionResponse.class)
            )
        )
    })
    public ResponseEntity<List<PermissionResponse>> getAllPermission() {
        return ResponseEntity.ok(service.getAllPermission());
    }
}