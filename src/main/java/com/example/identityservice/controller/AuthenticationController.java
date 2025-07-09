package com.example.identityservice.controller;

import com.example.identityservice.dto.request.AuthenticationRequest;
import com.example.identityservice.dto.request.IntrospectRequest;
import com.example.identityservice.dto.request.LogoutRequest;
import com.example.identityservice.dto.request.RefreshRequest;
import com.example.identityservice.dto.response.AuthenticationResponse;
import com.example.identityservice.dto.response.IntrospectResponse;
import com.example.identityservice.dto.response.LogoutResponse;
import com.example.identityservice.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs quản lý xác thực người dùng")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/log-in")
    @Operation(
            summary = "Đăng nhập người dùng",
            description = "Xác thực người dùng và trả về JWT token"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Đăng nhập thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class),
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = """
                                    {
                                        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                        "authenticated": true
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Thông tin đăng nhập không hợp lệ",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Error Response",
                                    value = """
                    {
                        "message": "Invalid username or password",
                        "status": 401
                    }
                    """
                            )
                    )
            )
    })
    public ResponseEntity<AuthenticationResponse> authenticated(
            @Parameter(description = "Thông tin đăng nhập", required = true)
            @RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authenticationService.authenticated(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/introspect")
    @Operation(
            summary = "Kiểm tra tính hợp lệ của token",
            description = "Xác minh JWT token và trả về thông tin người dùng"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token hợp lệ",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IntrospectResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token không hợp lệ hoặc đã hết hạn"
            )
    })
    public ResponseEntity<IntrospectResponse> introspected(
            @Parameter(description = "Token cần kiểm tra", required = true)
            @RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        IntrospectResponse response = authenticationService.introspected(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Làm mới token",
            description = "Tạo JWT token mới từ refresh token"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token mới được tạo thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Refresh token không hợp lệ hoặc đã hết hạn"
            )
    })
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Parameter(description = "Refresh token", required = true)
            @RequestBody RefreshRequest request) throws ParseException, JOSEException {
        AuthenticationResponse response = authenticationService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Đăng xuất người dùng",
            description = "Vô hiệu hóa token và đăng xuất người dùng"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Đăng xuất thành công",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = "true"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Token không hợp lệ"
            )
    })
    public ResponseEntity<Boolean> logout(
            @Parameter(description = "Token cần vô hiệu hóa", required = true)
            @RequestBody LogoutRequest request) throws ParseException, JOSEException {
        boolean result = authenticationService.logout(request);
        return ResponseEntity.ok(result);
    }
}