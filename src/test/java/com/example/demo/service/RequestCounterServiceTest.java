package com.example.demo.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestCounterServiceTest {

    private RequestCounterService counterService;

    @BeforeEach
    void setUp() {
        counterService = new RequestCounterService();
    }

    @Test
    @DisplayName("Should increment counter correctly in single thread")
    void shouldIncrementCounterCorrectlyInSingleThread() {
        counterService.increment();
        counterService.increment();

        assertEquals(2, counterService.getRequestCount());
    }

    @Test
    @DisplayName("Should increment counter correctly in multiple threads")
    void shouldIncrementCounterCorrectlyInMultipleThreads() throws InterruptedException {
        int threadCount = 100;
        int incrementsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counterService.increment();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        assertEquals(threadCount * incrementsPerThread, counterService.getRequestCount());
    }

    @Test
    @DisplayName("Should reset counter to zero")
    void shouldResetCounterToZero() {
        counterService.increment();
        counterService.reset();

        assertEquals(0, counterService.getRequestCount());
    }
}