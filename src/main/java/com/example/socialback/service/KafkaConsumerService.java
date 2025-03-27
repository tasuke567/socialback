package com.example.socialback.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.socialback.model.dto.PostLikeEvent;
import com.nimbusds.jose.shaded.gson.Gson;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "TestTopic", groupId = "social-group")
    public void consumeMessage(String message) {
        System.out.println("📩 Received message: " + message);
    }

    @KafkaListener(topics = "post-likes", groupId = "social-group")
    public void consumeLikeEvent(String message) {
        PostLikeEvent event = new Gson().fromJson(message, PostLikeEvent.class);
        System.out.println("📩 User " + event.getUsername() + " liked post: " + event.getPostId());
        // บันทึกลง DB หรือ Redis ได้
    }

}
