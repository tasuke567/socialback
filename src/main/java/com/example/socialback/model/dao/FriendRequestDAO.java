package com.example.socialback.model.dao;

import com.example.socialback.model.dto.FriendRequestDTO;
import com.example.socialback.model.entity.FriendshipStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class FriendRequestDAO {
    private final JdbcTemplate jdbcTemplate;

    public FriendRequestDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void sendFriendRequest(FriendRequestDTO friendRequest) {
        String sql = "INSERT INTO friend_requests (id, from_user_id, to_user_id, status, created_at) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                friendRequest.getId(), friendRequest.getFromUserId(), friendRequest.getToUserId(),
                friendRequest.getStatus().name(), friendRequest.getCreatedAt());
    }

    public void updateFriendRequestStatus(UUID requestId, FriendshipStatus status) {
        String sql = "UPDATE friend_requests SET status = ?::friend_request_status WHERE id = ?";
        jdbcTemplate.update(sql, status.name(), requestId);
    }

    public List<FriendRequestDTO> getPendingRequestsForUser(UUID userId) {
        String sql = "SELECT * FROM friend_requests WHERE to_user = ? AND status = 'PENDING'";
        return jdbcTemplate.query(sql, this::mapRowToFriendRequest, userId);
    }

    private FriendRequestDTO mapRowToFriendRequest(ResultSet rs, int rowNum) throws SQLException {
        return new FriendRequestDTO(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("from_user_id")),
                UUID.fromString(rs.getString("to_user_id")),
                FriendshipStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getString("username"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("profile_picture")
        );
    }
}
