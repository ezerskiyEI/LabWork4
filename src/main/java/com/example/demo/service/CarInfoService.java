package com.example.demo.service;


import com.example.demo.Cache.EntityCache;
import com.example.demo.model.CarInfo;
import com.example.demo.repository.CarInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class CarInfoService {

    @Autowired
    private CarInfoRepository carInfoRepository;

    @Autowired
    private EntityCache entityCache;

    private static final Pattern VIN_PATTERN = Pattern.compile("[A-HJ-NPR-Z0-9]{17}");

    public List<CarInfo> getAllCars() {
        return carInfoRepository.findAll();
    }

    public Optional<CarInfo> getCarByVin(String vin) {
        String key = "car:" + vin;
        if (entityCache.contains(key)) {
            return Optional.ofNullable(entityCache.get(key, CarInfo.class));
        }

        Optional<CarInfo> car = carInfoRepository.findByVin(vin);
        car.ifPresent(c -> entityCache.put(key, c));
        return car;
    }

    public CarInfo addCar(CarInfo carInfo) {
        if (!VIN_PATTERN.matcher(carInfo.getVin()).matches()) {
            throw new IllegalArgumentException("Неверный формат VIN");
        }

        CarInfo saved = carInfoRepository.save(carInfo);
        entityCache.put("car:" + saved.getVin(), saved);
        return saved;
    }

    public void deleteCarByVin(String vin) {
        carInfoRepository.deleteById(vin);
        entityCache.evict("car:" + vin);
    }

    public CarInfo updateCar(String vin, CarInfo updatedCar) {
        if (!carInfoRepository.existsById(vin)) {
            throw new IllegalArgumentException("Машина не найдена");
        }

        updatedCar.setVin(vin);
        CarInfo saved = carInfoRepository.save(updatedCar);
        entityCache.put("car:" + vin, saved);
        return saved;
    }
}