package com.example.identityservice.Exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AuthorizationDeniedException extends RuntimeException{
    private String message;

    public AuthorizationDeniedException(String message) {
        super(message);
        this.message = message;
    }
}
