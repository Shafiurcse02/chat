package com.chat.sr.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chat.sr.model.ChatMessage;
import com.chat.sr.repo.ChatMessageRepository;
@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @GetMapping("/private")
    public ResponseEntity<List<ChatMessage>> getPrivateMessages(
            @RequestParam String user1,
            @RequestParam String user2,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean asc,
            Principal principal
    ) {
        String authenticatedUser = principal.getName();
        if (!authenticatedUser.equals(user1) && !authenticatedUser.equals(user2)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Pageable pageable = PageRequest.of(page, size,
                asc ? Sort.by("localDateTime").ascending() : Sort.by("localDateTime").descending()
        );

        Page<ChatMessage> messagesPage = chatMessageRepository.findPrivateMessagesBetween(user1, user2, pageable);

        logger.info("Fetched {} private messages between [{}] and [{}]", messagesPage.getNumberOfElements(), user1, user2);
        List<ChatMessage> content = new ArrayList<>(messagesPage.getContent());

        Collections.reverse(content);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/public")
    public ResponseEntity<List<ChatMessage>> getPublicMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean asc
    ) {
        Pageable pageable = PageRequest.of(page, size,
                asc ? Sort.by("localDateTime").ascending() : Sort.by("localDateTime").descending()
        );

        Page<ChatMessage> messagesPage = chatMessageRepository.findPublicMessages(pageable);
        List<ChatMessage> content = new ArrayList<>(messagesPage.getContent());

        Collections.reverse(content);

        logger.info("Fetched {} public messages", content);

        return ResponseEntity.ok(content);
    }

}
