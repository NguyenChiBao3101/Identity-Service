package com.example.identityservice.dto.request;
import java.util.Set;
import lombok.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleUpdateRequest {
    private Set<String> roles;
}
