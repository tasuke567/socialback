package com.example.socialback.model.dao;

import com.example.socialback.model.dto.CommentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CommentDAO {
    private final JdbcTemplate jdbcTemplate;

    public List<CommentDTO> getCommentsByPostId(UUID postId) {
        String sql = """
                SELECT c.id, c.post_id, c.user_id, c.content, c.created_at, 
                       u.username, u.profile_picture
                FROM comments c
                JOIN users u ON c.user_id = u.id
                WHERE c.post_id = ?
                ORDER BY c.created_at ASC
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new CommentDTO(
                rs.getObject("id", UUID.class),
                rs.getObject("post_id", UUID.class),
                rs.getObject("user_id", UUID.class),
                rs.getString("content"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getString("username"),
                rs.getString("profile_picture")
        ), postId);
    }

    public CommentDTO addComment(UUID postId, UUID userId, String content) {
        UUID commentId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        String sql = "INSERT INTO comments (id, post_id, user_id, content, created_at) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, commentId, postId, userId, content, now);

        return new CommentDTO(commentId, postId, userId, content, now, null, null);
    }

    public boolean deleteComment(UUID commentId, UUID userId) {
        String sql = "DELETE FROM comments WHERE id = ? AND user_id = ?";
        return jdbcTemplate.update(sql, commentId, userId) > 0;
    }
}
