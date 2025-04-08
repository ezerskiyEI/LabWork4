package com.example.demo.Cache;

import com.example.demo.model.CarInfo;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CarCache {

    private final Map<String, CarInfo> cache = new ConcurrentHashMap<>();

    public CarInfo get(String vin) {
        return cache.get(vin);
    }

    public void put(String vin, CarInfo carInfo) {
        cache.put(vin, carInfo);
    }

    public void remove(String vin) {
        cache.remove(vin);
    }

    public boolean contains(String vin) {
        return cache.containsKey(vin);
    }
}