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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {
	
	  @Autowired
	    private SimpMessageSendingOperations messagingTemplate;  // একটাই যথেষ্ট
	@Autowired
	private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    // Keep track of connected users: sessionId -> username
    private final Map<String, String> activeUsers = new ConcurrentHashMap<>();

    // Call this when a user connects (you can do it in your login/subscribe logic)
    public void registerUser(String sessionId, String username) {
        if (sessionId != null && username != null) {
            activeUsers.put(sessionId, username);
        }
    }
	// যখন কেউ connect করে
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("🔗 New WebSocket connection established");
    }
    
    // যখন কেউ disconnect করে
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        if (event == null) return;

        try {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
            String sessionId = accessor.getSessionId();

            if (sessionId != null && activeUsers.containsKey(sessionId)) {
                String username = activeUsers.remove(sessionId);
                // Optional: broadcast user left message to other clients
                System.out.println("User disconnected: " + username + " (session: " + sessionId + ")");
            }

        } catch (Exception e) {
            // Catch all exceptions to prevent the ERROR log
            System.err.println("Error handling session disconnect: " + e.getMessage());
        }
    }

    // Optional helper to get all active users
    public Map<String, String> getActiveUsers() {
        return activeUsers;
    }


}
