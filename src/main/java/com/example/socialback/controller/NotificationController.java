package com.example.socialback.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.socialback.model.entity.NotificationEntity;
import com.example.socialback.service.NotificationService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // ✅ 1. ดึงการแจ้งเตือนทั้งหมด
    @GetMapping
    public ResponseEntity<List<NotificationEntity>> getNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        List<NotificationEntity> notifications = notificationService.getUserNotifications(userDetails.getUsername());
        return ResponseEntity.ok(notifications);
    }

    // ✅ 2. ทำเครื่องหมายว่าอ่านแล้ว
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable UUID notificationId) {
        boolean updated = notificationService.markNotificationAsRead(notificationId);
        if (updated) {
            return ResponseEntity.ok("Notification marked as read");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification not found");
        }
    }

    // ✅ 3. เพิ่มการแจ้งเตือนใหม่ (เฉพาะแอดมินหรือระบบ)
    @PostMapping("/add")
    public ResponseEntity<String> createNotification(@RequestParam UUID userId, @RequestParam String message) {
        boolean created = notificationService.createNotification(userId, message);
        if (created) {
            return ResponseEntity.ok("Notification created successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create notification");
        }
    }

    // ✅ 4. ลบการแจ้งเตือน
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> deleteNotification(@PathVariable UUID notificationId) {
        boolean deleted = notificationService.removeNotification(notificationId);
        if (deleted) {
            return ResponseEntity.ok("Notification deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification not found");
        }
    }
}
