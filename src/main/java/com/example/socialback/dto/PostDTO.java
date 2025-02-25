package com.example.socialback.dto;

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
    private LocalDateTime createdAt;
    private String ownerUsername; // ✅ ดึงเฉพาะข้อมูลที่ต้องการ
}
