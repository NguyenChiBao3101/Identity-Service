package com.example.identityservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.identityservice.dto.request.RoleRequest;
import com.example.identityservice.dto.response.RoleResponse;
import com.example.identityservice.service.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/role")
@Tag(name = "Role Management", description = "APIs quản lý vai trò người dùng")
@SecurityRequirement(name = "bearerAuth")
public class RoleController {
    @Autowired
    private RoleService service;

    @PostMapping("/add")
    @Operation(
        summary = "Tạo vai trò mới",
        description = "Tạo một vai trò mới trong hệ thống"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Tạo vai trò thành công",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RoleResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Dữ liệu đầu vào không hợp lệ"
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Tên vai trò đã tồn tại"
        )
    })
    public ResponseEntity<RoleResponse> createRole(
            @Parameter(description = "Thông tin vai trò mới", required = true)
            @RequestBody RoleRequest request) {
        return ResponseEntity.ok(service.createRole(request));
    }

    @GetMapping("/all")
    @Operation(
        summary = "Lấy danh sách tất cả vai trò",
        description = "Trả về danh sách tất cả vai trò trong hệ thống"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lấy danh sách thành công",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RoleResponse.class)
            )
        )
    })
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(service.getAllRoles());
    }

    @PutMapping("/update/{name}")
    @Operation(
        summary = "Cập nhật vai trò",
        description = "Cập nhật thông tin của một vai trò"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Cập nhật thành công",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RoleResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Không tìm thấy vai trò"
        )
    })
    public ResponseEntity<RoleResponse> updateRole(
            @Parameter(description = "Tên vai trò cần cập nhật", required = true)
            @PathVariable String name,
            @Parameter(description = "Thông tin cập nhật", required = true)
            @RequestBody RoleRequest request) {
        return ResponseEntity.ok(service.updateRole(name, request));
    }
}
