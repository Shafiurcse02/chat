package com.chat.sr.controller;

import java.time.LocalDateTime;

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

@Controller
public class ChatController {

	@Autowired
	private SimpMessageSendingOperations messagingTemplate1;
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private UserService userService;

	@Autowired
	private ChatMessageRepository chatMessageRepository;

	// নতুন ইউজার যোগ হলে "/app/chat.addUser" এ কল হবে
	@MessageMapping("/chat.addUser")
	@SendTo("/topic/public")
	public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		if (userService.userExists(chatMessage.getSender())) {
			headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
			userService.setUserOnlineStatus(chatMessage.getSender(), true);
			System.out.println("User Added Successfully " + chatMessage.getSender() + " session id is"
					+ headerAccessor.getSessionId());
			chatMessage.setLocalDateTime(LocalDateTime.now());
			if (chatMessage.getContent() == null) {
				chatMessage.setContent("");
			}
		}

		return chatMessageRepository.save(chatMessage);
	}

	// নতুন মেসেজ আসলে "/app/chat.sendMessage" এ কল হবে
	@MessageMapping("/chat.sendMessage")
	@SendTo("/topic/public")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {

		if (userService.userExists(chatMessage.getSender())) {
			if (chatMessage.getLocalDateTime() == null) {
				chatMessage.setLocalDateTime(LocalDateTime.now());
			}
			if (chatMessage.getContent() == null) {
				chatMessage.setContent("");
			}
			return chatMessageRepository.save(chatMessage);
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
				String recipintDestination = "/user/" + chatMessage.getRecipients() + "/queue/private";
				System.out.println("Sending message to Recipint Destination " + recipintDestination);
				messagingTemplate.convertAndSend(recipintDestination, savedMessage);

				String senderDestination = "/user/" + chatMessage.getSender() + "/queue/private";
				System.out.println("Sending message to Sender Destination " + senderDestination);
				messagingTemplate.convertAndSend(senderDestination, savedMessage);

			} catch (Exception e) {
				System.out.println("Error to send Message: " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			System.out.println(
					"Sender " + chatMessage.getSender() + " or " + chatMessage.getRecipients() + " Not Exists");
			;
		}
	}

}
