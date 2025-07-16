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
            // Tạo role ADMIN nếu chưa có
            if (roleRepository.findById(Roles.ADMIN.name()).isEmpty()) {
                Role adminRole = new Role();
                adminRole.setName(Roles.ADMIN.name());
                adminRole.setDescription("Administrator role");
                roleRepository.save(adminRole);
                log.info("Created ADMIN role");
            }

            // Tạo user admin nếu chưa có
            if (userRepository.findByUsername("admin").isEmpty()) {
                Role userRole = roleRepository.findById(Roles.ADMIN.name())
                        .orElseThrow(() -> new RuntimeException("Default role ADMIN not found"));
                Set<Role> roles = new HashSet<>();
                roles.add(userRole);
                User user = User.builder()
                        .username("admin")
                        .email("admin@gmail.com")
                        .firstName("bao")
                        .lastName("nguyen")
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
