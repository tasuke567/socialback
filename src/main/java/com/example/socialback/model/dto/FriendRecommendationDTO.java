package com.example.socialback.model.dto;

import com.example.socialback.model.entity.UserEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendRecommendationDTO {
    private UserEntity user;  // User ที่แนะนำ
    private Double score;  // คะแนนในการแนะนำ
}
