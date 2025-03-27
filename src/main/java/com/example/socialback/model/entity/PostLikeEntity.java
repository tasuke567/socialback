package com.example.socialback.model.entity;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLikeEntity {
    private UUID id;
    private UUID postId;
    private UUID userId;
    private LocalDateTime createdAt;
    private String username;  // ✅ เพิ่ม username
    private String profilePicture;  // ✅ เพิ่มรูปโปรไฟล์ (optional)

    // ✅ Constructor สำหรับ INSERT
    public PostLikeEntity(UUID postId, UUID userId) {
        this.id = UUID.randomUUID();
        this.postId = postId;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }
}
