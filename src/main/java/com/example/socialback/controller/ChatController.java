package com.example.socialback.controller;

import com.example.socialback.model.dto.ChatMessageEvent;
import com.example.socialback.service.ChatKafkaProducer;
import com.example.socialback.repository.ChatRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatKafkaProducer chatKafkaProducer;
    private final ChatRepository chatRepository;

    public ChatController(ChatKafkaProducer chatKafkaProducer, ChatRepository chatRepository) {
        this.chatKafkaProducer = chatKafkaProducer;
        this.chatRepository = chatRepository;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendChat(@RequestBody ChatMessageEvent chatMessage) {
        chatKafkaProducer.sendChatMessage(chatMessage);
        return ResponseEntity.ok("Chat message sent to Kafka!");
    }

    // ✅ เพิ่ม API ดึงประวัติแชท
    @GetMapping("/history/{conversationId}")
    public ResponseEntity<List<Map<String, Object>>> getChatHistory(@PathVariable String conversationId) {
        List<Map<String, Object>> chatHistory = chatRepository.getChatHistory(conversationId);
        return ResponseEntity.ok(chatHistory);
    }
}
