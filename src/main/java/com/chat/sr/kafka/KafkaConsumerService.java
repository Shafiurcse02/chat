package com.chat.sr.kafka;

import com.chat.sr.model.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class KafkaConsumerService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // for WebSocket push

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "chat.messages", groupId = "chat-group")
    public void consume(String jsonMessage) {
        try {
            ChatMessage message = objectMapper.readValue(jsonMessage, ChatMessage.class);

            if (message.getType() == ChatMessage.MessageType.PRIVATE) {
                // Send to receiver queue
                String receiverDestination = "/queue/private/" + message.getReceiver();
                messagingTemplate.convertAndSend(receiverDestination, message);

                // Also send to sender queue so sender sees the message
                String senderDestination = "/queue/private/" + message.getSender();
                messagingTemplate.convertAndSend(senderDestination, message);

            } else {
                // Public message goes to public topic
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
