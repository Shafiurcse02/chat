package com.chat.sr.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtHandshakeInterceptor.class);

    private final JwtUtils jwtUtils;

    public JwtHandshakeInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        if (!(request instanceof ServletServerHttpRequest)) {
            logger.warn("Handshake request is not an instance of ServletServerHttpRequest");
            return false;
        }

        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

        // Cookie থেকে JWT token বের করা
        String token = null;
        if (servletRequest.getCookies() != null) {
            for (Cookie cookie : servletRequest.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null) {
            logger.warn("JWT token not found in cookies during WebSocket handshake");
            return false;  // deny handshake
        }

        // টোকেন validate করা
        if (!jwtUtils.validateWithToken(token)) {
            logger.warn("Invalid JWT token during WebSocket handshake");
            return false;  // deny handshake
        }

        // ইউজারনেম extract করে attributes-এ সংরক্ষণ (optional)
        String username = jwtUtils.extractUsername(token);
        attributes.put("username", username);

        logger.info("WebSocket handshake authorized for user: {}", username);

        return true; // allow handshake
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // হ্যান্ডশেকের পর লগিং করতে চাইলে
        System.out.println("WebSocket handshake completed.");

        // যদি কোনো exception থাকে, সেটা লগ করতে পারো
        if (exception != null) {
            System.err.println("Handshake exception: " + exception.getMessage());
        }

        // অথবা এখানে অন্য কোনো প্রয়োজনীয় কাজ করতে পারো
    }

}
