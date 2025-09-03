package com.chat.sr.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TypingMessage {
    private String sender;
    private String receiver; // null মানে public chat

}
