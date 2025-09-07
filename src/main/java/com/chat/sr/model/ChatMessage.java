package com.chat.sr.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
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
	private String receiver;
	private String colors;

    @CreationTimestamp
    @Column(updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime localDateTime;

	@Enumerated(EnumType.STRING)
	private MessageType type;

	public enum MessageType {
		CHAT, TYPING, PRIVATE, JOIN, LEAVE
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}


	public String getColors() {
		return colors;
	}

	public void setColors(String colors) {
		this.colors = colors;
	}

	public LocalDateTime getLocalDateTime() {
		return localDateTime;
	}

	public void setLocalDateTime(LocalDateTime localDateTime) {
		this.localDateTime = localDateTime;
	}

	public MessageType getMsgtype() {
		return type;
	}

	public void setMsgtype(MessageType msgtype) {
		this.type = msgtype;
	}
	
	
	
	
}
