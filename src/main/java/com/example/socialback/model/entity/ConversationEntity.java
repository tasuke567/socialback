package com.example.socialback.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConversationEntity {
    private UUID id;
    private UUID user1;
    private UUID user2;
    private LocalDateTime createdAt;

    public ConversationEntity(UUID id, UUID user1, UUID user2, LocalDateTime createdAt) {
        this.id = id;
        this.user1 = user1;
        this.user2 = user2;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public UUID getUser1() {
        return user1;
    }
    public void setUser1(UUID user1) {
        this.user1 = user1;
    }
    public UUID getUser2() {
        return user2;
    }
    public void setUser2(UUID user2) {
        this.user2 = user2;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
