package com.example.socialback.model.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentEntity {
    private UUID id;
    private UUID postId;
    private UUID userId;
    private String username; // ใส่ username ของคนที่คอมเมนต์
    private String content;
    private LocalDateTime createdAt;

    // ✅ Constructor ที่ตรงกับการเรียกใช้
    public CommentEntity(UUID id, UUID postId, UUID userId, String content, LocalDateTime createdAt) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }

    // ✅ Constructor แบบกำหนดค่าเอง
    public CommentEntity(UUID postId, UUID userId, String username, String content) {
        this.id = UUID.randomUUID(); // สร้าง UUID ใหม่อัตโนมัติ
        this.postId = postId;
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.createdAt = LocalDateTime.now(); // ใช้เวลาปัจจุบัน
    }
}
