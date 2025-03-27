package com.example.socialback.repository;

import com.example.socialback.model.dto.ChatMessageEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class ChatRepository {
    private final JdbcTemplate jdbcTemplate;

    public ChatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveChat(ChatMessageEvent chat) {
        String sql = "INSERT INTO messages (id, conversation_id, sender_id, content, created_at) VALUES (gen_random_uuid(), ?, ?, ?, ?)";
        jdbcTemplate.update(sql, chat.getConversationId(), chat.getSenderId(), chat.getMessage(), Timestamp.from(Instant.now()));
    }

    // ✅ เพิ่มฟังก์ชันดึงประวัติแชท
    public List<Map<String, Object>> getChatHistory(String conversationId) {
        String sql = "SELECT * FROM messages WHERE conversation_id = ? ORDER BY created_at ASC";
        return jdbcTemplate.queryForList(sql, UUID.fromString(conversationId));
    }
}
