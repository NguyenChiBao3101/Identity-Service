package com.example.identityservice.Exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ForbiddenException  extends RuntimeException{
    private String message;

    public ForbiddenException(String msg) {
        super(msg);
        this.message = msg;
    }
}
