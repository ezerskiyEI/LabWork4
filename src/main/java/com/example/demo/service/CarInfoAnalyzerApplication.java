package com.example.demo.service;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.carinfoanalyzer")
public class CarInfoAnalyzerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarInfoAnalyzerApplication.class, args);
    }
}
