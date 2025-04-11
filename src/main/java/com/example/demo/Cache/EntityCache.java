package com.example.demo.Cache;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EntityCache {

    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    public <T> T get(String key, Class<T> type) {
        return type.cast(cache.get(key));
    }

    public void put(String key, Object value) {
        cache.put(key, value);
    }

    public void evict(String key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }

    public boolean contains(String key) {
        return cache.containsKey(key);
    }
}