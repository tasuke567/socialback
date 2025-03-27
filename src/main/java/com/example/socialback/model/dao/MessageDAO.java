package com.example.socialback.model.dao;

import com.example.socialback.model.entity.MessageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MessageDAO {
    private final JdbcTemplate jdbcTemplate;

    // ✅ ดึงข้อความจากแชท
    public List<MessageEntity> getMessagesByConversation(UUID conversationId) {
        String sql = """
                SELECT messages.id AS id,
                           conversation_id,
                           sender_id,
                           content,
                           messages.created_at,
                           username
                FROM messages
                JOIN users ON messages.sender_id = users.id
                WHERE conversation_id = ?
                ORDER BY messages.created_at ASC
                                                                    """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new MessageEntity(
                rs.getObject("conversation_id", UUID.class),
                rs.getObject("sender_id", UUID.class),
                rs.getString("content"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getString("username")), conversationId);
    }

    public MessageEntity sendMessage(UUID senderId, UUID receiverId, String content, UUID conversationId, String username) {
        if (conversationId == null) {
            throw new IllegalArgumentException("❌ conversationId cannot be null!");
        }
        
        System.out.println("🛠 Saving to DB -> conversationId: " + conversationId + ", senderId: " + senderId + ", content: " + content);
    
        LocalDateTime now = LocalDateTime.now();
        final String sql = """
                INSERT INTO messages (conversation_id, sender_id, content, created_at)
                VALUES (?, ?, ?, ?)
                """;
    
        int rowsAffected = jdbcTemplate.update(sql, conversationId, senderId, content, now);
    
        if (rowsAffected != 1) {
            throw new RuntimeException("Failed to send message: no rows inserted.");
        }
    
        return new MessageEntity(conversationId, senderId, content, now, username);
    }
    

    // ✅ ลบข้อความ
    public boolean deleteMessage(UUID messageId, UUID senderId) {
        String sql = """
                DELETE FROM messages WHERE id = ? AND sender_id = ?
                """;
        return jdbcTemplate.update(sql, messageId, senderId) > 0;
    }
}
