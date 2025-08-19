package com.chat.sr.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "chat_messages")
public class ChatMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String content;

	private String sender;
	private String recipients;
	private String colors;

	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime localDateTime;

	@Enumerated(EnumType.STRING)
	private MessageType msgtype;

	public enum MessageType {
		CHAT, TYPING, PRIVATE_MESSAGE, JOIN, LEAVE
	}
	
	
}
