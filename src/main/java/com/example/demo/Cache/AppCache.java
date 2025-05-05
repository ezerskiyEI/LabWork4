package com.example.demo.Cache;

import com.example.demo.model.CarInfo;
import com.example.demo.model.Owner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class AppCache {
    private final Map<String, CarInfo> carCache = new HashMap<>();
    private final Map<String, List<CarInfo>> carListCache = new HashMap<>();
    private final Map<Long, Owner> ownerCache = new HashMap<>();
    private final Map<String, List<Owner>> ownerListCache = new HashMap<>();

    // Методы для CarInfo
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

    // Методы для Owner
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

    // Общие методы
    public void clearAll() {
        carCache.clear();
        carListCache.clear();
        ownerCache.clear();
        ownerListCache.clear();
    }
}