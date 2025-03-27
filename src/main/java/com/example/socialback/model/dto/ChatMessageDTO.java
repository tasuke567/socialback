package com.example.socialback.model.dto;

import java.sql.Timestamp;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {
    private UUID senderId;
    private UUID receiverId;
    private UUID conversationId;
    private String content;
    private Timestamp timestamp;
}
