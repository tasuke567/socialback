package com.example.socialback.controller;

import com.example.socialback.model.dto.PostDTO;
import com.example.socialback.service.PostService;
import com.example.socialback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PostController {

    private final PostService postService;
    private final UserService userService;

    // ✅ ดึงโพสต์จากเพื่อน & เพจ
    @GetMapping("/feed")
    public ResponseEntity<List<PostDTO>> getFeed(@AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = userService.findByUsername(userDetails.getUsername()).getId();
        return ResponseEntity.ok(postService.getFeed(userId));
    }

    // ✅ สร้างโพสต์ใหม่
    @PostMapping
    public ResponseEntity<PostDTO> createPost(
            @RequestBody PostDTO postDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID ownerId = userService.findByUsername(userDetails.getUsername()).getId();
        String username = userDetails.getUsername();

        postDTO.setOwner_id(ownerId);
        postDTO.setUsername(username);
        postDTO.setCreated_at(LocalDateTime.now());
        postDTO.setUpdated_at(LocalDateTime.now());

        PostDTO savedPost = postService.createPost(postDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }

    // ✅ ดูรายละเอียดโพสต์
    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable UUID postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    // ✅ ลบโพสต์ของตัวเอง
    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable UUID postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID ownerId = userService.findByUsername(userDetails.getUsername()).getId();
        Map<String, String> response = postService.deletePost(postId, ownerId);
        return ResponseEntity.ok(response);
    }

    // ✅ แก้ไขโพสต์
    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable UUID postId,
            @RequestBody PostDTO postDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            UUID ownerId = userService.findByUsername(userDetails.getUsername()).getId();
            PostDTO existingPost = postService.getPostById(postId);

            if (!existingPost.getOwner_id().equals(ownerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Only update provided fields
            if (postDTO.getTitle() != null) {
                existingPost.setTitle(postDTO.getTitle());
            }
            if (postDTO.getContent() != null) {
                existingPost.setContent(postDTO.getContent());
            }
            existingPost.setUpdated_at(LocalDateTime.now());

            PostDTO updatedPost = postService.updatePost(existingPost);
            return ResponseEntity.ok(updatedPost);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        }
    }

    // ✅ ดูโพสต์ของผู้ใช้คนอื่น
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDTO>> getUserPosts(@PathVariable UUID userId) {
        return ResponseEntity.ok(postService.getUserPosts(userId));
    }
}
