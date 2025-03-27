package com.example.socialback.model.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.socialback.model.dto.UserDTO;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PostLikeDAO {
    private final JdbcTemplate jdbcTemplate;

    public boolean likePost(UUID postId, UUID userId) {
        String checkSql = "SELECT COUNT(*) FROM post_likes WHERE post_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, postId, userId);

        if (count != null && count > 0) {
            return false; // กดไลก์แล้ว
        }

        String sql = "INSERT INTO post_likes (id, post_id, user_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, UUID.randomUUID(), postId, userId);
        return true;
    }

    public void unlikePost(UUID postId, UUID userId) {
        String sql = "DELETE FROM post_likes WHERE post_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, postId, userId);
    }

    public int countLikes(UUID postId) {
        String sql = "SELECT COUNT(*) FROM post_likes WHERE post_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, postId);
    }

    public List<UserDTO> getPostLikers(UUID postId) {
        String sql = """
            SELECT u.id, u.username, u.profile_picture, u.email 
            FROM post_likes pl
            JOIN users u ON pl.user_id = u.id
            WHERE pl.post_id = ?
            ORDER BY pl.created_at DESC
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new UserDTO(
            rs.getObject("id", UUID.class),
            rs.getString("username"),
            "",
            rs.getString("profile_picture")),
        postId);
    }

    public boolean hasLikedPost(UUID postId, UUID userId) {
        String sql = "SELECT COUNT(*) FROM post_likes WHERE post_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, postId, userId);
        return count != null && count > 0;
    }
}
