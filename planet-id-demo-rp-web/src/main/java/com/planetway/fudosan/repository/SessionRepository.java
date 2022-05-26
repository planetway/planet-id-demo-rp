package com.planetway.fudosan.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class SessionRepository {

    private Map<String, Object> storage = new HashMap<>();

    public void setAttribute(String key, Object value) {
        storage.put(key, value);
    }

    public Object get(String key) {
        return storage.get(key);
    }
}
