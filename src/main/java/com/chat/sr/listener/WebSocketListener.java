package com.chat.sr.listener;


import com.chat.sr.model.ChatMessage;
import com.chat.sr.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketListener {
    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessageSendingOperations messingTemplate;
    private static final Logger logger = LoggerFactory.getLogger(WebSocketListener.class);
    @EventListener
    public void handleWebSocketConnectionListener(SessionConnectedEvent sessionConnectedEvent){
logger.info("Connected to WebSocket");
    }
    public void handleWebSocketDisConnectionListener(SessionDisconnectEvent  sessionDisconnectEvent){
        StompHeaderAccessor  stompHeaderAccessor=StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage());
        String user=stompHeaderAccessor.getSessionAttributes().get("username").toString();
        userService.setUserOnlineStatus(user,false);

        System.out.println("User Disconnected from Websocket" );
        ChatMessage chatMessage= new ChatMessage();
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        chatMessage.setSender(user);

        messingTemplate.convertAndSend("/topic/public", chatMessage);
    }
}
