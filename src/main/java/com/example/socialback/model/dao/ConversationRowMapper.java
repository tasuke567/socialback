package com.example.socialback.model.dao;

import com.example.socialback.model.entity.ConversationEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class ConversationRowMapper implements RowMapper<ConversationEntity> {
    @Override
    public ConversationEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        UUID user1 = UUID.fromString(rs.getString("user1"));
        UUID user2 = UUID.fromString(rs.getString("user2"));
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        return new ConversationEntity(id, user1, user2, createdAt);
    }
}
