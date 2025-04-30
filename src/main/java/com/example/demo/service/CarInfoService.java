package com.example.demo.service;


import com.example.demo.Cache.AppCache;
import com.example.demo.model.CarInfo;
import com.example.demo.repository.CarInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarInfoService {

    @Autowired
    private CarInfoRepository carRepo;

    @Autowired
    private AppCache cache;

    public List<CarInfo> getAllCars() {
        return carRepo.findAll();
    }

    public CarInfo getCarByVin(String vin) {
        CarInfo cached = cache.getCar(vin);
        if (cached != null) return cached;

        return carRepo.findById(vin).map(car -> {
            cache.putCar(vin, car);
            return car;
        }).orElse(null);
    }

    public CarInfo addCar(CarInfo carInfo) {
        CarInfo saved = carRepo.save(carInfo);
        cache.putCar(saved.getVin(), saved);
        return saved;
    }

    public CarInfo updateCar(String vin, CarInfo car) {
        if (!carRepo.existsById(vin)) return null;
        car.setVin(vin);
        CarInfo updated = carRepo.save(car);
        cache.putCar(vin, updated);
        return updated;
    }

    public void deleteCar(String vin) {
        carRepo.deleteById(vin);
        cache.evictCar(vin);
    }

    public List<CarInfo> getCarsByOwnerName(String name) {
        return carRepo.findByOwnerName(name);
    }
}