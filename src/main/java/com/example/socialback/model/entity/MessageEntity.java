package com.example.socialback.model.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageEntity {
    private UUID conversationId; // ไอดีของแชท
    private UUID senderId; // ผู้ส่งข้อความ
    private String content; // เนื้อหาข้อความ
    private LocalDateTime createdAt;
    private String username; // ชื่อผู้ใช้งาน
}
