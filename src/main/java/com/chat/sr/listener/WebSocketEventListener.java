package com.chat.sr.listener;

import com.chat.sr.service.OnlineUserService;
import com.chat.sr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Set;

@Component
public class WebSocketEventListener {

    @Autowired
    private OnlineUserService onlineUserService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private UserService userService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = getUsernameFromHeader(headerAccessor);

        if (username != null) {
            System.out.println("🔌 User connected: " + username);
            onlineUserService.addUser(username);                        // Redis call
            userService.setUserOIsActiveStatus(username, true);        // DB status
            broadcastOnlineUsers();                                    // Notify all
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = getUsernameFromHeader(headerAccessor);

        if (username != null) {
            System.out.println("❌ User disconnected: " + username);
            onlineUserService.removeUser(username);                    // Redis call
            userService.setUserOIsActiveStatus(username, false);       // DB status
            broadcastOnlineUsers();                                    // Notify all
        }
        Principal principal = StompHeaderAccessor.wrap(event.getMessage()).getUser();
        if (principal != null) {
            onlineUserService.removeUser(principal.getName());
            // Also clear security context if needed
            SecurityContextHolder.clearContext();
        }
    }

    private void broadcastOnlineUsers() {
        Set<String> onlineUsers = onlineUserService.getOnlineUsers();  // Redis set
        messagingTemplate.convertAndSend("/topic/online-users", onlineUsers);

    }

    private String getUsernameFromHeader(StompHeaderAccessor accessor) {
        return accessor.getUser() != null ? accessor.getUser().getName() : null;
    }


}
