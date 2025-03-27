package com.example.socialback.model.dao;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.UUID;
import com.example.socialback.model.entity.NotificationEntity;
@Repository
public class NotificationDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public NotificationDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ✅ ดึงการแจ้งเตือนของผู้ใช้
    public List<NotificationEntity> getNotificationsByUserId(UUID userId) {
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new NotificationRowMapper(), userId);
    }

    // ✅ ทำเครื่องหมายว่าอ่านแล้ว
    public int markAsRead(UUID notificationId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ?";
        return jdbcTemplate.update(sql, notificationId);
    }

    // ✅ เพิ่มการแจ้งเตือนใหม่
    public int addNotification(UUID userId, String message) {
        String sql = "INSERT INTO notifications (id, user_id, message, is_read, created_at) VALUES (?, ?, ?, FALSE, now())";
        return jdbcTemplate.update(sql, UUID.randomUUID(), userId, message);
    }

    // ✅ ลบการแจ้งเตือน
    public int deleteNotification(UUID notificationId) {
        String sql = "DELETE FROM notifications WHERE id = ?";
        return jdbcTemplate.update(sql, notificationId);
    }
}
