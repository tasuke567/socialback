package com.example.socialback.controller;

import com.example.socialback.dto.PostDTO;
import com.example.socialback.entity.CommentEntity;
import com.example.socialback.entity.PostEntity;
import com.example.socialback.entity.PostShareEntity;
import com.example.socialback.entity.UserEntity;
import com.example.socialback.repository.PostRepository;
import com.example.socialback.repository.PostShareRepository;
import com.example.socialback.service.AuthService;
import com.example.socialback.service.PostService;
import com.example.socialback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PostController {

    private final PostRepository postRepository;
    private final PostService postService;
    private final AuthService authService;
    private final PostShareRepository postShareRepository;
    private final UserService userService;

    // ✅ Get all posts
    @GetMapping
    public ResponseEntity<List<PostEntity>> getAllPosts() {
        return ResponseEntity.ok(postRepository.findAll());
    }

    // ✅ Create a new post
    @PostMapping
    public ResponseEntity<PostEntity> createPost(
            @RequestBody PostEntity post,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Get authenticated user from security context
        UserEntity owner = userService.findByUsername(userDetails.getUsername());

        // Set post metadata
        post.setOwner(owner);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        // Save and return the new post
        PostEntity savedPost = postRepository.save(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }

    // ✅ Update an existing post
    @PutMapping("/{postId}")
    public ResponseEntity<PostEntity> updatePost(
            @PathVariable("postId") UUID postId,
            @RequestBody PostEntity updatedPost,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Get authenticated user from security context
        UserEntity owner = userService.findByUsername(userDetails.getUsername());

        return postRepository.findById(postId)
                .map(existingPost -> {
                    // Verify ownership
                    if (!existingPost.getOwner().getId().equals(owner.getId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<PostEntity>build(); // ✅ Explicit type
                    }

                    // Update only non-null fields
                    if (updatedPost.getTitle() != null) {
                        existingPost.setTitle(updatedPost.getTitle());
                    }
                    if (updatedPost.getContent() != null) {
                        existingPost.setContent(updatedPost.getContent());
                    }

                    // Update timestamp
                    existingPost.setUpdatedAt(LocalDateTime.now());

                    // Save and return updated post
                    return ResponseEntity.ok(postRepository.save(existingPost));
                })
                .orElse(ResponseEntity.<PostEntity>notFound().build()); // ✅ Explicit type
    }

    // ✅ Delete a post
    @DeleteMapping("/{postId}")
    public ResponseEntity<Object> deletePost(@PathVariable UUID id, @RequestParam String userId) {
        try {
            UUID parsedUserId = UUID.fromString(userId);
            return postRepository.findById(id)
                    .map(post -> {
                        if (post.getOwner().getId().equals(parsedUserId)) {
                            postRepository.deleteById(id);
                            return ResponseEntity.ok().build();
                        } else {
                            return ResponseEntity.<PostEntity>status(HttpStatus.FORBIDDEN).build();
                        }
                    })
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ Like a post
    @PostMapping("/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable("id") UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        // Get user directly from security context
        UserEntity owner = userService.findByUsername(userDetails.getUsername());

        try {
            postService.likePost(id, owner.getId());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid post ID");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ✅ Comment on a post
    @PostMapping("/{id}/comment")
    public ResponseEntity<?> commentOnPost(
            @PathVariable("id") UUID postId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody String content) {

        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Comment content cannot be empty");
        }

        // Get authenticated user same as createPost
        UserEntity owner = userService.findByUsername(userDetails.getUsername());

        try {
            CommentEntity comment = new CommentEntity();
            comment.setContent(content.trim());
            comment.setUserId(owner.getId());
            comment.setUsername(owner.getUsername());
            comment.setCreatedAt(LocalDateTime.now());

            postService.commentOnPost(postId, comment);
            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid post ID");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ✅ Share a post
    @PostMapping("/{id}/share")
    public ResponseEntity<?> sharePost(@PathVariable("id") UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        // Get user directly from security context
        UserEntity owner = userService.findByUsername(userDetails.getUsername());

        try {
            postService.sharePost(id, owner.getId());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid post ID");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ✅ Get posts shared by a specific user
    @GetMapping("/shared/{userId}")
    public ResponseEntity<List<PostDTO>> getSharedPosts(@PathVariable UUID userId) {
        List<PostShareEntity> shares = postShareRepository.findAllByUserId(userId); // ✅ ใช้งานได้แล้ว

        List<PostDTO> sharedPosts = shares.stream()
                .map(share -> new PostDTO(
                        share.getPost().getId(),
                        share.getPost().getTitle(),
                        share.getPost().getContent(),
                        share.getPost().getCreatedAt(),
                        share.getPost().getOwner().getUsername()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(sharedPosts);
    }

    @DeleteMapping("/{postId}/unlike")
    public ResponseEntity<Void> unlikePost(@PathVariable UUID postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity owner = authService.getUserId(userDetails.getUsername()).getBody();
        if (owner == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        try {
            postService.unlikePost(postId, owner.getId());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ Get post likers
    @GetMapping("/{postId}/likes")
    public ResponseEntity<?> getPostLikers(@PathVariable UUID postId) {
        try {
            List<UserEntity> likers = postService.getPostLikers(postId);
            return ResponseEntity.ok(likers);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid post ID");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ✅ Get post comments
    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getPostComments(@PathVariable UUID postId) {
        try {
            List<CommentEntity> comments = postService.getCommentsByPostId(postId);
            return ResponseEntity.ok(comments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid post ID");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
