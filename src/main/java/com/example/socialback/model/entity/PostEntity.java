package com.example.socialback.model.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostEntity {
    private UUID id;
    private UUID ownerId; // ใช้ UUID ของ owner แทน UserEntity
    private String username;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int commentCount;
    private int likeCount;
    private int shareCount;

    // ✅ Constructor ที่รับค่าจาก `JdbcTemplate`
    public PostEntity(UUID id, String content, UUID userId, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.ownerId = userId;
        this.createdAt = createdAt;
    }
}
