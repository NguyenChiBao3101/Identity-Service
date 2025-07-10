package com.example.identityservice.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    //List<String> roles;         //gây lỗi( sửa -> comment)
}
