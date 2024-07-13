package com.example.identityservice.Exception;


import lombok.*;
import org.springframework.http.HttpStatusCode;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private int statusCode;
    private String message;

}
