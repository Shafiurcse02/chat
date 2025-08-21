package com.chat.sr.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.chat.sr.model.ChatMessage;
import com.chat.sr.model.ChatMessage.MessageType;
import com.chat.sr.service.UserService;

@Component
public class WebSocketEventListener {
	
	  @Autowired
	    private SimpMessageSendingOperations messagingTemplate;  // একটাই যথেষ্ট
	@Autowired
	private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

	
	// যখন কেউ connect করে
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("🔗 New WebSocket connection established");
    }
    
    // যখন কেউ disconnect করে
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = headerAccessor.getSessionAttributes().get("username").toString();
        
        if (username != null) {
            logger.info("❌ User Disconnected: " + username);
            System.out.println("❌ User Disconnected: " + username);
            userService.setUserOnlineStatus(username, false); 
            ChatMessage chatMessage=new ChatMessage();
            chatMessage.setMsgtype(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }

}
