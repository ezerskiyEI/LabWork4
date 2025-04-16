package com.example.demo.Cache;

import com.example.demo.model.CarInfo;
import com.example.demo.model.Owner;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AppCache {

    private final Map<String, CarInfo> carCache = new ConcurrentHashMap<>();
    private final Map<Long, Owner> ownerCache = new ConcurrentHashMap<>();

    // ==== CarInfo ====

    public CarInfo getCar(String vin) {
        return carCache.get(vin);
    }

    public void putCar(String vin, CarInfo carInfo) {
        carCache.put(vin, carInfo);
    }

    public void evictCar(String vin) {
        carCache.remove(vin);
    }

    // ==== Owner ====

    public Owner getOwner(Long id) {
        return ownerCache.get(id);
    }

    public void putOwner(Long id, Owner owner) {
        ownerCache.put(id, owner);
    }

    public void evictOwner(Long id) {
        ownerCache.remove(id);
    }

    // ==== Общая очистка ====

    public void clearAll() {
        carCache.clear();
        ownerCache.clear();
    }
}