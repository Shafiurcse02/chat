package com.chat.sr.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OnlineUserService {

    private static final String ONLINE_USERS_KEY = "chat:online-users";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void addUser(String username) {
        redisTemplate.opsForSet().add(ONLINE_USERS_KEY, username);
    }

    public void removeUser(String username) {
        redisTemplate.opsForSet().remove(ONLINE_USERS_KEY, username);
    }

    public Set<String> getOnlineUsers() {
        return redisTemplate.opsForSet().members(ONLINE_USERS_KEY);
    }

    public boolean isOnline(String username) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(ONLINE_USERS_KEY, username));
    }
    @PreDestroy
    public void clearOnlineUsers() {
        redisTemplate.delete(ONLINE_USERS_KEY);
    }



}
