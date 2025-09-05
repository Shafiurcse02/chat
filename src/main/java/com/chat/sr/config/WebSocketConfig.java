package com.chat.sr.config;

import com.chat.sr.controller.ChatController;
import com.chat.sr.security.CustomUserDetailsService;
import com.chat.sr.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    public JwtUtils jwtUtils;

        @Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic", "/queue");
		config.setApplicationDestinationPrefixes("/app");
		config.setUserDestinationPrefix("/user");
	}

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request,
                                                      WebSocketHandler wsHandler,
                                                      Map<String, Object> attributes) {
                        // ✅ Extract username from JWT
                        String username = extractUsernameFromJwt(request);
                        if (username == null) {
                            logger.warn("JWT not found in handshake request.");
                            return null;
                        }

                        try {
                            // ✅ Validate user exists
                            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                            // ✅ Return authenticated principal
                            return new UsernamePasswordAuthenticationToken(
                                    userDetails.getUsername(),
                                    null,
                                    userDetails.getAuthorities()
                            );
                        } catch (UsernameNotFoundException e) {
                            logger.warn("User not found during WebSocket handshake: ");
                            return null; // Deny WebSocket connection
                        }
                    }
                })
                .setAllowedOrigins("http://localhost:5173")
                .withSockJS(); // Or remove this if you're not using SockJS
    }


    private String extractUsernameFromJwt(ServerHttpRequest request) {
        List<String> cookieHeaders = request.getHeaders().get("Cookie");
        if (cookieHeaders == null || cookieHeaders.isEmpty()) return null;

        for (String header : cookieHeaders) {
            String[] cookies = header.split(";");
            for (String cookie : cookies) {
                String[] pair = cookie.trim().split("=", 2);
                if (pair.length == 2 && pair[0].trim().equals("jwt")) {
                    return jwtUtils.extractUsername(pair[1].trim());
                }
            }
        }
        return null;
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    Principal user = accessor.getUser();

                    if (user != null) {
                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                user.getName(), null, List.of()); // optionally load roles
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        accessor.setUser(authentication);
                    } else {
                        // 🔒 Prevent unauthenticated connections from proceeding
                        logger.warn("WebSocket CONNECT attempt without authenticated Principal.");
                        return null; // Reject the message and close connection
                    }
                }

                return message;
            }
        });
    }

}
