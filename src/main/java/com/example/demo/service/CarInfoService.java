package com.example.demo.service;


import com.example.demo.Cache.AppCache;
import com.example.demo.model.CarInfo;
import com.example.demo.repository.CarInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarInfoService {

    @Autowired
    private CarInfoRepository carInfoRepository;

    @Autowired
    private AppCache appCache;

    public List<CarInfo> getAllCars() {
        return carInfoRepository.findAll();
    }

    public Optional<CarInfo> getCarByVin(String vin) {
        CarInfo cached = appCache.getCar(vin);
        if (cached != null) {
            return Optional.of(cached);
        }

        Optional<CarInfo> car = carInfoRepository.findByVin(vin);
        car.ifPresent(c -> appCache.putCar(vin, c));
        return car;
    }

    public CarInfo addCar(CarInfo carInfo) {
        CarInfo saved = carInfoRepository.save(carInfo);
        appCache.putCar(saved.getVin(), saved);
        return saved;
    }

    public CarInfo updateCar(String vin, CarInfo carInfo) {
        carInfo.setVin(vin);
        CarInfo updated = carInfoRepository.save(carInfo);
        appCache.putCar(vin, updated);
        return updated;
    }

    public void deleteCar(String vin) {
        carInfoRepository.deleteByVin(vin);
        appCache.evictCar(vin);
    }

    public List<CarInfo> getCarsByOwnerName(String ownerName) {
        return carInfoRepository.findCarsByOwnerName(ownerName);
    }
}