package com.example.socialback.service;

import com.example.socialback.model.dao.CommentDAO;
import com.example.socialback.model.dto.CommentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentDAO commentDAO;

    public List<CommentDTO> findAllByPostId(UUID postId) {
        return commentDAO.getCommentsByPostId(postId);
    }

    @Transactional
    public CommentDTO commentOnPost(UUID postId, UUID userId, String content) {
        return commentDAO.addComment(postId, userId, content);
    }

    @Transactional
    public boolean deleteComment(UUID commentId, UUID userId) {
        return commentDAO.deleteComment(commentId, userId);
    }
}
