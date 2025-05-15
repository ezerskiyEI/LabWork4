package com.example.demo.service;

import com.example.demo.Cache.AppCache;
import com.example.demo.model.CarInfo;
import com.example.demo.repository.CarInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

    public Optional<CarInfo> getCarByVin(String vin) {
        Optional<CarInfo> cachedCar = cache.getCar(vin);
        if (cachedCar.isPresent()) {
            return cachedCar;
        }
        Optional<CarInfo> dbCar = repository.findById(vin);
        dbCar.ifPresent(car -> cache.putCar(car));
        return dbCar;
    }

    public List<CarInfo> getCarsByVinsBulk(List<String> vins) {
        return vins.stream()
                .map(this::getCarByVin)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public CarInfo addCar(CarInfo car) {
        CarInfo saved = repository.save(car);
        cache.putCar(saved);
        cache.evictAllCarLists();
        cache.evictAllOwnerLists();
        return saved;
    }

    public List<CarInfo> addCarsBulk(List<CarInfo> cars) {
        return cars.stream()
                .map(this::addCar)
                .collect(Collectors.toList());
    }

    public Optional<CarInfo> updateCar(String vin, CarInfo car) {
        if (!repository.existsById(vin)) {
            return Optional.empty();
        }
        car.setVin(vin);
        CarInfo updated = repository.save(car);
        cache.putCar(updated);
        cache.evictAllCarLists();
        cache.evictAllOwnerLists();
        return Optional.of(updated);
    }

    public List<CarInfo> updateCarsBulk(List<CarInfo> cars) {
        return cars.stream()
                .map(car -> updateCar(car.getVin(), car))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public boolean deleteCar(String vin) {
        if (!repository.existsById(vin)) {
            return false;
        }
        repository.deleteById(vin);
        cache.evictCar(vin);
        cache.evictAllCarLists();
        cache.evictAllOwnerLists();
        return true;
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