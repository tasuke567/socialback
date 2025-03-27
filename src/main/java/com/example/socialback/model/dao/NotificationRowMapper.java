package com.example.socialback.model.dao;

import com.example.socialback.model.entity.NotificationEntity;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
public class NotificationRowMapper implements RowMapper<NotificationEntity> {
    @Override
    public NotificationEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new NotificationEntity(
            rs.getObject("id", UUID.class),
            rs.getObject("user_id", UUID.class),
            rs.getString("message"),
            rs.getBoolean("is_read"),
            rs.getTimestamp("created_at")
        );
    }
}
