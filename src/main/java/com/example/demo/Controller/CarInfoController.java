package com.example.demo.Controller;

import com.example.demo.model.CarInfo;
import com.example.demo.Cache.AppCache;
import com.example.demo.service.CarInfoAnalyzerService;
import com.example.demo.service.CarInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarInfoController {

    @Autowired
    private CarInfoService carInfoService;

    @Autowired
    private CarInfoAnalyzerService analyzerService;

    @GetMapping("/analyze")
    public CarInfo analyzeText(@RequestParam String text) {
        return analyzerService.analyzeText(text).orElse(null);
    }

    @GetMapping("/by-year-and-make")
    public List<CarInfo> getCarsByYearAndMake(
            @RequestParam int year,
            @RequestParam String make) {
        return carInfoService.findByYearAndMake(year, make);
    }

    @PostMapping
    public CarInfo addCar(@RequestBody CarInfo carInfo) {
        return carInfoService.addCar(carInfo);
    }

    @GetMapping("/{vin}")
    public ResponseEntity<CarInfo> getCar(@PathVariable String vin) {
        return carInfoService.getCarByVin(vin)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{vin}")
    public ResponseEntity<CarInfo> updateCar(
            @PathVariable String vin,
            @RequestBody CarInfo carInfo) {
        ResponseEntity ResponseEntity = null;
        return carInfoService.updateCar(vin, carInfo)
                .map(org.springframework.http.ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{vin}")
    public ResponseEntity<Void> deleteCar(@PathVariable String vin) {
        return carInfoService.deleteCar(vin)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/by-owner/{ownerId}")
    public List<CarInfo> getCarsByOwner(@PathVariable Long ownerId) {
        return carInfoService.findByOwnerId(ownerId);
    }

    @GetMapping
    public List<CarInfo> getAllCars() {
        return carInfoService.getAllCars();
    }
}