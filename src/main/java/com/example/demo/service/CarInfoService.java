package com.example.demo.service;

import com.example.demo.Cache.AppCache;
import com.example.demo.model.CarInfo;
import com.example.demo.repository.CarInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarInfoService {

    private final CarInfoRepository repository;
    private final AppCache cache;
    private final RequestCounterService counterService;

    public CarInfoService(CarInfoRepository repository, AppCache cache, RequestCounterService counterService) {
        this.repository = repository;
        this.cache = cache;
        this.counterService = counterService;
    }

    public List<CarInfo> findByYearAndMake(int year, String make) {
        counterService.increment();
        String cacheKey = "cars_by_year_" + year + "_make_" + make;
        Optional<List<CarInfo>> cachedCars = cache.getCarList(cacheKey);
        if (cachedCars.isPresent()) {
            return cachedCars.get();
        }
        List<CarInfo> cars = repository.findByYearAndMake(year, make);
        cache.putCarList(cacheKey, cars);
        return cars;
    }

    public List<CarInfo> findByOwnerId(Long ownerId) {
        counterService.increment();
        String cacheKey = "cars_by_owner_" + ownerId;
        Optional<List<CarInfo>> cachedCars = cache.getCarList(cacheKey);
        if (cachedCars.isPresent()) {
            return cachedCars.get();
        }
        List<CarInfo> cars = repository.findByOwnerId(ownerId);
        cache.putCarList(cacheKey, cars);
        return cars;
    }

    public Optional<CarInfo> getCarByVin(String vin) {
        counterService.increment();
        Optional<CarInfo> cachedCar = cache.getCar(vin);
        if (cachedCar.isPresent()) {
            return cachedCar;
        }
        Optional<CarInfo> car = repository.findById(vin);
        car.ifPresent(cache::putCar);
        return car;
    }

    public List<CarInfo> getCarsByVinsBulk(List<String> vins) {
        counterService.increment();
        return vins.stream()
                .map(this::getCarByVin)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public CarInfo addCar(CarInfo car) {
        counterService.increment();
        CarInfo savedCar = repository.save(car);
        cache.putCar(savedCar);
        cache.evictAllCarLists();
        cache.evictAllOwnerLists();
        return savedCar;
    }

    public List<CarInfo> addCarsBulk(List<CarInfo> cars) {
        counterService.increment();
        return cars.stream()
                .map(this::addCar)
                .collect(Collectors.toList());
    }

    public Optional<CarInfo> updateCar(String vin, CarInfo car) {
        counterService.increment();
        if (!repository.existsById(vin)) {
            return Optional.empty();
        }
        car.setVin(vin);
        CarInfo updatedCar = repository.save(car);
        cache.putCar(updatedCar);
        cache.evictAllCarLists();
        cache.evictAllOwnerLists();
        return Optional.of(updatedCar);
    }

    public List<CarInfo> updateCarsBulk(List<CarInfo> cars) {
        counterService.increment();
        return cars.stream()
                .filter(car -> car.getVin() != null)
                .map(car -> updateCar(car.getVin(), car))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public boolean deleteCar(String vin) {
        counterService.increment();
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
        counterService.increment();
        Optional<List<CarInfo>> cachedCars = cache.getCarList("all_cars");
        if (cachedCars.isPresent()) {
            return cachedCars.get();
        }
        List<CarInfo> cars = repository.findAll();
        cache.putCarList("all_cars", cars);
        return cars;
    }


    public List<CarInfo> getAll() {
        return getAllCars();
    }

    public Optional<CarInfo> getByVin(String vin) {
        return getCarByVin(vin);
    }

    public CarInfo create(CarInfo carInfo) {
        return addCar(carInfo);
    }

    public CarInfo update(CarInfo carInfo) {
        return updateCar(carInfo.getVin(), carInfo).orElseThrow(() -> new RuntimeException("Car not found"));
    }

    public void delete(String vin) {
        if (!deleteCar(vin)) {
            throw new RuntimeException("Car not found");
        }
    }
}