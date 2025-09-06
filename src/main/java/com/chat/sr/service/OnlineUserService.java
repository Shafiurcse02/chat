

package com.chat.sr.service;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class OnlineUserService {

    private static final String ONLINE_USERS_KEY = "chat:online-users";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void addUser(String username) {
        redisTemplate.opsForSet().add(ONLINE_USERS_KEY, username);
        System.out.println("[Presence] User online: " + username);
    }

    public void removeUser(String username) {
        redisTemplate.opsForSet().remove(ONLINE_USERS_KEY, username);
        System.out.println("[Presence] User offline: " + username);
    }

    public Set<String> getOnlineUsers() {
        return redisTemplate.opsForSet().members(ONLINE_USERS_KEY);
    }

    public boolean isOnline(String username) {
        Boolean result = redisTemplate.opsForSet().isMember(ONLINE_USERS_KEY, username);
        return result != null && result;
    }

    @PreDestroy
    public void clearOnlineUsers() {
        redisTemplate.delete(ONLINE_USERS_KEY);
        System.out.println("[Presence] Online users cleared on shutdown.");
    }
}

