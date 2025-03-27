package com.example.socialback.controller;

import com.example.socialback.model.dto.ChatMessageDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessageDTO sendMessage(ChatMessageDTO message) {
        System.out.println("📩 New Chat: " + message);
        return message;
    }
}
