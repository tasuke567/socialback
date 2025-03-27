package com.example.socialback.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private UUID id;
    private String title;
    private String content;
    private UUID owner_id;
    private String username;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    // ✅ Constructor ที่รองรับทุกค่า
    public PostDTO(UUID id, UUID owner_id, String title, String content, LocalDateTime created_at,
            LocalDateTime updated_at, String username) {
        this.id = id;
        this.owner_id = owner_id;
        this.username = username;
        this.title = title;
        this.content = content;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    // ✅ Constructor แบบสั้น (กรณีใช้สร้างโพสต์ใหม่)
    public PostDTO(UUID owner_id, String title, String content, String username) {
        this(UUID.randomUUID(), owner_id, title, content, LocalDateTime.now(), LocalDateTime.now(), username);
    }
}
