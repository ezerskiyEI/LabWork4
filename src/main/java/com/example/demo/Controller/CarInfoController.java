package com.example.demo.Controller;

import com.example.demo.model.CarInfo;
import com.example.demo.service.CarInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarInfoController {

    @Autowired
    private CarInfoService service;

    @GetMapping
    public List<CarInfo> getAll() {
        return service.getAllCars();
    }

    @GetMapping("/{vin}")
    public CarInfo getByVin(@PathVariable String vin) {
        return service.getCarByVin(vin);
    }

    @GetMapping("/by-owner")
    public List<CarInfo> getByOwner(@RequestParam String name) {
        return service.getCarsByOwnerName(name);
    }

    @PostMapping
    public CarInfo add(@RequestBody CarInfo car) {
        return service.addCar(car);
    }

    @PutMapping("/{vin}")
    public CarInfo update(@PathVariable String vin, @RequestBody CarInfo car) {
        return service.updateCar(vin, car);
    }

    @DeleteMapping("/{vin}")
    public void delete(@PathVariable String vin) {
        service.deleteCar(vin);
    }
}