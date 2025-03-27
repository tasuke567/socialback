package com.example.socialback.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.UUID;
import com.example.socialback.model.entity.NotificationEntity;
import com.example.socialback.model.dao.NotificationDAO;

@Service
public class NotificationService {

    private final NotificationDAO notificationDAO;
    private final UserService userService;

    @Autowired
    public NotificationService(NotificationDAO notificationDAO, UserService userService) {
        this.notificationDAO = notificationDAO;
        this.userService = userService;
    }

    // ✅ ดึงการแจ้งเตือนทั้งหมดของ user ที่ล็อกอินอยู่
    public List<NotificationEntity> getUserNotifications(String username) {
        UUID userId = userService.findByUsername(username).getId();
        return notificationDAO.getNotificationsByUserId(userId);
    }

    // ✅ ทำเครื่องหมายว่าอ่านแล้ว
    public boolean markNotificationAsRead(UUID notificationId) {
        return notificationDAO.markAsRead(notificationId) > 0;
    }

    // ✅ เพิ่มการแจ้งเตือนใหม่
    public boolean createNotification(UUID userId, String message) {
        return notificationDAO.addNotification(userId, message) > 0;
    }

    // ✅ ลบการแจ้งเตือน
    public boolean removeNotification(UUID notificationId) {
        return notificationDAO.deleteNotification(notificationId) > 0;
    }
}
