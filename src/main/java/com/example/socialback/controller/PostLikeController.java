package com.example.socialback.controller;

import com.example.socialback.model.dto.UserDTO;
import com.example.socialback.service.PostLikeService;
import com.example.socialback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PostLikeController {

    private final PostLikeService postLikeService;
    private final UserService userService;

    // ✅ กดไลก์โพสต์
    @PostMapping("/{postId}")
    public ResponseEntity<String> likePost(
            @PathVariable UUID postId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = userService.findByUsername(userDetails.getUsername()).getId();
        boolean liked = postLikeService.likePost(postId, userId);

        return liked
                ? ResponseEntity.ok("Liked the post successfully! ❤️")
                : ResponseEntity.status(409).body("You already liked this post! 😆");
    }

    // ✅ ยกเลิกไลก์โพสต์
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> unlikePost(
            @PathVariable UUID postId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = userService.findByUsername(userDetails.getUsername()).getId();
        postLikeService.unlikePost(postId, userId);

        return ResponseEntity.ok("Unlike successful!");
    }

    // ✅ ดูจำนวนไลก์ของโพสต์
    @GetMapping("/{postId}/count")
    public ResponseEntity<Integer> getLikeCount(@PathVariable UUID postId) {
        return ResponseEntity.ok(postLikeService.countLikes(postId));
    }

    // ✅ ดูผู้ไลก์โพสต์
    @GetMapping("/{postId}/likers")
    public ResponseEntity<List<UserDTO>> getPostLikers(
            @PathVariable UUID postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postLikeService.getPostLikers(postId));
    }

    // ✅ ตรวจสอบว่าไลค์โพสต์นี้แล้วหรือยัง
    @GetMapping("/{postId}/check")
    public ResponseEntity<Map<String, Boolean>> checkLikeStatus(
            @PathVariable UUID postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UUID userId = userService.findByUsername(userDetails.getUsername()).getId();
        boolean hasLiked = postLikeService.hasLikedPost(postId, userId);
        
        return ResponseEntity.ok(Collections.singletonMap("liked", hasLiked));
    }
}
