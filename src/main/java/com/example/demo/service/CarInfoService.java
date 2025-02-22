package com.example.demo.service;

import com.example.demo.model.CarInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarInfoService {
    public List<CarInfo> extractCarInfo() {
        // Возвращаем пустой список
        return List.of();
    }
}