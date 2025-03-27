package com.example.socialback.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private UUID id;
    private UUID postId;
    private UUID userId;
    private String content;
    private LocalDateTime createdAt;
    private String username;  // ✅ เพิ่ม username
    private String profilePicture;  // ✅ เพิ่มรูปโปรไฟล์ (optional)

    
}
