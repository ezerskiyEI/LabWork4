package com.example.demo.Controller;

import com.example.demo.model.CarInfo;
import com.example.demo.service.CarInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<CarInfo> getCarByVin(@PathVariable String vin) {
        return carInfoService.getCarByVin(vin)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public CarInfo addCar(@RequestBody CarInfo carInfo) {
        return carInfoService.addCar(carInfo);
    }

    @PutMapping("/{vin}")
    public ResponseEntity<CarInfo> updateCar(@PathVariable String vin, @RequestBody CarInfo carInfoDetails) {
        return carInfoService.updateCar(vin, carInfoDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{vin}")
    public ResponseEntity<Void> deleteCar(@PathVariable String vin) {
        if (carInfoService.deleteCarByVin(vin)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
