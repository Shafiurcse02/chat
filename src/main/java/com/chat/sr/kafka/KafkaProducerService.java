package com.chat.sr.kafka;

import com.chat.sr.model.ChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendMessage(String topic, ChatMessage message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(topic, jsonMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void sendOnlineUsersUpdate(Set<String> onlineUsers) {
        // Convert Set to JSON string (or just join if you prefer)
        String message = String.join(",", onlineUsers);
        kafkaTemplate.send("online.presence", message);
    }
}
