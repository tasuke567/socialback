package com.example.socialback.controller;

import com.example.socialback.service.PostSharesService;
import com.example.socialback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/shares")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PostSharesController {
    private final PostSharesService postSharesService;
    private final UserService userService;

    // ✅ แชร์โพสต์
    @PostMapping("/{postId}")
    public ResponseEntity<?> sharePost(
            @PathVariable UUID postId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = userService.findByUsername(userDetails.getUsername()).getId();
        boolean shared = postSharesService.sharePost(postId, userId);

        if (shared) {
            int shareCount = postSharesService.getShareCount(postId);
            // Build a comprehensive JSON response
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Post shared successfully.",
                    "shareCount", shareCount,
                    "postId", postId.toString(),
                    "timestamp", LocalDateTime.now().toString()));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Failed to share post. The post may have already been shared.",
                    "postId", postId.toString(),
                    "timestamp", LocalDateTime.now().toString()));
        }
    }

    // ✅ ยกเลิกแชร์โพสต์
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePostShare(
            @PathVariable UUID postId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = userService.findByUsername(userDetails.getUsername()).getId();
        boolean deleted = postSharesService.deletePostShare(postId, userId);

        if (deleted) {
            int shareCount = postSharesService.getShareCount(postId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Post unshared successfully.",
                    "shareCount", shareCount,
                    "postId", postId.toString(),
                    "timestamp", LocalDateTime.now().toString()));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Failed to unshare post.",
                    "postId", postId.toString(),
                    "timestamp", LocalDateTime.now().toString()));
        }
    }

    // ✅ ดูจำนวนแชร์ของโพสต์
    @GetMapping("/{postId}/count")
    public ResponseEntity<Integer> getShareCount(@PathVariable UUID postId) {
        return ResponseEntity.ok(postSharesService.getShareCount(postId));
    }

    // ✅ ดูโพสต์ที่ผู้ใช้แชร์
    @GetMapping("/user")
    public ResponseEntity<List<UUID>> getUserSharedPosts(@AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = userService.findByUsername(userDetails.getUsername()).getId();
        return ResponseEntity.ok(postSharesService.getUserSharedPosts(userId));
    }

    @GetMapping("/{postId}/check")
    public ResponseEntity<Map<String, Boolean>> checkShareStatus(
            @PathVariable UUID postId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = userService.findByUsername(userDetails.getUsername()).getId();
        boolean shared = postSharesService.checkShareStatus(postId, userId);
        return ResponseEntity.ok(Map.of("shared", shared));
    }

}
