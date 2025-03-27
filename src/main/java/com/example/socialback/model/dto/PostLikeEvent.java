package com.example.socialback.model.dto;

import lombok.Data;

@Data
public class PostLikeEvent {
    private String postId;
    private String userId;
    private String username;
    private String timestamp;
}
