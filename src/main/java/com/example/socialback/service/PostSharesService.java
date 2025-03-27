package com.example.socialback.service;

import com.example.socialback.model.dao.PostSharesDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostSharesService {
    private final PostSharesDAO postSharesDAO;

    @Transactional
    public boolean sharePost(UUID postId, UUID userId) {
        return postSharesDAO.sharePost(postId, userId);
    }

    @Transactional
    public boolean deletePostShare(UUID postId, UUID userId) {
        return postSharesDAO.deletePostShare(postId, userId);
    }

    public int getShareCount(UUID postId) {
        return postSharesDAO.getShareCount(postId);
    }

    public List<UUID> getUserSharedPosts(UUID userId) {
        return postSharesDAO.getUserSharedPosts(userId);
    }

    // In PostSharesService.java
    public boolean checkShareStatus(UUID postId, UUID userId) {
        return postSharesDAO.checkShareStatus(postId, userId);
    }

}
