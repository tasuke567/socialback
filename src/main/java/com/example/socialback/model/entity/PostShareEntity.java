package com.example.socialback.model.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "post_shares")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostShareEntity {

    @Id
    private UUID id;

    @Column(name = "post_id", nullable = false)
    private UUID postId; // ✅ ใช้ UUID แทน @ManyToOne

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public PostShareEntity(UUID postId, UUID userId) {
        this.id = UUID.randomUUID();
        this.postId = postId;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }
}
