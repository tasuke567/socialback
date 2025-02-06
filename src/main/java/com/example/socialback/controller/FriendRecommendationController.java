// FriendRecommendationController.java
package com.example.socialback.controller;

import com.example.socialback.dto.FriendRecommendationDTO;
import com.example.socialback.model.User;
import com.example.socialback.service.FriendRecommendationService;
import com.example.socialback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FriendRecommendationController {

    private final FriendRecommendationService recommendationService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<FriendRecommendationDTO>> getRecommendations(
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        List<FriendRecommendationDTO> recommendations =
                recommendationService.getRecommendations(currentUser.getId());
        return ResponseEntity.ok(recommendations);
    }
}