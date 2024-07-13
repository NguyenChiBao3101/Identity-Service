package com.example.identityservice.dto.response;

import com.example.identityservice.entity.Role;
import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String firstName;
    private String lastName;
    private String username;
    private Set<RoleResponse> roles;
    private String email;
}
