package com.example.socialback.controller;

import com.example.socialback.model.dto.CommentDTO;
import com.example.socialback.service.CommentService;
import com.example.socialback.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    // ✅ ดึงคอมเมนต์ทั้งหมดของโพสต์
    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPost(@PathVariable UUID postId) {
        return ResponseEntity.ok(commentService.findAllByPostId(postId));
    }

    // ✅ คอมเมนต์โพสต์
    @PostMapping("/{postId}")
    public ResponseEntity<CommentDTO> commentOnPost(
            @PathVariable UUID postId,
            @RequestBody CommentDTO commentDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            UUID userId = userService.findByUsername(userDetails.getUsername()).getId();
            CommentDTO savedComment = commentService.commentOnPost(postId, userId, commentDTO.getContent());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // ✅ ลบคอมเมนต์ของตัวเอง
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(
            @PathVariable UUID commentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = userService.findByUsername(userDetails.getUsername()).getId();
        boolean deleted = commentService.deleteComment(commentId, userId);

        return deleted
                ? ResponseEntity.ok(Collections.singletonMap("message", "Comment deleted successfully"))
                : ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "You are not authorized to delete this comment"));
    }
}
