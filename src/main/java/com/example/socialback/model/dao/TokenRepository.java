package com.example.socialback.model.dao;

import com.example.socialback.model.entity.TokenEntity;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TokenRepository {
    private final String url = "jdbc:mysql://localhost:3306/Social";
    private final String user = "postgress";
    private final String password = "123456";

    // Create
    public void save(TokenEntity tokenEntity) {
        String sql = "INSERT INTO tokens (token, username) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tokenEntity.getToken());
            pstmt.setString(2, tokenEntity.getUsername());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Read
    public List<TokenEntity> findAll() {
        List<TokenEntity> tokens = new ArrayList<>();
        String sql = "SELECT * FROM tokens";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String token = rs.getString("token");
                String username = rs.getString("username");
                tokens.add(new TokenEntity(token, username));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tokens;
    }

    // Delete
    public void deleteByUsername(String username) {
        String sql = "DELETE FROM tokens WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
