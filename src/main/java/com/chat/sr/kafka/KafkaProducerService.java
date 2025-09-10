package com.chat.sr.kafka;

import com.chat.sr.controller.ChatController;
import com.chat.sr.model.ChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
@Service
public class KafkaProducerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper; // ✅ Spring Bean থেকে ইনজেক্ট হবে

    public void sendMessage(String topic, ChatMessage message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);

            //logger.info("👤1111s message in producer: {}, {}", jsonMessage, message);

            kafkaTemplate.send(topic, jsonMessage);
           // logger.info("👤 2222 message in producer: {}, {}", jsonMessage, message);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void sendOnlineUsersUpdate(Set<String> onlineUsers) {
        String message = String.join(",", onlineUsers);
        kafkaTemplate.send("online.presence", message);
    }
}
