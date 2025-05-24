package com.example.demo.Controller;

import com.example.demo.model.CarInfo;
import com.example.demo.service.CarInfoAnalyzerService;
import com.example.demo.service.CarInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cars")
public class CarInfoController {

    private final CarInfoService carInfoService;
    private final CarInfoAnalyzerService analyzerService;

    public CarInfoController(CarInfoService carInfoService, CarInfoAnalyzerService analyzerService) {
        this.carInfoService = carInfoService;
        this.analyzerService = analyzerService;
    }

    @GetMapping
    public ResponseEntity<List<CarInfo>> getAll() {
        return ResponseEntity.ok(carInfoService.getAll());
    }

    @GetMapping("/{vin}")
    public ResponseEntity<Optional<CarInfo>> getByVin(@PathVariable String vin) {
        return ResponseEntity.ok(carInfoService.getByVin(vin));
    }

    @GetMapping("/by-owner/{ownerId}")
    public ResponseEntity<List<CarInfo>> getByOwnerId(@PathVariable Long ownerId) {
        return ResponseEntity.ok(carInfoService.findByOwnerId(ownerId));
    }

    @GetMapping("/analyze")
    public ResponseEntity<Optional<CarInfo>> analyzeText(@RequestParam String text) {
        Optional<CarInfo> carInfo = analyzerService.analyzeText(text);
        if (carInfo != null) {
            return ResponseEntity.ok(carInfo);
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping
    public ResponseEntity<CarInfo> create(@RequestBody CarInfo carInfo) {
        return ResponseEntity.ok(carInfoService.create(carInfo));
    }

    @PutMapping("/{vin}")
    public ResponseEntity<CarInfo> update(@PathVariable String vin, @RequestBody CarInfo carInfo) {
        carInfo.setVin(vin);
        return ResponseEntity.ok(carInfoService.update(carInfo));
    }

    @DeleteMapping("/{vin}")
    public ResponseEntity<Void> delete(@PathVariable String vin) {
        carInfoService.delete(vin);
        return ResponseEntity.ok().build();
    }
}