package com.example.socialback.repository;

import com.example.socialback.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {
   
    List<CommentEntity> findByPostId(UUID postId);
}
