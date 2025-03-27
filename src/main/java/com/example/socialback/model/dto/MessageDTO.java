package com.example.socialback.model.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // หรือ @Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private UUID conversationId;
    private UUID senderId;
    private UUID receiverId;
    private String content;
    private String username;

    
    
}
