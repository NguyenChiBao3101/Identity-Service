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
            // Tạo sẵn các role enum nếu chưa có trong DB
            for (Roles roleEnum : Roles.values()) {
                roleRepository.findById(roleEnum.name()).orElseGet(() -> {
                    Role role = new Role();
                    role.setName(roleEnum.name());
                    role.setDescription(roleEnum.name() + " role");
                    log.info("Created role: {}", roleEnum.name());
                    return roleRepository.save(role);
                });
            }

            // Tạo user admin nếu chưa có
            if (userRepository.findByUsername("admin").isEmpty()) {
                // Lấy role từ enum -> tạo đối tượng mới, không phụ thuộc DB
                Role adminRole = Role.builder()
                        .name(Roles.ADMIN.name())
                        .description("Administrator role")
                        .build();

                Set<Role> roles = Set.of(adminRole);

                User user = User.builder()
                        .username("admin")
                        .email("admin@gmail.com")
                        .firstName("bao")
                        .lastName("nguyen")
                        .password(passwordEncoder.encode("admin"))
                        .roles(roles)
                        .build();
                userRepository.save(user);
                log.warn("Admin account created with default password: 'admin'");
            }
        };
    }
}
