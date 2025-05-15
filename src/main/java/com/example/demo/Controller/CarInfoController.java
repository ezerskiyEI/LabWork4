package com.example.demo.Controller;

import com.example.demo.dto.BulkOperationRequest;
import com.example.demo.model.CarInfo;
import com.example.demo.service.CarInfoAnalyzerService;
import com.example.demo.service.CarInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@Tag(name = "Car Information", description = "API for managing car information")
public class CarInfoController {
    private static final Logger logger = LoggerFactory.getLogger(CarInfoController.class);

    private final CarInfoService carInfoService;
    private final CarInfoAnalyzerService analyzerService;

    @Autowired
    public CarInfoController(CarInfoService carInfoService,
                             CarInfoAnalyzerService analyzerService) {
        this.carInfoService = carInfoService;
        this.analyzerService = analyzerService;
    }

    @Operation(summary = "Analyze text to extract car information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully analyzed"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/analyze")
    public ResponseEntity<CarInfo> analyzeText(
            @Parameter(description = "Text containing car information")
            @RequestParam String text) {
        logger.info("Analyzing text for car information: {}", text);
        return analyzerService.analyzeText(text)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Get cars by year and make")
    @GetMapping("/by-year-and-make")
    public ResponseEntity<List<CarInfo>> getCarsByYearAndMake(
            @Parameter(description = "Manufacture year") @RequestParam int year,
            @Parameter(description = "Car make") @RequestParam String make) {
        logger.info("Getting cars by year: {} and make: {}", year, make);
        return ResponseEntity.ok(carInfoService.findByYearAndMake(year, make));
    }

    @Operation(summary = "Add a new car")
    @PostMapping
    public ResponseEntity<CarInfo> addCar(
            @Parameter(description = "Car information to add") @Valid @RequestBody CarInfo carInfo) {
        logger.info("Adding new car with VIN: {}", carInfo.getVin());
        return ResponseEntity.ok(carInfoService.addCar(carInfo));
    }

    @Operation(summary = "Add multiple cars")
    @PostMapping("/bulk")
    public ResponseEntity<List<CarInfo>> addCarsBulk(
            @Parameter(description = "List of cars to add") @Valid @RequestBody List<CarInfo> cars) {
        logger.info("Adding {} cars in bulk", cars.size());
        List<CarInfo> result = carInfoService.addCarsBulk(cars);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get cars by VINs in bulk")
    @PostMapping("/bulk-by-vins")
    public ResponseEntity<List<CarInfo>> getCarsByVinsBulk(
            @Parameter(description = "List of VINs to retrieve") @Valid @RequestBody BulkOperationRequest request) {
        logger.info("Retrieving {} cars by VINs in bulk", request.getIdentifiers().size());
        List<CarInfo> result = carInfoService.getCarsByVinsBulk(request.getIdentifiers());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get car by VIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the car"),
            @ApiResponse(responseCode = "404", description = "Car not found")
    })
    @GetMapping("/{vin}")
    public ResponseEntity<CarInfo> getCar(
            @Parameter(description = "VIN of the car") @PathVariable String vin) {
        logger.info("Getting car by VIN: {}", vin);
        return carInfoService.getCarByVin(vin)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update car information")
    @PutMapping("/{vin}")
    public ResponseEntity<CarInfo> updateCar(
            @Parameter(description = "VIN of the car to update") @PathVariable String vin,
            @Parameter(description = "Updated car information") @Valid @RequestBody CarInfo carInfo) {
        logger.info("Updating car with VIN: {}", vin);
        return carInfoService.updateCar(vin, carInfo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update multiple cars")
    @PutMapping("/bulk")
    public ResponseEntity<List<CarInfo>> updateCarsBulk(
            @Parameter(description = "List of cars to update") @Valid @RequestBody List<CarInfo> cars) {
        logger.info("Updating {} cars in bulk", cars.size());
        List<CarInfo> result = carInfoService.updateCarsBulk(cars);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Delete a car")
    @DeleteMapping("/{vin}")
    public ResponseEntity<Void> deleteCar(
            @Parameter(description = "VIN of the car to delete") @PathVariable String vin) {
        logger.info("Deleting car with VIN: {}", vin);
        return carInfoService.deleteCar(vin)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get cars by owner ID")
    @GetMapping("/by-owner/{ownerId}")
    public ResponseEntity<List<CarInfo>> getCarsByOwner(
            @Parameter(description = "Owner ID") @PathVariable Long ownerId) {
        logger.info("Getting cars by owner ID: {}", ownerId);
        return ResponseEntity.ok(carInfoService.findByOwnerId(ownerId));
    }

    @Operation(summary = "Get all cars")
    @GetMapping
    public ResponseEntity<List<CarInfo>> getAllCars() {
        logger.info("Getting all cars");
        return ResponseEntity.ok(carInfoService.getAllCars());
    }
}