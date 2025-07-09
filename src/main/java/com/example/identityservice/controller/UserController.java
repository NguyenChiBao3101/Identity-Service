package com.example.identityservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.identityservice.dto.request.UserCreationRequest;
import com.example.identityservice.dto.request.UserRoleUpdateRequest;
import com.example.identityservice.dto.request.UserUpdateRequest;
import com.example.identityservice.dto.response.UserCreationResponse;
import com.example.identityservice.dto.response.UserResponse;
import com.example.identityservice.dto.response.UserUpdateResponse;
import com.example.identityservice.entity.User;
import com.example.identityservice.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
@Tag(name = "User Management", description = "APIs quản lý người dùng")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Operation(
        summary = "Đăng ký người dùng mới",
        description = "Tạo tài khoản người dùng mới trong hệ thống"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Đăng ký thành công",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserCreationResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Dữ liệu đầu vào không hợp lệ"
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Username đã tồn tại"
        )
    })
    public ResponseEntity<UserCreationResponse> createUser(
            @Parameter(description = "Thông tin người dùng mới", required = true)
            @RequestBody @Valid UserCreationRequest request) {
        return ResponseEntity.ok(userService.createNewUser(request));
    }

    @GetMapping("/all")
    @Operation(
        summary = "Lấy danh sách tất cả người dùng",
        description = "Trả về danh sách tất cả người dùng trong hệ thống (yêu cầu quyền ADMIN)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lấy danh sách thành công",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
            )
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Không có quyền truy cập"
        )
    })
    public ResponseEntity<List<User>> getAllUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/getById/{id}")
    @Operation(
        summary = "Lấy thông tin người dùng theo ID",
        description = "Trả về thông tin chi tiết của người dùng dựa trên ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lấy thông tin thành công",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Không tìm thấy người dùng"
        )
    })
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID của người dùng", required = true)
            @PathVariable long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{username}")
    @Operation(
        summary = "Lấy thông tin người dùng theo username",
        description = "Trả về thông tin chi tiết của người dùng dựa trên username"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lấy thông tin thành công",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Không tìm thấy người dùng"
        )
    })
    public ResponseEntity<UserResponse> getUserByUsername(
            @Parameter(description = "Username của người dùng", required = true)
            @PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PutMapping("/update/{username}")
    @Operation(
        summary = "Cập nhật thông tin người dùng",
        description = "Cập nhật thông tin cá nhân của người dùng"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Cập nhật thành công",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserUpdateResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Không tìm thấy người dùng"
        )
    })
    public ResponseEntity<UserUpdateResponse> updateUser(
            @Parameter(description = "Username của người dùng", required = true)
            @PathVariable String username,
            @Parameter(description = "Thông tin cập nhật", required = true)
            @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(request, username));
    }

    @PutMapping("/updateRole/{username}")
    @Operation(
        summary = "Cập nhật vai trò người dùng",
        description = "Thay đổi vai trò của người dùng (yêu cầu quyền ADMIN)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Cập nhật vai trò thành công",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Không có quyền thay đổi vai trò"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Không tìm thấy người dùng"
        )
    })
    public ResponseEntity<UserResponse> updateRoleUser(
            @Parameter(description = "Username của người dùng", required = true)
            @PathVariable String username,
            @Parameter(description = "Danh sách vai trò mới", required = true)
            @RequestBody UserRoleUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUserRoles(username, request));
    }

    @DeleteMapping("/deleteByUsername/{username}")
    @Operation(
        summary = "Xóa người dùng theo username",
        description = "Xóa người dùng khỏi hệ thống dựa trên username"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Xóa thành công",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Không tìm thấy người dùng"
        )
    })
    public ResponseEntity<UserResponse> deleteUserByUsername(
            @Parameter(description = "Username của người dùng cần xóa", required = true)
            @PathVariable String username){
        return ResponseEntity.ok(userService.deleteUserByUsername(username));
    }

    @DeleteMapping("/deleteById/{id}")
    @Operation(
        summary = "Xóa người dùng theo ID",
        description = "Xóa người dùng khỏi hệ thống dựa trên ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Xóa thành công",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Không tìm thấy người dùng"
        )
    })
    public ResponseEntity<UserResponse> deleteUserById(
            @Parameter(description = "ID của người dùng cần xóa", required = true)
            @PathVariable long id){
        return ResponseEntity.ok(userService.deleteUserById(id));
    }
}
