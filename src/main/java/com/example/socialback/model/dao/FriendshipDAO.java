package com.example.socialback.model.dao;

import com.example.socialback.model.dto.FriendshipDTO;
import com.example.socialback.model.entity.FriendshipStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class FriendshipDAO {
    private final JdbcTemplate jdbcTemplate;

    public FriendshipDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createFriendship(FriendshipDTO friendship) {
        String sql = "INSERT INTO friendships (id, user_id, friend_id, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                friendship.getId(), friendship.getUserId(), friendship.getFriendId(),
                friendship.getStatus(), friendship.getCreatedAt(), friendship.getUpdatedAt());
    }

    public List<FriendshipDTO> getFriendsByUserId(UUID userId) {
        String sql = "SELECT * FROM friendships WHERE user_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToFriendship, userId);
    }

    private FriendshipDTO mapRowToFriendship(ResultSet rs, int rowNum) throws SQLException {
        return new FriendshipDTO(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("user_id")),
                UUID.fromString(rs.getString("friend_id")),
                FriendshipStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}
