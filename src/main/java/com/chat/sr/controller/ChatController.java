package com.chat.sr.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.chat.sr.dto.TypingMessage;
import com.chat.sr.kafka.KafkaProducerService;
import com.chat.sr.model.ChatMessage;
import com.chat.sr.repo.ChatMessageRepository;
import com.chat.sr.service.OnlineUserService;
import com.chat.sr.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

@Controller
public class ChatController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private OnlineUserService onlineUserService;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private KafkaProducerService kafkaProducerService;

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    // ✅ User join
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor,
                               Principal principal) {
        try {
            String authenticatedUser = principal.getName();
            chatMessage.setSender(authenticatedUser);

            if (userService.userExists(authenticatedUser)) {
                headerAccessor.getSessionAttributes().put("username", authenticatedUser);
                userService.setUserOIsActiveStatus(authenticatedUser, true);

                logger.info("👤 New User Added: {} | Session ID: {}", authenticatedUser, headerAccessor.getSessionId());

                chatMessage.setLocalDateTime(LocalDateTime.now());
                if (chatMessage.getContent() == null) {
                    chatMessage.setContent("");
                }

                ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
                logger.debug("💾 User join message saved with ID: {}", savedMessage.getId());

                return savedMessage;
            } else {
                logger.warn("⚠️ Tried to add user [{}], but user does not exist!", authenticatedUser);
            }
        } catch (Exception e) {
            logger.error("❌ Error while adding user [{}]: {}", chatMessage.getSender(), e.getMessage(), e);
        }
        return null;
    }

    // ✅ Public chat message
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        try {
            String authenticatedUser = principal.getName();
            chatMessage.setSender(authenticatedUser);

            if (userService.userExists(authenticatedUser)) {
                if (chatMessage.getLocalDateTime() == null) {
                    chatMessage.setLocalDateTime(LocalDateTime.now());
                }
                if (chatMessage.getContent() == null) {
                    chatMessage.setContent("");
                }
                if (chatMessage.getType() == null) {
                    chatMessage.setType(ChatMessage.MessageType.CHAT);
                }

                ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
                logger.info("✅ Public message saved. Sender: {}, ID: {}", savedMessage.getSender(), savedMessage.getId());
                return savedMessage;
            } else {
                logger.warn("⚠️ Authenticated user [{}] does not exist. Message rejected.", authenticatedUser);
            }
        } catch (Exception e) {
            logger.error("❌ Error while saving/sending public message: {}", e.getMessage(), e);
        }
        return null;
    }

    // ✅ Private message
    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage, Principal principal) {
        String authenticatedUser = principal.getName();
        chatMessage.setSender(authenticatedUser);
chatMessage.setLocalDateTime(LocalDateTime.now());
        if (authenticatedUser.equals(chatMessage.getReceiver())) {
            logger.warn("⚠️ User [{}] tried to send a message to themselves. Ignored.", authenticatedUser);
            return;
        }

        if (!userService.userExists(authenticatedUser) || !userService.userExists(chatMessage.getReceiver())) {
            logger.warn("⚠️ Sender [{}] or Recipient [{}] not exists", authenticatedUser, chatMessage.getReceiver());
            return;
        }

        if (chatMessage.getContent() == null || chatMessage.getContent().trim().isEmpty()) {
            logger.warn("⚠️ Empty message content from {}", authenticatedUser);
            return;
        }

        if (chatMessage.getLocalDateTime() == null) {
            chatMessage.setLocalDateTime(LocalDateTime.now());
        }

        chatMessage.setType(ChatMessage.MessageType.PRIVATE);
        logger.info("📩 Private message (to be sent):  {}", chatMessage);

        try {
            ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
            kafkaProducerService.sendMessage("chat.messages", savedMessage);
            logger.info("📩 Private message sent to {} and {}", savedMessage.getReceiver(), authenticatedUser);
        } catch (Exception e) {
            logger.error("❌ Error while sending message: {}", e.getMessage(), e);
        }
    }

    // ✅ Typing indicator
    @MessageMapping("/typing")
    @SendTo("/topic/typing")
    public TypingMessage sendTyping(TypingMessage typing, Principal principal) {
        typing.setSender(principal.getName());
        return typing;
    }

    @MessageMapping("/online-users")
    public void sendOnlineUsers(SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        String authenticatedUser = principal.getName();
        Set<String> onlineUsers = new HashSet<>(onlineUserService.getOnlineUsers());
        kafkaProducerService.sendOnlineUsersUpdate(onlineUsers);

    }


}
