package com.example.socialback.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostLikeDTO {
    private UUID id;
    private UUID postId;
    private UUID userId;
    private LocalDateTime createdAt;
    private String username;
    private String profile_picture;
}
