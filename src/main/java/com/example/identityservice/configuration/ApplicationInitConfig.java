package com.example.identityservice.configuration;

import java.util.HashSet;
import java.util.Set;

import com.example.identityservice.entity.Role;
import com.example.identityservice.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.identityservice.entity.User;
import com.example.identityservice.enums.Roles;
import com.example.identityservice.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ApplicationInitConfig {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                Role adminRole = roleRepository.findById("ADMIN").orElseThrow(() -> new RuntimeException("ADMIN role is invalid"));
                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(roles)
                        .build();
                userRepository.save(user);
                System.out.println("User's role:" + user.getRoles());
                log.warn("Admin account has been created with default password");
            }
        };
    }
}
