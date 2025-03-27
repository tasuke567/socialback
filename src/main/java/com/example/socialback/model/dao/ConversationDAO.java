package com.example.socialback.model.dao;

import com.example.socialback.model.entity.ConversationEntity;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ConversationDAO {
        private final JdbcTemplate jdbcTemplate;

        // ✅ ดึงแชททั้งหมดของผู้ใช้
        public List<ConversationEntity> getUserConversations(UUID userId) {
                String sql = """
                                SELECT id, user1, user2, created_at
                                FROM conversations
                                WHERE user1 = ? OR user2 = ?
                                """;
                return jdbcTemplate.query(sql, (rs, rowNum) -> new ConversationEntity(
                                rs.getObject("id", UUID.class),
                                rs.getObject("user1", UUID.class),
                                rs.getObject("user2", UUID.class),
                                rs.getTimestamp("created_at").toLocalDateTime()), userId, userId);
        }

        // ✅ ค้นหาแชทระหว่าง 2 คน (ถ้ามีอยู่แล้ว)
        public Optional<ConversationEntity> findConversation(UUID user1, UUID user2) {
                String sql = """
                                SELECT id, user1, user2, created_at
                                FROM conversations
                                WHERE (user1 = ? AND user2 = ?) OR (user1 = ? AND user2 = ?)
                                """;
                List<ConversationEntity> conversations = jdbcTemplate.query(sql, (rs, rowNum) -> new ConversationEntity(
                                rs.getObject("id", UUID.class),
                                rs.getObject("user1", UUID.class),
                                rs.getObject("user2", UUID.class),
                                rs.getTimestamp("created_at").toLocalDateTime()), user1, user2, user2, user1);

                return conversations.stream().findFirst();
        }

        // ✅ สร้างแชทใหม่
        public ConversationEntity createConversation(UUID user1, UUID user2) {
                UUID conversationId = UUID.randomUUID();
                LocalDateTime now = LocalDateTime.now();
                String sql = """
                                INSERT INTO conversations (id, user1, user2, created_at) VALUES (?, ?, ?, ?)
                                """;
                jdbcTemplate.update(sql, conversationId, user1, user2, now);
                return new ConversationEntity(conversationId, user1, user2, now);
        }

        // ค้นหา conversation โดยใช้ id
        public Optional<ConversationEntity> findById(UUID conversationId) {
                final String sql = "SELECT id, user1, user2, created_at FROM conversations WHERE id = ?";
                try {
                        ConversationEntity conversation = jdbcTemplate.queryForObject(
                                        sql,
                                        new Object[] { conversationId },
                                        new ConversationRowMapper());
                        return Optional.of(conversation);
                } catch (EmptyResultDataAccessException ex) {
                        return Optional.empty();
                }
        }

}
