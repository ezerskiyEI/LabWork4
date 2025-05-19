package com.example.demo.Controller;

import com.example.demo.service.RequestCounterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/counter")
public class RequestCounterController {

    private final RequestCounterService counterService;

    public RequestCounterController(RequestCounterService counterService) {
        this.counterService = counterService;
    }

    @GetMapping
    public long getRequestCount() {
        return counterService.getRequestCount();
    }
}