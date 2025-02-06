package com.example.socialback.service;

import com.example.socialback.dto.LoginRequest;
import com.example.socialback.dto.RegisterRequest;
import com.example.socialback.model.User;
import com.example.socialback.repository.UserRepository;
import com.example.socialback.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // Register a new user
    public ResponseEntity<?> register(RegisterRequest request) {
        // Check if email is already in use
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new RuntimeException("Email is already in use.");
        });

        // Create a new User instance and populate its fields
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setCreatedAt(new Date());
        newUser.setUpdatedAt(new Date());

        User savedUser = userRepository.save(newUser);

// ตรวจสอบว่า ID ถูกกำหนด
        if (savedUser.getId() == null) {
            throw new RuntimeException("User ID is not generated");
        }

        String token = jwtUtil.generateToken(
                (UserDetails) userRepository.findByUsername(savedUser.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found")),
                savedUser.getId().toString()
        );

        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", savedUser.getUsername(),
                "email", savedUser.getEmail()
        ));

    }

    // Login user
    public ResponseEntity<?> login(LoginRequest request) {
        try {
            // Check if user exists by username
            Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "User not found"));
            }

            // Authenticate user using the provided credentials
            Authentication authentication = authenticateUser(request.getUsername(), request.getPassword());
            // Generate token for authenticated user
            String token = generateTokenForUser(authentication);

            return ResponseEntity.ok(Map.of("token", token));

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Bad credentials"));
        }
    }

    // Authenticate user with username and password
    private Authentication authenticateUser(String username, String password) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
    }

    // Generate JWT token for an authenticated user
    private String generateTokenForUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getId() == null) {
            throw new RuntimeException("User ID is null");
        }

        return jwtUtil.generateToken(userDetails, user.getId().toString());
    }


    // Retrieve the current logged-in user from a given token
    public ResponseEntity<?> getCurrentUser(String token) {
        try {
            String email = jwtUtil.extractUsername(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or expired token"));
        }
    }

    // Logout user by clearing the security context
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }
}
