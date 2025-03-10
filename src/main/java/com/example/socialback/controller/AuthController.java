package com.example.socialback.controller;

import com.example.socialback.dto.LoginRequest;
import com.example.socialback.dto.RegisterRequest;
import com.example.socialback.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        logger.debug("Received registration request: {}", request);
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(
            @RequestHeader(value = "Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        return authService.getCurrentUser(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().body("Logged out successfully");
    }
}