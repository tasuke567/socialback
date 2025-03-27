package com.example.socialback.model.entity;

import java.sql.Timestamp;
import java.util.UUID;

public class NotificationEntity {
    private UUID id;
    private UUID userId;
    private String message;
    private boolean isRead;
    private Timestamp createdAt;

    // ✅ Constructor
    public NotificationEntity(UUID id, UUID userId, String message, boolean isRead, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // ✅ Getter & Setter
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean isRead) { this.isRead = isRead; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
