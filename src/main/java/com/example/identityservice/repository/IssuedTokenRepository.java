package com.example.identityservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.identityservice.entity.IssuedToken;

public interface IssuedTokenRepository extends JpaRepository<IssuedToken, String> {
    List<IssuedToken> findByUsername(String username);
    void deleteByUsername(String username);
} 