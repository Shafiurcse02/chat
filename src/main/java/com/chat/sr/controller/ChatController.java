package com.chat.sr.controller;

import java.security.Principal;
import java.time.LocalDateTime;

import com.chat.sr.dto.TypingMessage;
import com.chat.sr.model.ChatMessage;
import com.chat.sr.repo.ChatMessageRepository;
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
    private ChatMessageRepository chatMessageRepository;

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

        if (userService.userExists(authenticatedUser) && userService.userExists(chatMessage.getRecipients())) {
            if (chatMessage.getLocalDateTime() == null) {
                chatMessage.setLocalDateTime(LocalDateTime.now());
            }
            if (chatMessage.getContent() == null) {
                chatMessage.setContent("");
            }

            chatMessage.setType(ChatMessage.MessageType.PRIVATE_MESSAGE);
            ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

            try {
                messagingTemplate.convertAndSendToUser(chatMessage.getRecipients(), "/queue/private", savedMessage);
                messagingTemplate.convertAndSendToUser(authenticatedUser, "/queue/private", savedMessage);
                logger.info("📩 Private message sent to {} and {}", chatMessage.getRecipients(), authenticatedUser);
            } catch (Exception e) {
                logger.error("❌ Error while sending message: {}", e.getMessage(), e);
            }
        } else {
            logger.warn("⚠️ Sender [{}] or Recipient [{}] not exists", authenticatedUser, chatMessage.getRecipients());
        }
    }

    // ✅ Typing indicator
    @MessageMapping("/typing")
    @SendTo("/topic/typing")
    public TypingMessage sendTyping(TypingMessage typing, Principal principal) {
        typing.setSender(principal.getName());
        return typing;
    }

}
