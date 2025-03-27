package com.example.socialback.service;

import com.example.socialback.model.dao.AuthDAO;
import com.example.socialback.model.dto.AuthResponse;
import com.example.socialback.model.dto.LoginRequest;
import com.example.socialback.model.dto.RegisterRequest;
import com.example.socialback.model.entity.UserEntity;
import com.example.socialback.security.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import com.example.socialback.model.dao.TokenRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.socialback.model.dao.UserInterestDAO;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthDAO authDAO;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final UserService userService;
    private final UserInterestDAO userInterestDAO;

    public ResponseEntity<?> register(RegisterRequest request) {
        if (authDAO.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username already taken!");
        }

        UserEntity user = new UserEntity(
                UUID.randomUUID(),
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFirstName(),
                request.getLastName() );
        authDAO.saveUser(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getId().toString());
        return ResponseEntity.ok(new AuthResponse(token, "User registered successfully"));
    }

    

    public ResponseEntity<?> getCurrentUser(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            UserEntity user = authDAO.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body("Invalid token or user not found");
            }
            return ResponseEntity.ok(user);
        } catch (ExpiredJwtException e) {
            // Token is expired
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body("Token expired");
        } catch (JwtException e) {
            // Token is invalid
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body("Invalid token");
        } catch (Exception e) {
            // Other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("An error occurred while processing the token");
        }
    }
    

    public void logout(String username) {
        tokenRepository.deleteByUsername(username);
    }

    public ResponseEntity<?> login(LoginRequest request) {
        // สมมุติว่าได้ตรวจสอบ username/password แล้ว
        UserEntity user = userService.findByUsername(request.getUsername());
        if (user == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        // สร้าง JWT token โดยส่ง username และ userId
        String token = jwtUtil.generateToken(user.getUsername(), user.getId().toString());

        // ตรวจสอบข้อมูลในตาราง user_interests โดยใช้ DAO
        boolean hasInterests = !userInterestDAO.findByUserId(user.getId()).isEmpty();

        // รวมข้อมูล response ส่งกลับไปยัง client
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("token", token);
        responseBody.put("hasInterests", hasInterests);

        return ResponseEntity.ok(responseBody);
    }

    public boolean checkUserInterests(String username) {
        // ดึงข้อมูลผู้ใช้จากฐานข้อมูลโดยใช้ username
        UserEntity user = authDAO.findByUsername(username);
        if (user == null) {
            // ถ้าไม่พบผู้ใช้ คุณอาจเลือกที่จะส่ง false หรือโยน exception ก็ได้
            return false;
        }
        // ตรวจสอบว่าผู้ใช้มีความสนใจหรือไม่ โดยดูว่าผลลัพธ์ของ DAO ว่างหรือไม่
        return !userInterestDAO.findByUserId(user.getId()).isEmpty();
    }

    
    
}
