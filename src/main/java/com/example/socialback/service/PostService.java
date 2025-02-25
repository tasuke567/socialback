package com.example.socialback.service;

import com.example.socialback.entity.*;
import com.example.socialback.repository.*;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostShareRepository postShareRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostEntity createPost(PostEntity post, UserEntity user) {
        post.setOwner(user);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public PostEntity findPostById(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Transactional
    public void likePost(UUID postId, UUID userId) {
        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            return;
        }

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PostLikeEntity like = PostLikeEntity.builder()
                .post(post)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        postLikeRepository.save(like);
    }

    @Transactional
    public void unlikePost(UUID postId, UUID userId) {
        postLikeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    @Transactional
    public void commentOnPost(UUID postId, CommentEntity comment) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        UserEntity user = userRepository.findById(comment.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    @Transactional
    public void sharePost(UUID postId, UUID userId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PostShareEntity share = PostShareEntity.builder()
                .post(post)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        postShareRepository.save(share);
    }

    public List<PostShareEntity> findAllByUserId(UUID userId) {
        return postShareRepository.findAllByUserId(userId);
    }

    @Transactional
    public void updateLikes(UUID postId, List<UserEntity> likedBy) {
        postLikeRepository.deleteLikesByPostId(postId);
        likedBy.forEach(user -> {
            PostEntity post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));
            PostLikeEntity like = PostLikeEntity.builder()
                    .post(post)
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .build();
            postLikeRepository.save(like);
        });
    }

    @Transactional(readOnly = true)
    public List<UserEntity> getPostLikers(UUID postId) {
        return postLikeRepository.findAllByPostId(postId).stream()
                .map(PostLikeEntity::getUser)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommentEntity> getCommentsByPostId(UUID postId) {
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("Post not found");
        }
        return commentRepository.findByPostId(postId);
    }
}
