package com.example.demo.service;

import com.example.demo.Cache.AppCache;
import com.example.demo.model.CarInfo;
import com.example.demo.repository.CarInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarInfoService {
    private final CarInfoRepository repository;
    private final AppCache cache;

    @Autowired
    public CarInfoService(CarInfoRepository repository, AppCache cache) {
        this.repository = repository;
        this.cache = cache;
    }

    public List<CarInfo> getCarsByMakeAndYear(String make, int minYear) {
        String cacheKey = String.format("cars_make_%s_year_%d", make, minYear);
        List<CarInfo> cached = cache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<CarInfo> cars = repository.findByMakeAndYearAfter(make, minYear);
        cache.put(cacheKey, cars);
        return cars;
    }

    public CarInfo getCarByVin(String vin) {
        String cacheKey = "car_" + vin;
        CarInfo cached = cache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        CarInfo car = repository.findById(vin).orElse(null);
        if (car != null) {
            cache.put(cacheKey, car);
        }
        return car;
    }

    public CarInfo addCar(CarInfo carInfo) {
        CarInfo saved = repository.save(carInfo);
        cache.put("car_" + saved.getVin(), saved);
        clearCachedLists();
        return saved;
    }

    public CarInfo updateCar(String vin, CarInfo car) {
        if (!repository.existsById(vin)) {
            return null;
        }
        car.setVin(vin);
        CarInfo updated = repository.save(car);
        cache.put("car_" + vin, updated);
        clearCachedLists();
        return updated;
    }

    public void deleteCar(String vin) {
        repository.deleteById(vin);
        cache.evict("car_" + vin);
        clearCachedLists();
    }

    public List<CarInfo> getCarsByOwnerName(String name) {
        String cacheKey = "cars_by_owner_" + name.hashCode();
        List<CarInfo> cached = cache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<CarInfo> cars = repository.findByOwnerName(name);
        cache.put(cacheKey, cars);
        return cars;
    }

    public List<CarInfo> getAllCars() {
        String cacheKey = "all_cars";
        List<CarInfo> cached = cache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<CarInfo> cars = repository.findAll();
        cache.put(cacheKey, cars);
        return cars;
    }

    private void clearCachedLists() {
        List<String> allKeys = cache.getAllKeys();
        if (allKeys != null) {
            allKeys.stream()
                    .filter(key -> key.startsWith("cars_make_") ||
                            key.startsWith("cars_by_owner_") ||
                            key.equals("all_cars"))
                    .forEach(cache::evict);
        }
    }
}