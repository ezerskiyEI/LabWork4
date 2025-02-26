package com.example.demo.Controller;


import com.example.demo.model.CarInfo;
import com.example.demo.service.CarInfoAnalyzerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class CarInfoController {

    @Autowired
    private CarInfoAnalyzerService analyzerService;

    // Принимает текст на анализ и возвращает найденные данные об авто
    @GetMapping("/analyze")
    public Object analyzeText(@RequestParam String text) {
        Optional<CarInfo> carInfo = analyzerService.analyzeText(text);
        // Возвращаем результат анализа или null, если ничего не найдено
        return carInfo.orElse(null);
    }
}