package com.example.socialback.model.dao;

import com.example.socialback.model.dto.PostDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PostDAO {
    private final JdbcTemplate jdbcTemplate;

    public List<PostDTO> getFeed(UUID userId) {
        String sql = """
                        SELECT DISTINCT p.*
                FROM posts p
                LEFT JOIN friendships f
                    ON (p.owner_id = f.user_id OR p.owner_id = f.friend_id)
                    AND f.status = 'ACCEPTED'
                WHERE (f.user_id = ? OR f.friend_id = ? OR p.owner_id = ?)
                ORDER BY p.created_at DESC
                        """;
        return jdbcTemplate.query(sql, new PostRowMapper(), userId, userId, userId);
    }

    public PostDTO getPostById(UUID postId) {
        String sql = "SELECT * FROM posts WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new PostRowMapper(), postId);
    }

    public PostDTO createPost(PostDTO postDTO) {
        String sql = "INSERT INTO posts (id, owner_id, title, content, created_at, updated_at, username) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, postDTO.getId(), postDTO.getOwner_id(), postDTO.getTitle(),
                postDTO.getContent(), LocalDateTime.now(), LocalDateTime.now(), postDTO.getUsername());
        return postDTO;
    }

    public Map<String, String> deletePost(UUID postId, UUID ownerId) {
        String deleteSharesSql = "DELETE FROM post_shares WHERE post_id = ?";
        jdbcTemplate.update(deleteSharesSql, postId);
        // ลบไลค์ของโพสต์ก่อน
        String deleteLikesSql = "DELETE FROM post_likes WHERE post_id = ?";
        jdbcTemplate.update(deleteLikesSql, postId);
        String deleteCommentsSql = "DELETE FROM comments WHERE post_id = ?";
        jdbcTemplate.update(deleteCommentsSql, postId);
        String sql = "DELETE FROM posts WHERE id = ? AND owner_id = ?";
        boolean isDeleted = jdbcTemplate.update(sql, postId, ownerId) > 0;

        // Return JSON response
        return isDeleted ? Map.of("message", "Post deleted successfully") : Map.of("message", "Post not found or not deleted");
    }

    public PostDTO updatePost(PostDTO postDTO) {
        String sql = "UPDATE posts SET title = ?, content = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql, postDTO.getTitle(), postDTO.getContent(), LocalDateTime.now(), postDTO.getId());
        return postDTO;
    }

    public List<PostDTO> getUserPosts(UUID userId) {
        String sql = "SELECT * FROM posts WHERE owner_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new PostRowMapper(), userId);
    }
}
