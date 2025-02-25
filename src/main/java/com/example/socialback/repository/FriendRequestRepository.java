package com.example.socialback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.socialback.entity.FriendRequestEntity;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequestEntity, UUID> {

    // หาว่ามี friend request pending ไหม
    @Query("""
        SELECT fr
        FROM FriendRequestEntity fr
        WHERE fr.fromUserId = :fromUserId
          AND fr.toUserId = :toUserId
          AND fr.status = 'PENDING'
    """)
    Optional<FriendRequestEntity> findPendingRequest(
            @Param("fromUserId") UUID fromUserId,
            @Param("toUserId") UUID toUserId
    );

    @Modifying
    @Query("""
        DELETE FROM FriendRequestEntity fr
        WHERE fr.fromUserId = :fromUserId
          AND fr.toUserId = :toUserId
    """)
    void deleteByUsers(@Param("fromUserId") UUID fromUserId, @Param("toUserId") UUID toUserId);
}