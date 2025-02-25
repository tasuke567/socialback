package com.example.socialback.repository;

import com.example.socialback.entity.PostLikeEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PostLikeRepository extends JpaRepository<PostLikeEntity, UUID> {

    @Modifying
    @Transactional
    @Query("DELETE FROM PostLikeEntity pl WHERE pl.post.id = :postId")
    void deleteLikesByPostId(@Param("postId") UUID postId);

    boolean existsByPostIdAndUserId(UUID postId, UUID userId);

    void deleteByPostIdAndUserId(UUID postId, UUID userId);

    long countByPostId(UUID postId);

    List<PostLikeEntity> findAllByPostId(UUID postId);
}

