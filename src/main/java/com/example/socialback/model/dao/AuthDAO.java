package com.example.socialback.model.dao;

import com.example.socialback.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AuthDAO {
    private final JdbcTemplate jdbcTemplate;

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    public void saveUser(UserEntity user) {
        String sql = "INSERT INTO users (id, username, password, email, first_name, last_name, created_at, updated_at, account_non_expired, account_non_locked, credentials_non_expired, enabled, roles) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, 
            user.getId(), 
            user.getUsername(), 
            user.getPassword(), 
            user.getEmail(),
            user.getFirstName(),   
            user.getLastName(),    
            user.getCreatedAt(),   
            user.getUpdatedAt(),   
            user.isAccountNonExpired(),
            user.isAccountNonLocked(),
            user.isCredentialsNonExpired(),
            user.isEnabled(),
            user.getRoles());
    }

    public UserEntity findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> UserEntity.builder()
                .id(rs.getObject("id", UUID.class))
                .username(rs.getString("username"))
                .password(rs.getString("password"))
                .email(rs.getString("email"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .accountNonExpired(rs.getBoolean("account_non_expired"))
                .accountNonLocked(rs.getBoolean("account_non_locked"))
                .credentialsNonExpired(rs.getBoolean("credentials_non_expired"))
                .enabled(rs.getBoolean("enabled"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .roles(rs.getString("roles"))
                .build(), 
            username);
    }

    public UserEntity findById(UUID id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> UserEntity.builder()
                .id(rs.getObject("id", UUID.class))
                .username(rs.getString("username"))
                .password(rs.getString("password"))
                .email(rs.getString("email"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .accountNonExpired(rs.getBoolean("account_non_expired"))
                .accountNonLocked(rs.getBoolean("account_non_locked"))
                .credentialsNonExpired(rs.getBoolean("credentials_non_expired"))
                .enabled(rs.getBoolean("enabled"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .roles(rs.getString("roles"))
                .build(),
            id);
    }
}
