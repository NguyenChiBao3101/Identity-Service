package com.example.identityservice.service;

import com.example.identityservice.Exception.NotFoundException;
import com.example.identityservice.dto.request.UserCreationRequest;
import com.example.identityservice.dto.request.UserUpdateRequest;
import com.example.identityservice.dto.response.UserResponse;
import com.example.identityservice.entity.User;
import com.example.identityservice.enums.Roles;
import com.example.identityservice.mapper.UserMapper;
import com.example.identityservice.repository.RoleRepository;
import com.example.identityservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //create new user
    public UserResponse createNewUser(UserCreationRequest request) {
        if(userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is existed");
        }
        User user = userMapper.toUser(request);
        // encrypt password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // set roles
        HashSet<String> roles = new HashSet<>();
        roles.add(Roles.USER.name());
        //user.setRoles(roles);
        return  userMapper.toUserResponse(userRepository.save(user));
    }

    //get all users
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        log.info("Access getAllUsers method successfully!");
        return userRepository.findAll();
    }

    //get user by userId

    @PostAuthorize("returnObject.username == authentication.name")
    public User getUserById(long id) {
        log.info("Access getUserById method successfully!!");
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        return user;
    }

    //get user by username
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUserByUsername(String username) {
        log.info("Access getUserByUsername method successfully!!");
        return userMapper.toUserResponse(userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found")));
    }

    //update user by username
    //@PostAuthorize("returnObject.username == authentication.name")
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

    //delete user
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        userRepository.delete(user);
        return userMapper.toUserResponse(user);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse deleteUserById(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        userRepository.delete(user);
        return userMapper.toUserResponse(user);
    }

}
