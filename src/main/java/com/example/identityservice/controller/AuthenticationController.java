package com.example.identityservice.controller;

import com.example.identityservice.dto.request.AuthenticationRequest;
import com.example.identityservice.dto.request.IntrospectRequest;
import com.example.identityservice.dto.request.LogoutRequest;
import com.example.identityservice.dto.request.RefreshRequest;
import com.example.identityservice.dto.response.AuthenticationResponse;
import com.example.identityservice.dto.response.IntrospectResponse;
import com.example.identityservice.dto.response.LogoutResponse;
import com.example.identityservice.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/log-in")
    public AuthenticationResponse authenticated(@RequestBody AuthenticationRequest request){
        return authenticationService.authenticated(request);
    }

    @PostMapping("/introspect")
    public IntrospectResponse introspected (@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        return authenticationService.introspected(request);
    }

    @PostMapping("/refresh")
    public AuthenticationResponse authenticate(@RequestBody RefreshRequest request) throws ParseException, JOSEException {
        return authenticationService.refreshToken(request);
    }

    @PostMapping("/logout")
    public boolean logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
         return authenticationService.logout(request);
    }

}
