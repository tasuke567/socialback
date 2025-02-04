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
import java.util.List;
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


    // อาจมี dependency เช่น UserRepository, PasswordEncoder, JwtProvider เป็นต้น

    public ResponseEntity<?> register(RegisterRequest request) {
        // ตรวจสอบว่ามีผู้ใช้อยู่แล้วหรือไม่
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email is already in use.");
        }

        // สร้างผู้ใช้ใหม่ (อาจรวมถึงการเข้ารหัส password)
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setCreatedAt(new Date());
        newUser.setUpdatedAt(new Date());
        // บันทึกผู้ใช้ใหม่
        User savedUser = userRepository.save(newUser);

        // หากต้องการให้ล็อกอินอัตโนมัติ ให้สร้าง token
        String token = jwtUtil.generateToken(
                (UserDetails) userRepository.findByUsername(savedUser.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found")),
                savedUser.getId().toString()
        );

        // สร้าง response DTO ที่จะส่งกลับ
        RegisterRequest authResponse = new RegisterRequest(token, savedUser.getUsername(), savedUser.getEmail());
        return ResponseEntity.ok(authResponse);
    }


    // Login user
    public ResponseEntity<?> login(LoginRequest request) {
        try {
            Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "User not found"));
            }

            Authentication authentication = authenticateUser(request.getUsername(), request.getPassword());
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

    // Generate JWT token for authenticated user
    private String generateTokenForUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate token with userId
        return jwtUtil.generateToken(userDetails, user.getId().toString());
    }

    // Get current logged-in user
    public ResponseEntity<?> getCurrentUser(String token) {
        try {
            String email = jwtUtil.extractUsername(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired token"));
        }
    }

    // Logout user
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }
}
