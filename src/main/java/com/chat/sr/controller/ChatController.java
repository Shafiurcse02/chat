package com.chat.sr.controller;

import java.time.LocalDateTime;

import com.chat.sr.dto.TypingMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.chat.sr.model.ChatMessage;
import com.chat.sr.model.User;
import com.chat.sr.repo.ChatMessageRepository;
import com.chat.sr.service.UserService;
import com.chat.sr.service.UserServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ChatController {

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	@Autowired
	private UserService userService;

	@Autowired
	private ChatMessageRepository chatMessageRepository;
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);


    // নতুন ইউজার যোগ হলে "/app/chat.addUser" এ কল হবে
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        try {
            if (userService.userExists(chatMessage.getSender())) {

                headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
                userService.setUserOnlineStatus(chatMessage.getSender(), true);

                logger.info("👤 New User Added: {} | Session ID: {}",
                        chatMessage.getSender(), headerAccessor.getSessionId());

                chatMessage.setLocalDateTime(LocalDateTime.now());

                if (chatMessage.getContent() == null) {
                    chatMessage.setContent("");
                }
               // messagingTemplate.convertAndSend("/topic/public", chatMessage);

                ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
                logger.debug("💾 User join message saved with ID: {}", savedMessage.getId());

                return savedMessage;
            } else {
                logger.warn("⚠️ Tried to add user [{}], but user does not exist!", chatMessage.getSender());
            }

        } catch (Exception e) {
            logger.error("❌ Error while adding user [{}]: {}", chatMessage.getSender(), e.getMessage(), e);
        }

        return null;
    }

    // নতুন মেসেজ আসলে "/app/chat.sendMessage" এ কল হবে
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        System.out.println("Send message Controller Function");
        try {
            if (userService.userExists(chatMessage.getSender())) {

                if (chatMessage.getLocalDateTime() == null) {
                    chatMessage.setLocalDateTime(LocalDateTime.now());
                }
                if (chatMessage.getContent() == null) {
                    chatMessage.setContent("");
                }

                ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
                logger.info("✅ Public message saved. Sender: {}, ID: {}", savedMessage.getSender(), savedMessage.getId());
                return savedMessage;

            } else {
                logger.warn("⚠️ Sender [{}] does not exist. Message rejected.", chatMessage.getSender());
            }

        } catch (Exception e) {
            logger.error("❌ Error while saving/sending public message: {}", e.getMessage(), e);
        }
        return null;
    }


	@MessageMapping("/chat.sendPrivateMessage")
	public void sendPrivateMessage(@Payload ChatMessage chatMessage) {
		if (userService.userExists(chatMessage.getSender()) && userService.userExists(chatMessage.getRecipients())) {
			if (chatMessage.getLocalDateTime() == null) {
				chatMessage.setLocalDateTime(LocalDateTime.now());
			}
			if (chatMessage.getContent() == null) {
				chatMessage.setContent("");
			}
			
			chatMessage.setMsgtype(ChatMessage.MessageType.PRIVATE_MESSAGE);
			ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
			System.out.println("Message saved Successfully " + savedMessage.getId());
            try {
                // ✅ recipient এর কাছে পাঠানো
                messagingTemplate.convertAndSendToUser(
                        chatMessage.getRecipients(),
                        "/queue/private",
                        savedMessage
                );

                // ✅ sender এর কাছেও পাঠানো (to keep sync)
                messagingTemplate.convertAndSendToUser(
                        chatMessage.getSender(),
                        "/queue/private",
                        savedMessage
                );

                logger.info("📩 Private message sent to {} and {}", chatMessage.getRecipients(), chatMessage.getSender());
            } catch (Exception e) {
                logger.error("❌ Error while sending message: {}", e.getMessage(), e);
            }
        } else {
            logger.warn("⚠️ Sender [{}] or Recipient [{}] not exists",
                    chatMessage.getSender(),
                    chatMessage.getRecipients());
        }
	}
    // Typing notification
    @MessageMapping("/typing")
    @SendTo("/topic/typing")
    public TypingMessage sendTyping(TypingMessage typing) {
        return typing;
    }

}
