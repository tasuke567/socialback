package com.example.socialback.model.dto;

import java.sql.Timestamp;
import java.util.UUID;

import lombok.Data;

@Data
public class ChatMessageEvent {
    private UUID conversationId;
    private UUID senderId;
    private UUID receiverId;
    private String message;
    private Timestamp timestamp;
}
