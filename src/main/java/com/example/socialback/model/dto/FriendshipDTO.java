package com.example.socialback.model.dto;

import com.example.socialback.model.entity.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipDTO {
    private UUID id;
    private UUID userId;
    private UUID friendId;
    private String status; // PENDING, ACCEPTED, REJECTED, BLOCKED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public FriendshipDTO(UUID id, UUID userId, UUID friendId, FriendshipStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
    }
}
