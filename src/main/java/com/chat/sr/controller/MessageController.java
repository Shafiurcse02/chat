package com.chat.sr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chat.sr.model.ChatMessage;
import com.chat.sr.repo.ChatMessageRepository;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

	@Autowired
	private ChatMessageRepository chatMessageRepository;
	
	public ResponseEntity<List<ChatMessage>> getPriavteMesages(@RequestParam String user1,@RequestParam String user2) {
		List<ChatMessage> messages=chatMessageRepository.findPrivateMessageBetweenuser1AndUSer2(user1,user2);
		return ResponseEntity.ok(messages);
		
	}
	



}
