package com.example.demo.service;


import com.example.demo.Cache.CarCache;
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
    private CarCache carCache;

    public List<CarInfo> getAllCars() {
        return carInfoRepository.findAll();
    }

    public Optional<CarInfo> getCarByVin(String vin) {
        if (carCache.contains(vin)) {
            return Optional.of(carCache.get(vin));
        }
        Optional<CarInfo> car = carInfoRepository.findByVin(vin);
        car.ifPresent(c -> carCache.put(vin, c));
        return car;
    }

    public CarInfo addCar(CarInfo carInfo) {
        CarInfo saved = carInfoRepository.save(carInfo);
        carCache.put(saved.getVin(), saved);
        return saved;
    }

    public void deleteCarByVin(String vin) {
        carInfoRepository.deleteById(vin);
        carCache.remove(vin);
    }

    public List<CarInfo> getCarsByOwnerName(String ownerName) {
        return carInfoRepository.findCarsByOwnerName(ownerName);
    }
}