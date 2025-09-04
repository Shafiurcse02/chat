package com.chat.sr.listener;

import com.chat.sr.service.OnlineUserService;
import com.chat.sr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

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
        String username = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : null;

        System.out.println("🔌 User connected: " + username);

        if (username != null) {
            onlineUserService.addUser(username);
            broadcastOnlineUsers();
            userService.setUserOIsActiveStatus(username, true);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : null;

        if (username != null) {
            onlineUserService.removeUser(username);
            broadcastOnlineUsers();
            userService.setUserOIsActiveStatus(username, false);
            System.out.println("❌ User disconnected: " + username);
        }
    }

    private void broadcastOnlineUsers() {
        Set<String> onlineUsers = onlineUserService.getOnlineUsers();
        messagingTemplate.convertAndSend("/topic/online-users", onlineUsers);
    }
}
