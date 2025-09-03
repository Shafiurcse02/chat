package com.chat.sr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    // ✅ Private messages API
    @GetMapping("/private")
    public ResponseEntity<List<ChatMessage>> getPrivateMessages(
            @RequestParam String user1,
            @RequestParam String user2) {

        List<ChatMessage> messages =
                chatMessageRepository.findPrivateMessageBetweenUser1AndUser2(user1, user2);

        return ResponseEntity.ok(messages);
    }
}
