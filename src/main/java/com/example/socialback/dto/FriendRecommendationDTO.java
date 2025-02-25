package com.example.socialback.dto;

import com.example.socialback.entity.UserEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendRecommendationDTO {
    private UserEntity user;  // User ที่แนะนำ
    private Double score;  // คะแนนในการแนะนำ
}
