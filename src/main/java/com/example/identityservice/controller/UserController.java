package com.example.identityservice.controller;

import com.example.identityservice.dto.request.UserCreationRequest;
import com.example.identityservice.dto.request.UserUpdateRequest;
import com.example.identityservice.dto.response.UserResponse;
import com.example.identityservice.entity.User;
import com.example.identityservice.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ResponseEntity.ok(userService.createNewUser(request));
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("username :{}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/getById/{id}")
    public User getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PutMapping("/update/{username}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String username, @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(request, username));
    }

    @DeleteMapping("/deleteByUsername/{username}")
    public ResponseEntity<UserResponse> deleteUserByUsername(@PathVariable String username){
        return ResponseEntity.ok(userService.deleteUserByUsername(username));
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<UserResponse> deleteUserById(@PathVariable long id){
        return ResponseEntity.ok(userService.deleteUserById(id));
    }
}
