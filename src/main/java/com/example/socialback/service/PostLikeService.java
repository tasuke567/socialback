package com.example.socialback.service;

import com.example.socialback.model.dao.PostLikeDAO;
import com.example.socialback.model.dto.UserDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeDAO postLikeDAO;

    @Transactional
    public boolean likePost(UUID postId, UUID userId) {
        return postLikeDAO.likePost(postId, userId);
    }

    @Transactional
    public void unlikePost(UUID postId, UUID userId) {
        postLikeDAO.unlikePost(postId, userId);
    }

    public int countLikes(UUID postId) {
        return postLikeDAO.countLikes(postId);
    }

    public List<UserDTO> getPostLikers(UUID postId) {
        return postLikeDAO.getPostLikers(postId);
    }

    public boolean hasLikedPost(UUID postId, UUID userId) {
        return postLikeDAO.hasLikedPost(postId, userId);
    }
}
