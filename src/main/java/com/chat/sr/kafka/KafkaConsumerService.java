package com.chat.sr.kafka;

import com.chat.sr.controller.ChatController;
import com.chat.sr.model.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class KafkaConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // for WebSocket push
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaConsumerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    @KafkaListener(topics = "chat.messages", groupId = "chat-group")
    public void consume(String jsonMessage) {
        try {
            ChatMessage message = objectMapper.readValue(jsonMessage, ChatMessage.class);

            if (message.getType() == ChatMessage.MessageType.PRIVATE) {
                logger.info("👤 message in  consumer : {}", message);

                messagingTemplate.convertAndSendToUser(message.getReceiver(), "/queue/private", message);
                messagingTemplate.convertAndSendToUser(message.getSender(), "/queue/private", message);

            } else {
                messagingTemplate.convertAndSend("/topic/public", message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @KafkaListener(topics = "online.presence", groupId = "chat-group")
    public void listenOnlineUsers(String message) {
        // Convert CSV string back to Set
        Set<String> onlineUsers = new HashSet<>(Arrays.asList(message.split(",")));

        // Broadcast to WebSocket topic
        messagingTemplate.convertAndSend("/topic/online-users", onlineUsers);
    }
}
