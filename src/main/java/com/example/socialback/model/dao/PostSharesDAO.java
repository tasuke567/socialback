package com.example.socialback.model.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PostSharesDAO {
    private final JdbcTemplate jdbcTemplate;

    public boolean sharePost(UUID postId, UUID userId) {
        String checkSql = "SELECT COUNT(*) FROM post_shares WHERE post_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, postId, userId);

        if (count != null && count > 0) {
            return false; // แชร์แล้ว
        }

        String sql = "INSERT INTO post_shares (id, post_id, user_id, created_at) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, UUID.randomUUID(), postId, userId, LocalDateTime.now());
        return true;
    }

    public boolean deletePostShare(UUID postId, UUID userId) {
        String sql = "DELETE FROM post_shares WHERE post_id = ? AND user_id = ?";
        return jdbcTemplate.update(sql, postId, userId) > 0;
    }

    public int getShareCount(UUID postId) {
        String sql = "SELECT COUNT(*) FROM post_shares WHERE post_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, postId);
    }

    public List<UUID> getUserSharedPosts(UUID userId) {
        String sql = "SELECT post_id FROM post_shares WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, UUID.class, userId);
    }

    // In PostSharesDAO.java
    public boolean checkShareStatus(UUID postId, UUID userId) {
        String sql = "SELECT COUNT(*) FROM post_shares WHERE post_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, postId, userId);
        return count != null && count > 0;
    }

}
