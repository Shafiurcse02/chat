package com.chat.sr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.chat.sr.model.ChatMessage;
import com.chat.sr.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
public class MessageController {
	
	@Autowired
	private UserService userService;

	@MessageMapping("chat.addUser")
	@SendTo("/topic/public")
	public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		if (userService.isExists(chatMessage.getSenderPerson())) {

		}
	}

	@MessageMapping("chat.sendMessage")
	@SendTo("/topic/public")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
		return chatMessage;
	}

	@MessageMapping("chat.sendPrivateMessage")
	public ChatMessage sendPrivateMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {

	}

}
