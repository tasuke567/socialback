package com.example.socialback.controller;

import com.example.socialback.model.dto.MessageDTO;
import com.example.socialback.model.dto.MessageRequestDto;
import com.example.socialback.model.entity.ConversationEntity;
import com.example.socialback.model.entity.MessageEntity;
import com.example.socialback.security.CustomUserDetails;
import com.example.socialback.security.JwtUtil;
import com.example.socialback.service.MessageService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import com.example.socialback.service.ChatService;
import lombok.RequiredArgsConstructor;
    import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.messaging.simp.SimpMessagingTemplate;
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;
    private final ChatService chatService;
    private final JwtUtil jwtUtil;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    // ✅ ดึงรายชื่อแชททั้งหมด
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationEntity>> getUserConversations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            System.out.println("🚨 No Authentication found in SecurityContext");
        } else {
            System.out.println("✅ Authentication exists: " + userDetails.getUsername());
        }

        if (userDetails == null) {
            System.out.println("🚨 Authentication failed: userDetails is null");
            return ResponseEntity.status(401).body(null);
        }

        System.out.println("✅ User Authenticated: " + userDetails.getUsername());

        UUID userId = userDetails.getUser().getId();
        List<ConversationEntity> conversations = messageService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    // ✅ ดึงข้อความในแชท
    @GetMapping("/{conversationId}")
    public ResponseEntity<List<MessageEntity>> getMessages(@PathVariable UUID conversationId) {
        List<MessageEntity> messages = messageService.getMessagesByConversation(conversationId);
        return ResponseEntity.ok(messages);
    }

    // ✅ ส่งข้อความ
    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .build(); // หรือ .body(Map.of("error", "Unauthorized")) ก็ได้
        }

        UUID senderId = userDetails.getUser().getId();
        String username = userDetails.getUsername();
        String receiverIdStr = payload.get("receiverId");
        String content = payload.get("content");

        if (receiverIdStr == null || content == null
                || content.trim().isEmpty()
                || payload.get("conversationId") == null) {
            return ResponseEntity.badRequest()
                    .body(null); // หรือใช้ .body(Map.of("error","Invalid request"))
        }

        UUID conversationId;
        UUID receiverId;
        try {
            conversationId = UUID.fromString(payload.get("conversationId"));
            receiverId = UUID.fromString(receiverIdStr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(null);
        }

        // ✅ Debug log
        System.out.println("📤 Sending to Kafka -> conversationId: " + conversationId
                + ", senderId: " + senderId
                + ", receiverId: " + receiverId
                + ", content: " + content);

        String topic = "chat-messages";

        // ✅ ส่งไป Kafka
        String messageJson = String.format(
                "{\"conversationId\": \"%s\", \"senderId\": \"%s\", \"receiverId\": \"%s\", \"content\": \"%s\"}",
                conversationId, senderId, receiverId, content);
        System.out.println("📢 Broadcasting message to WebSocket: " + messageJson);

        kafkaTemplate.send(topic, messageJson);
        
        broadcastToConversation(conversationId, new MessageDTO(conversationId, senderId, receiverId, content, username));

        // แทนที่จะส่ง Map.of("status",...) กลับไป
        // ให้ส่ง 202 Accepted เปล่า ๆ แทน
        return ResponseEntity.accepted().build();
    }

    // ✅ ลบข้อความ
    @DeleteMapping("/{messageId}")
    public ResponseEntity<String> deleteMessage(
            @PathVariable UUID messageId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        UUID userId = userDetails.getUser().getId();
        boolean deleted = messageService.deleteMessage(messageId, userId);

        return deleted ? ResponseEntity.ok("Message deleted successfully")
                : ResponseEntity.status(403).body("You can only delete your own messages");
    }

    @PostMapping("/conversation")
    public ResponseEntity<Map<String, Object>> getOrCreateConversation(
            @RequestBody Map<String, String> payload,
            @RequestHeader("Authorization") String authToken) {

        System.out.println("🔍 Raw Authorization Header: " + authToken); // ✅ Debug JWT ที่ได้รับ

        Map<String, Object> response = new HashMap<>();

        try {
            String friendId = payload.get("friendId");

            // ✅ ตรวจสอบว่า token มี "Bearer " อยู่หรือไม่
            if (authToken == null || !authToken.startsWith("Bearer ")) {
                response.put("status", "error");
                response.put("message", "Invalid or missing token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // ✅ ตัดคำว่า "Bearer " ออกก่อนส่งไป decode
            String jwt = authToken.substring(7).trim();
            System.out.println("🔍 Clean JWT Token: " + jwt);

            String userId = jwtUtil.extractUserId(jwt);
            String conversationId = chatService.findOrCreateConversation(userId, friendId);

            // ✅ ส่ง response เป็น JSON
            response.put("status", "success");
            response.put("conversationId", conversationId);
            return ResponseEntity.ok(response);

        } catch (ExpiredJwtException e) {
            response.put("status", "error");
            response.put("message", "Token expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (JwtException e) {
            response.put("status", "error");
            response.put("message", "Invalid JWT token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ตัวอย่างใน Controller หรือ Service
    // เมื่อ user ส่งข้อความเข้ามา
    public void broadcastToConversation(UUID conversationId, MessageDTO messageDTO) {
        // broadcast ผ่าน /topic/conversation/{conversationId}
        try {
            String json = objectMapper.writeValueAsString(messageDTO);
            messagingTemplate.convertAndSend("/topic/conversation/" + conversationId, json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
