package com.example.identityservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
//@Table(name ="User")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    @ManyToMany
    private Set<Role> roles;
}
