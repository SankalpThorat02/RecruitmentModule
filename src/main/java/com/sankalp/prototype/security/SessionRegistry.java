package com.sankalp.prototype.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionRegistry {

    private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();

    public String getSessionId(String username) {
        return userSessionMap.get(username);
    }

    public void registerSession(String username, String sessionId) {
        userSessionMap.put(username, sessionId);
    }

    public void removeSession(String username) {
        userSessionMap.remove(username);
    }
}

