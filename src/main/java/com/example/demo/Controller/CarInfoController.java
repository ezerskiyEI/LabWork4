package com.example.demo.Controller;


import com.example.demo.model.CarInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarInfoController {

    @GetMapping("/analyze")
    public String analyzeText(@RequestParam String text) {
        // Возвращаем текст, который был передан в параметре запроса
        return text;
    }

    @GetMapping("/car")
    public CarInfo getCarInfo(
            @RequestParam String vin,
            @RequestParam String make,
            @RequestParam String model,
            @RequestParam int year
    ) {
        // Возвращаем данные об авто в формате JSON
        return new CarInfo(vin, make, model, year);
    }
}
