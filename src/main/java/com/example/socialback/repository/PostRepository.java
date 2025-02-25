package com.example.socialback.repository;

import com.example.socialback.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, UUID> {
    @EntityGraph(attributePaths = { "comments", "likes", "shares" })
    @Query("SELECT p FROM PostEntity p WHERE p.id = :postId")
    Optional<PostEntity> findPostWithRelations(@Param("postId") UUID postId);
}
