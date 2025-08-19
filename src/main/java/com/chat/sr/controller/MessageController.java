package com.chat.sr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.chat.sr.model.ChatMessage;
import com.chat.sr.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
public class MessageController {

	@Autowired
	private UserService userService;

	@MessageMapping("chat.addUser")
	@SendTo("/topic/public")
	public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
	// If you want to print recipients, ensure ChatMessage has a getRecipients() method.
	// If not, you may need to access the appropriate field directly.
	// Example if ChatMessage has a recipients field:
	 System.out.println(chatMessage.getRecipients());

	// If getRecipients() does not exist, try:
	// System.out.println(chatMessage.recipients);

	// If neither exists, check your ChatMessage class for the correct way to access recipients.
		return new ChatMessage();
	}

	@MessageMapping("chat.sendMessage")
	@SendTo("/topic/public")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
		return chatMessage;
	}

	@MessageMapping("chat.sendPrivateMessage")
	public ChatMessage sendPrivateMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		return chatMessage;
	}

}
