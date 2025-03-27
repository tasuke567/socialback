package com.example.socialback.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.example.socialback.service.MessageService;
import java.util.UUID;

@Service
public class ChatKafkaConsumer {
    private final ObjectMapper objectMapper;
    private final MessageService messageService;

    public ChatKafkaConsumer(ObjectMapper objectMapper, MessageService messageService) {
        this.objectMapper = objectMapper;
        this.messageService = messageService;
    }

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void consumeChatMessage(String messageJson) {
        try {
            System.out.println("📥 Raw Kafka Message: " + messageJson);

            // แปลง JSON เป็น Map
            Map<String, String> message = objectMapper.readValue(messageJson, Map.class);
            System.out.println("📥 Parsed Kafka Message: " + message);

            // ดึงค่าที่ต้องใช้
            String conversationId = message.get("conversationId");
            String senderId = message.get("senderId");
            String receiverId = message.get("receiverId");
            String content = message.get("content");
            String username = message.get("username");

            System.out.println("📢 Broadcasting to WebSockettttttttttttttttttttt: " + messageJson);

            // เช็คถ้ามีอันไหนเป็น null จะได้ไม่ล้ม
            if (conversationId == null || senderId == null || receiverId == null || content == null) {
                System.err.println("❌ Missing required fields in Kafka message!"
                        + " conversationId=" + conversationId
                        + ", senderId=" + senderId
                        + ", receiverId=" + receiverId
                        + ", content=" + content
                );
                return;
            }

            // Debug Log
            System.out.println("🛠 Saving to DB -> conversationId: " + conversationId
                    + ", senderId: " + senderId
                    + ", receiverId: " + receiverId
                    + ", content: " + content);

            // เรียก service ส่ง message
            messageService.sendMessage( 
                UUID.fromString(conversationId),
                UUID.fromString(senderId),
                UUID.fromString(receiverId),
                content,
                username
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
