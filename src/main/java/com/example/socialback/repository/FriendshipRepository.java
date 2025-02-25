package com.example.socialback.repository;

import com.example.socialback.entity.FriendshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FriendshipRepository extends JpaRepository<FriendshipEntity, UUID> {

    // เช็คว่า 2 คนเป็นเพื่อนกันไหม
    @Query("""
       SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END
       FROM FriendshipEntity f
       WHERE (f.userId = :user1 AND f.friendId = :user2)
          OR (f.userId = :user2 AND f.friendId = :user1)
    """)
    boolean areFriends(@Param("user1") UUID user1, @Param("user2") UUID user2);

    // ลบความเป็นเพื่อน
    @Modifying
    @Query("""
       DELETE FROM FriendshipEntity f
       WHERE (f.userId = :user1 AND f.friendId = :user2)
          OR (f.userId = :user2 AND f.friendId = :user1)
    """)
    void removeFriendship(@Param("user1") UUID user1, @Param("user2") UUID user2);

    // หา friendId ทั้งหมดของ user
    @Query("""
        SELECT f.friendId
        FROM FriendshipEntity f
        WHERE f.userId = :userId
        UNION
        SELECT f.userId
        FROM FriendshipEntity f
        WHERE f.friendId = :userId
    """)
    List<UUID> findAllFriendIds(@Param("userId") UUID userId);

    // etc.
}
