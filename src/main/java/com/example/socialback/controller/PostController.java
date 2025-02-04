package com.example.socialback.controller;

import com.example.socialback.model.Post;
import com.example.socialback.repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.neo4j.core.Neo4jTemplate;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostRepository postRepository;
    private final Neo4jTemplate neo4jTemplate;

    public PostController(PostRepository postRepository, Neo4jTemplate neo4jTemplate) {
        this.postRepository = postRepository;
        this.neo4jTemplate = neo4jTemplate;
    }

    // Get all posts
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return ResponseEntity.ok(posts);
    }

    // Create a new post
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        if (post.getCreatedAt() == null) {
            post.setCreatedAt(new Date());
        }
        // บันทึกโพสต์และคืนค่าผลลัพธ์
        Post savedPost = postRepository.save(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }

    // Update an existing post
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody Post updatedPost) {
        Optional<Post> existingPostOptional = postRepository.findById(postId);

        if (existingPostOptional.isPresent()) {
            Post existingPost = existingPostOptional.get();
            existingPost.setTitle(updatedPost.getTitle());
            existingPost.setContent(updatedPost.getContent());
            Post savedPost = postRepository.save(existingPost);
            return ResponseEntity.ok(savedPost);
        }

        return ResponseEntity.notFound().build();
    }

    // Delete a post
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, @RequestParam Long userId) {
        Optional<Post> postOptional = neo4jTemplate.findById(postId, Post.class);

        if (postOptional.isPresent()) {
            Post post = postOptional.get();

            if (post.getUserId().equals(userId)) {
                postRepository.deleteById(postId);
                return ResponseEntity.ok().build();
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // ถ้า userId ไม่ตรงกัน
        }

        return ResponseEntity.notFound().build(); // ถ้าโพสต์ไม่พบ
    }
}
