package com.example.socialback.model.entity;

import lombok.*;


import jakarta.persistence.*;
import java.util.UUID;
import java.time.LocalDateTime;



@Entity
@Table(name = "friend_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequestEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(name = "requester_id")
    private UUID requesterId;

    @Column(name = "receiver_id")
    private UUID receiverId;

    private String status;  // "PENDING", "ACCEPTED", "REJECTED"
    private LocalDateTime createdAt;
}
