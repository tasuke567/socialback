package com.example.socialback.service;

import com.example.socialback.model.dao.PostDAO;
import com.example.socialback.model.dto.PostDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostDAO postDAO;

    public List<PostDTO> getFeed(UUID userId) {
        return postDAO.getFeed(userId);
    }

    public PostDTO getPostById(UUID postId) {
        return postDAO.getPostById(postId);
    }

    @Transactional
    public PostDTO createPost(PostDTO postDTO) {
        postDTO.setId(UUID.randomUUID());
        return postDAO.createPost(postDTO);
    }

    @Transactional
    public Map<String, String> deletePost(UUID postId, UUID ownerId) {
        return postDAO.deletePost(postId, ownerId);
    }

    @Transactional
    public PostDTO updatePost(PostDTO postDTO) {
        return postDAO.updatePost(postDTO);
    }

    public List<PostDTO> getUserPosts(UUID userId) {
        return postDAO.getUserPosts(userId);
    }
}
