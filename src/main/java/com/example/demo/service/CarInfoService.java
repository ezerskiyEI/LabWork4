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

<<<<<<< HEAD
    public void deleteCarByVin(String vin) {
        carInfoRepository.deleteById(vin);
        carCache.remove(vin);
    }

    public List<CarInfo> getCarsByOwnerName(String ownerName) {
        return carInfoRepository.findCarsByOwnerName(ownerName);
=======
    public Optional<CarInfo> updateCar(String vin, CarInfo carInfoDetails) {
        return carInfoRepository.findByVin(vin).map(existingCar -> {
            existingCar.setMake(carInfoDetails.getMake());
            existingCar.setModel(carInfoDetails.getModel());
            existingCar.setYear(carInfoDetails.getYear());
            return carInfoRepository.save(existingCar);
        });
>>>>>>> 03f5d34f8d291d57e2ae16c0d816222fffb062d1
    }

    public boolean deleteCarByVin(String vin) {
        if (carInfoRepository.existsById(vin)) {
            carInfoRepository.deleteById(vin);
            return true;
        }
        return false;
    }
}
