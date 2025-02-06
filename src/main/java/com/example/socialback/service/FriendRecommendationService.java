// FriendRecommendationService.java
package com.example.socialback.service;

import com.example.socialback.model.User;
import com.example.socialback.dto.FriendRecommendationDTO;
import com.example.socialback.repository.FriendRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendRecommendationService {

    private final FriendRecommendationRepository recommendationRepository;
    

    @Transactional(readOnly = true)
    public List<FriendRecommendationDTO> getRecommendations(UUID userId) {
        // Get recommendations from different sources
        List<User> friendOfFriends = recommendationRepository.findFriendOfFriends(userId);
        List<User> commonInterests = recommendationRepository.findUsersWithCommonInterests(userId);

        // Combine and score recommendations
        Map<User, Double> scores = new HashMap<>();

        // Score friend of friends (weight: 0.7)
        friendOfFriends.forEach(user ->
                scores.merge(user, 0.7, Double::sum));

        // Score common interests (weight: 0.3)
        commonInterests.forEach(user ->
                scores.merge(user, 0.3, Double::sum));

        // Convert to DTOs with scores
        return scores.entrySet().stream()
                .map(entry -> FriendRecommendationDTO.builder()
                        .user(entry.getKey())
                        .score(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(FriendRecommendationDTO::getScore).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
}