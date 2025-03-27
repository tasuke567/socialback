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
public class FriendRequestDTO {
    private UUID id;
    private UUID fromUserId;
    private UUID toUserId;
    private FriendshipStatus status; // PENDING, ACCEPTED, REJECTED
    private LocalDateTime createdAt;
    private String username;
    private String firstName;
    private String lastName;
    private String profilePicture;

    public FriendRequestDTO(UUID id, UUID fromUserId, UUID toUserId, FriendshipStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public FriendRequestDTO(UUID id, UUID fromUserId, UUID toUserId, FriendshipStatus status, Object o, LocalDateTime createdAt, String username, String firstName, String lastName, String profilePicture) {

    }
}
