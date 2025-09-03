package com.chat.sr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @GetMapping("/private")
    public ResponseEntity<List<ChatMessage>> getPrivateMessages(
            @RequestParam String user1,
            @RequestParam String user2,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "true") boolean asc
    ) {

        Pageable pageable = PageRequest.of(page, size,
                asc ? Sort.by("timestamp").ascending() : Sort.by("timestamp").descending()
        );

        Page<ChatMessage> messagesPage = chatMessageRepository.findPrivateMessagesBetween(user1, user2, pageable);

        return ResponseEntity.ok(messagesPage.getContent());
    }

}
