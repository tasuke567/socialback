package com.example.socialback.service;

import com.example.socialback.dto.LoginRequest;
import com.example.socialback.dto.RegisterRequest;
import com.example.socialback.entity.UserEntity;
import com.example.socialback.repository.UserRepository;
import com.example.socialback.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;  // JPA Repo ของ UserEntity

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<?> register(RegisterRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new RuntimeException("Email is already in use.");
        });

        // สร้าง UserEntity แทน
        UserEntity newUser = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .roles("ROLE_USER")
                .build();

        UserEntity savedUser = userRepository.save(newUser);

        // Create UserDetails from savedUser
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                savedUser.getUsername(), 
                savedUser.getPassword(), 
                new ArrayList<>() // Add authorities if needed
        );

        // Generate token using UserDetails
        String token = jwtUtil.generateToken(userDetails, savedUser.getId().toString());
        return ResponseEntity.ok(Map.of("token", token));
    }

    public ResponseEntity<?> login(LoginRequest request) {
        // หาใน DB
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the user has the required role (e.g., "ROLE_USER")
        if (!user.getRoles().contains("ROLE_USER")) { // Assuming roles are stored as a List<String>
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Access denied"));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Create UserDetails from user
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    user.getUsername(), 
                    user.getPassword(), 
                    new ArrayList<>() // Add authorities if needed
            );

            // Generate token using UserDetails
            String token = jwtUtil.generateToken(userDetails, user.getId().toString());
            return ResponseEntity.ok(Map.of("token", token));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Bad credentials"));
        }
    }

    public ResponseEntity<?> getCurrentUser(String token) {
        // Validate the token and extract user information
        String username = jwtUtil.extractUsername(token); // Assuming you have a JwtUtil for token handling
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(user);
    }

    public ResponseEntity<UserEntity> getUserId(String token) {
        String userId = jwtUtil.extractUserId(token); 
        UserEntity user = userRepository.findById(UUID.fromString(userId))
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    public String getToken(UserDetails userDetails) {
        // Assuming you have a way to get the token from the SecurityContext or another source
        // This is a placeholder; you may need to adjust based on your actual implementation
        return SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
    }
}
