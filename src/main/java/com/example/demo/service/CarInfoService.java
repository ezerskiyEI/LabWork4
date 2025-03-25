package com.example.demo.service;


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

    public List<CarInfo> getAllCars() {
        return carInfoRepository.findAll();
    }

    public Optional<CarInfo> getCarByVin(String vin) {
        return carInfoRepository.findByVin(vin);
    }

    public CarInfo addCar(CarInfo carInfo) {
        return carInfoRepository.save(carInfo);
    }

    public void deleteCarByVin(String vin) {
        carInfoRepository.deleteById(vin);
    }
}