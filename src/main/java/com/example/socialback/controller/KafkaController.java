package com.example.socialback.controller;

import com.example.socialback.model.dto.PostLikeEvent;
import com.example.socialback.service.KafkaProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kafka")
public class KafkaController {
    private final KafkaProducerService kafkaProducerService;

    public KafkaController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestParam String message) {
        kafkaProducerService.sendMessage("TestTopic", message);
        return ResponseEntity.ok("Message sent to Kafka: " + message);
    }

    @PostMapping("/like")
    public ResponseEntity<String> sendLikeEvent(@RequestBody PostLikeEvent event) {
        kafkaProducerService.sendLikeEvent(event);
        return ResponseEntity.ok("Like event sent to Kafka: " + event);
    }
}
