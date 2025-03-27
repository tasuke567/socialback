package com.example.socialback.model.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.util.UUID;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendshipEntity {
    private UUID id;
    private UUID userId;
    private UUID friendId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status; // e.g., ACCEPTED, BLOCKED, etc.

    // ✅ เพิ่ม constructor ให้รองรับทุกพารามิเตอร์
    public FriendshipEntity(UUID id, UUID userId, UUID friendId, FriendshipStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
