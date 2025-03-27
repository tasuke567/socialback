package com.example.socialback.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime; // In milliseconds

    private SecretKey signingKey;

    @jakarta.annotation.PostConstruct
    public void init() {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("JWT secret key is not configured properly!");
        }

        try {
            byte[] keyBytes = Base64.getDecoder().decode(secretKey);

            if (keyBytes.length < 32) {
                throw new IllegalArgumentException("JWT secret key must be at least 32 bytes long after decoding!");
            }

            signingKey = Keys.hmacShaKeyFor(keyBytes);
            logger.info("✅ Successfully loaded JWT Secret Key ({} bytes)", keyBytes.length);

        } catch (IllegalArgumentException e) {
            logger.error("❌ Invalid Base64 encoding for JWT secret key: {}", secretKey);
            throw new IllegalStateException("JWT secret key must be Base64 encoded and at least 32 bytes long!");
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            logger.error("Failed to extract claims from JWT: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username, String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        String token = createToken(claims, username);

        logger.info("🔑 Generated JWT for {}: {}", username, token.substring(0, 15) + "... (truncated)");
        return token;
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException e) {
            logger.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }
}
