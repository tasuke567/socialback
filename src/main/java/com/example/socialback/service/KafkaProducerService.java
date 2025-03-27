package com.example.socialback.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.socialback.model.dto.PostLikeEvent;
import com.nimbusds.jose.shaded.gson.Gson;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
        System.out.println("✅ Sent message: " + message);
    }
    public void sendLikeEvent(PostLikeEvent event) {    
        String message = new Gson().toJson(event);
        kafkaTemplate.send("post-likes", message);
    }
    
}
