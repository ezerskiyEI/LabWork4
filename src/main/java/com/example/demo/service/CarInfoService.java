package com.example.demo.service;

import com.example.demo.Cache.AppCache;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.CarInfo;
import com.example.demo.repository.CarInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarInfoService {
    private final CarInfoRepository repository;
    private final AppCache cache;

    @Autowired
    public CarInfoService(CarInfoRepository repository, AppCache cache) {
        this.repository = repository;
        this.cache = cache;
    }

    public List<CarInfo> findByYearAndMake(int year, String make) {
        String cacheKey = "cars_by_year_" + year + "_make_" + make;
        return cache.getCarList(cacheKey)
                .orElseGet(() -> {
                    List<CarInfo> cars = repository.findByYearAndMake(year, make);
                    cache.putCarList(cacheKey, cars);
                    return cars;
                });
    }

    public List<CarInfo> findByOwnerId(Long ownerId) {
        String cacheKey = "cars_by_owner_" + ownerId;
        return cache.getCarList(cacheKey)
                .orElseGet(() -> {
                    List<CarInfo> cars = repository.findByOwnerId(ownerId);
                    cache.putCarList(cacheKey, cars);
                    return cars;
                });
    }

    public CarInfo getCarByVin(String vin) {
        return cache.getCar(vin)
                .orElseGet(() -> {
                    CarInfo car = repository.findById(vin)
                            .orElseThrow(() -> new NotFoundException("Car not found with VIN: " + vin));
                    cache.putCar(car);
                    return car;
                });
    }

    public CarInfo addCar(CarInfo car) {
        CarInfo saved = repository.save(car);
        cache.putCar(saved);
        cache.evictAllCarLists();
        cache.evictAllOwnerLists();
        return saved;
    }

    public CarInfo updateCar(String vin, CarInfo car) {
        if (!repository.existsById(vin)) {
            throw new NotFoundException("Car not found with VIN: " + vin);
        }
        car.setVin(vin);
        CarInfo updated = repository.save(car);
        cache.putCar(updated);
        cache.evictAllCarLists();
        cache.evictAllOwnerLists();
        return updated;
    }

    public void deleteCar(String vin) {
        repository.deleteById(vin);
        cache.evictCar(vin);
        cache.evictAllCarLists();
        cache.evictAllOwnerLists();
    }

    public List<CarInfo> getAllCars() {
        return cache.getCarList("all_cars")
                .orElseGet(() -> {
                    List<CarInfo> cars = repository.findAll();
                    cache.putCarList("all_cars", cars);
                    return cars;
                });
    }
}