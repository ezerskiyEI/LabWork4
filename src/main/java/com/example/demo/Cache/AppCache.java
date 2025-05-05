package com.example.demo.Cache;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AppCache {
    private final Map<String, Object> cacheStore = new ConcurrentHashMap<>();

    public <T> T get(String key) {
        return (T) cacheStore.get(key);
    }

    public void put(String key, Object value) {
        cacheStore.put(key, value);
    }

    public void evict(String key) {
        cacheStore.remove(key);
    }

    public void clear() {
        cacheStore.clear();
    }

    public List<String> getAllKeys() {
        return new ArrayList<>(cacheStore.keySet());
    }

    // Специализированные методы для Owner
    public <T> T getOwner(Long key) {
        return get("owner_" + key);
    }

    public void putOwner(Long key, Object value) {
        put("owner_" + key, value);
    }

    public void evictOwner(Long key) {
        evict("owner_" + key);
    }
}