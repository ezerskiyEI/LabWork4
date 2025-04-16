package com.example.demo.Controller;

import com.example.demo.model.CarInfo;
import com.example.demo.service.CarInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cars")
public class CarInfoController {

    @Autowired
    private CarInfoService carInfoService;

    @GetMapping
    public List<CarInfo> getAllCars() {
        return carInfoService.getAllCars();
    }

    @GetMapping("/{vin}")
    public Optional<CarInfo> getCarByVin(@PathVariable String vin) {
        return carInfoService.getCarByVin(vin);
    }

    @PostMapping
    public CarInfo addCar(@RequestBody CarInfo carInfo) {
        return carInfoService.addCar(carInfo);
    }

    @PutMapping("/{vin}")
    public CarInfo updateCar(@PathVariable String vin, @RequestBody CarInfo carInfo) {
        return carInfoService.updateCar(vin, carInfo);
    }

    @DeleteMapping("/{vin}")
    public void deleteCar(@PathVariable String vin) {
        carInfoService.deleteCar(vin);
    }

    @GetMapping("/by-owner")
    public List<CarInfo> getCarsByOwnerName(@RequestParam String ownerName) {
        return carInfoService.getCarsByOwnerName(ownerName);
    }
}