package com.example.demo.Cache;

import com.example.demo.model.CarInfo;
import com.example.demo.model.Owner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AppCache {
    private static final int MAX_CACHE_SIZE = 1000;

    private final Map<String, CarInfo> carCache = new ConcurrentHashMap<>();
    private final Map<String, List<CarInfo>> carListCache = new ConcurrentHashMap<>();
    private final Map<Long, Owner> ownerCache = new ConcurrentHashMap<>();
    private final Map<String, List<Owner>> ownerListCache = new ConcurrentHashMap<>();
    private final Map<String, Optional<CarInfo>> analyzedTextCache = new ConcurrentHashMap<>();

    // CarInfo methods
    public Optional<CarInfo> getCar(String vin) {
        return Optional.ofNullable(carCache.get(vin));
    }

    public void putCar(CarInfo car) {
        carCache.put(car.getVin(), car);
    }

    public Optional<List<CarInfo>> getCarList(String key) {
        return Optional.ofNullable(carListCache.get(key));
    }

    public void putCarList(String key, List<CarInfo> cars) {
        carListCache.put(key, cars);
    }

    public void evictCar(String vin) {
        carCache.remove(vin);
    }

    public void evictAllCarLists() {
        carListCache.clear();
    }

    // Owner methods
    public Optional<Owner> getOwner(Long id) {
        return Optional.ofNullable(ownerCache.get(id));
    }

    public void putOwner(Owner owner) {
        ownerCache.put(owner.getId(), owner);
    }

    public Optional<List<Owner>> getOwnerList(String key) {
        return Optional.ofNullable(ownerListCache.get(key));
    }

    public void putOwnerList(String key, List<Owner> owners) {
        ownerListCache.put(key, owners);
    }

    public void evictOwner(Long id) {
        ownerCache.remove(id);
    }

    public void evictAllOwnerLists() {
        ownerListCache.clear();
    }

    // Text analysis cache methods
    public Optional<CarInfo> getAnalyzedText(String text) {
        return analyzedTextCache.getOrDefault(text, Optional.empty());
    }

    public void putAnalyzedText(String text, Optional<CarInfo> carInfo) {
        if (analyzedTextCache.size() >= MAX_CACHE_SIZE) {
            analyzedTextCache.clear();
        }
        analyzedTextCache.put(text, carInfo);
    }

    public void evictAnalyzedText(String text) {
        analyzedTextCache.remove(text);
    }

    public void evictAllAnalyzedTexts() {
        analyzedTextCache.clear();
    }

    // General methods
    public void clearAll() {
        carCache.clear();
        carListCache.clear();
        ownerCache.clear();
        ownerListCache.clear();
        analyzedTextCache.clear();
    }
}