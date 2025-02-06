// FriendRecommendationDTO.java
package com.example.socialback.dto;

import com.example.socialback.model.User;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendRecommendationDTO {
    private User user;
    private Double score;
    private int mutualFriends;
    private int commonInterests;
}