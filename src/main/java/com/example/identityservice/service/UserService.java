package com.example.identityservice.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.identityservice.Exception.ForbiddenException;
import com.example.identityservice.dto.response.UserUpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.identityservice.Exception.NotFoundException;
import com.example.identityservice.dto.request.UserCreationRequest;
import com.example.identityservice.dto.request.UserRoleUpdateRequest;
import com.example.identityservice.dto.request.UserUpdateRequest;
import com.example.identityservice.dto.response.UserCreationResponse;
import com.example.identityservice.dto.response.UserResponse;
import com.example.identityservice.entity.Role;
import com.example.identityservice.entity.User;
import com.example.identityservice.enums.Roles;
import com.example.identityservice.mapper.UserMapper;
import com.example.identityservice.repository.InvalidatedTokenRepository;
import com.example.identityservice.repository.IssuedTokenRepository;
import com.example.identityservice.repository.RoleRepository;
import com.example.identityservice.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IssuedTokenRepository issuedTokenRepository;
    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;

    //create new user                                                               (get error)
    public UserCreationResponse createNewUser(UserCreationRequest request) {
        if(userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username have already existed");
        }
        User user = userMapper.toUser(request);
        // encrypt password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> roles = request.getRoles().stream()
                    .map(roleName -> roleRepository.findById(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        return  userMapper.toUserCreationResponse(userRepository.save(user));
    }

    //create new user                                                               (Fixed)
//    public UserCreationResponse createNewUser(UserCreationRequest request) {
//        if(userRepository.existsByUsername(request.getUsername())) {
//            throw new RuntimeException("Username is existed");
//        }
//        User user = userMapper.toUser(request);
//        // encrypt password
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//
//        // set default role: USER
//        Role userRole = roleRepository.findById(Roles.USER.name())
//                .orElseThrow(() -> new RuntimeException("Default role USER not found"));
//        Set<Role> roles = new HashSet<>();
//        roles.add(userRole);
//        user.setRoles(roles);
//        return  userMapper.toUserCreationResponse(userRepository.save(user));
//    }

    //get all users
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        System.out.println("Access getAllUsers method successfully!");
        return userRepository.findAll();
    }

    //get user by userId                                                       (get error)
    public User getUserById(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        return user;
    }

    //get user by userId                                                        (fixed)
//    @PostAuthorize("returnObject.userId == principal.getClaim('userId')")
//    public User getUserById(long id) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Jwt jwt = (Jwt) authentication.getPrincipal();
//        Long userId = jwt.getClaim("userId");

//        // Kiểm tra userId có trùng với id được yêu cầu không
//        if (!userId.equals(id)) {
//            throw new ForbiddenException("You're not allow to access this user information");
//        }
//
//        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
//        return user;
//    }

    //get user by username
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUserByUsername(String username) {
        System.out.println("Access getUserByUsername method successfully!!");
        return userMapper.toUserResponse(userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found")));
    }

    // User update theirs own info by username                                      (get error)
//    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse updateUser(UserUpdateRequest request, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        //update user
        userMapper.updateUser(user,request);
        //encrypt updated password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));
        return  userMapper.toUserResponse(userRepository.save(user));
    }

    // User update theirs own info by username (fixed)
//    @PostAuthorize("returnObject.username == authentication.name")
//    public UserUpdateResponse updateUser(UserUpdateRequest request, String username) {
//        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
//        //update user
//        userMapper.updateUser(user,request);
//        //encrypt updated password
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        return  userMapper.toUserUpdateResponse(userRepository.save(user));
//    }

    // admin update user role
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUserRoles(String username, UserRoleUpdateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
        // Lấy roles từ DB
        Set<Role> roles = request.getRoles().stream()
                .map(roleName -> roleRepository.findById(roleName)
                        .orElseThrow(() -> new NotFoundException("Role not found: " + roleName)))
                .collect(Collectors.toSet());
        user.setRoles(roles);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    //delete user
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserResponse deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        // Invalidate all tokens
        var issuedTokens = issuedTokenRepository.findByUsername(username);
        for (var token : issuedTokens) {
            invalidatedTokenRepository.save(
                com.example.identityservice.entity.InvalidatedToken.builder()
                    .id(token.getId())
                    .expiryTime(token.getExpiryTime())
                    .build()
            );
        }
        issuedTokenRepository.deleteByUsername(username);
        userRepository.delete(user);
        return userMapper.toUserResponse(user);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserResponse deleteUserById(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        // Invalidate all tokens
        var issuedTokens = issuedTokenRepository.findByUsername(user.getUsername());
        for (var token : issuedTokens) {
            invalidatedTokenRepository.save(
                com.example.identityservice.entity.InvalidatedToken.builder()
                    .id(token.getId())
                    .expiryTime(token.getExpiryTime())
                    .build()
            );
        }
        issuedTokenRepository.deleteByUsername(user.getUsername());
        userRepository.delete(user);
        return userMapper.toUserResponse(user);
    }

}
