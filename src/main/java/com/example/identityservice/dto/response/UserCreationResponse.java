package com.example.identityservice.dto.response;

import com.example.identityservice.entity.Role;
import jakarta.persistence.ManyToMany;

import java.util.Set;
import lombok.*;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationResponse {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private Set<Role> roles;

}
