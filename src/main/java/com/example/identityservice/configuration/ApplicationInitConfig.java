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
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            // Tạo các role enum nếu chưa có trong DB
            for (Roles roleEnum : Roles.values()) {
                roleRepository.findById(roleEnum.name()).orElseGet(() -> {
                    Role role = new Role();
                    role.setName(roleEnum.name());
                    role.setDescription(roleEnum.name() + " role");
                    log.info("Created role: {}", roleEnum.name());
                    return roleRepository.save(role);
                });
            }

            // Lấy role từ DB
            Role adminRole = roleRepository.findById(Roles.ADMIN.name())
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
            Role clientRole = roleRepository.findById(Roles.USER.name())
                    .orElseThrow(() -> new RuntimeException("CLIENT role not found"));

            Set<Role> adminRoles = Set.of(adminRole);
            Set<Role> clientRoles = Set.of(clientRole);

            // Danh sách user cần tạo
            User[] defaultUsers = new User[] {
                    User.builder()
                            .username("admin")
                            .email("admin@gmail.com")
                            .firstName("Bao")
                            .lastName("Nguyen")
                            .password(passwordEncoder.encode("admin"))
                            .roles(adminRoles)
                            .build(),
                    User.builder()
                            .username("user1")
                            .email("user1@gmail.com")
                            .firstName("Alice")
                            .lastName("Tran")
                            .password(passwordEncoder.encode("user123"))
                            .roles(clientRoles)
                            .build(),
                    User.builder()
                            .username("user2")
                            .email("user2@gmail.com")
                            .firstName("Bob")
                            .lastName("Le")
                            .password(passwordEncoder.encode("user123"))
                            .roles(clientRoles)
                            .build(),
                    User.builder()
                            .username("user3")
                            .email("user3@gmail.com")
                            .firstName("Carol")
                            .lastName("Pham")
                            .password(passwordEncoder.encode("user123"))
                            .roles(clientRoles)
                            .build(),
                    User.builder()
                            .username("user4")
                            .email("user4@gmail.com")
                            .firstName("David")
                            .lastName("Nguyen")
                            .password(passwordEncoder.encode("user123"))
                            .roles(clientRoles)
                            .build(),
                    User.builder()
                            .username("user5")
                            .email("user5@gmail.com")
                            .firstName("Eva")
                            .lastName("Vo")
                            .password(passwordEncoder.encode("user123"))
                            .roles(clientRoles)
                            .build()
            };

            // Lưu vào DB nếu username chưa tồn tại
            for (User user : defaultUsers) {
                if (userRepository.findByUsername(user.getUsername()).isEmpty()) {
                    userRepository.save(user);
                    log.info("Created user: {}", user.getUsername());
                }
            }

            log.warn("Default admin & users created if not present.");
        };
    }

}
