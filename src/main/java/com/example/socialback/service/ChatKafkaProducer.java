package com.example.socialback.service;

import com.example.socialback.model.dto.ChatMessageEvent;
import com.nimbusds.jose.shaded.gson.Gson;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Gson gson = new Gson();

    public ChatKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendChatMessage(ChatMessageEvent chatMessage) {
        String messageJson = gson.toJson(chatMessage);
        kafkaTemplate.send("chat-messages", messageJson);
        System.out.println("💬 Sent chat message: " + messageJson);
    }
}
