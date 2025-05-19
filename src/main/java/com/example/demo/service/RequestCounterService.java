package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class RequestCounterService {

    private final AtomicLong requestCount = new AtomicLong(0);

    public void increment() {
        requestCount.incrementAndGet();
    }

    public long getRequestCount() {
        return requestCount.get();
    }

    public void reset() {
        requestCount.set(0);
    }
}