package com.example.demo.service;

import com.example.demo.Cache.AppCache;
import com.example.demo.model.CarInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarInfoAnalyzerServiceTest {

    @Mock
    private AppCache cache;

    @InjectMocks
    private CarInfoAnalyzerService analyzerService;

    @Test
    void analyzeText_nullInput() {
        Optional<CarInfo> result = analyzerService.analyzeText(null);

        assertFalse(result.isPresent());
    }

    @Test
    void analyzeText_emptyInput() {
        Optional<CarInfo> result = analyzerService.analyzeText("");

        assertFalse(result.isPresent());
    }

    @Test
    void analyzeText_fromCache() {
        CarInfo car = new CarInfo();
        car.setVin("1HGCM82633A004352");
        when(cache.getAnalyzedText("HONDA ACCORD 2003 1HGCM82633A004352")).thenReturn(Optional.of(car));

        Optional<CarInfo> result = analyzerService.analyzeText("Honda Accord 2003 1HGCM82633A004352");

        assertTrue(result.isPresent());
        assertEquals(car, result.get());
        verify(cache, never()).putAnalyzedText(anyString(), any());
    }

    @Test
    void analyzeText_validInput() {
        String text = "Honda Accord 2003 1HGCM82633A004352";
        when(cache.getAnalyzedText(text.toUpperCase())).thenReturn(Optional.empty());

        Optional<CarInfo> result = analyzerService.analyzeText(text);

        assertTrue(result.isPresent());
        CarInfo car = result.get();
        assertEquals("1HGCM82633A004352", car.getVin());
        assertEquals("Honda", car.getMake());
        assertEquals("Accord", car.getModel());
        assertEquals(2003, car.getYear());
        verify(cache).putAnalyzedText(text.toUpperCase(), result);
    }

    @Test
    void analyzeText_invalidVin() {
        String text = "Honda Accord 2003 INVALID_VIN";
        when(cache.getAnalyzedText(text.toUpperCase())).thenReturn(Optional.empty());

        Optional<CarInfo> result = analyzerService.analyzeText(text);

        assertFalse(result.isPresent());
        verify(cache).putAnalyzedText(text.toUpperCase(), Optional.empty());
    }

    @Test
    void analyzeText_noMakeModel() {
        String text = "Car 2003 1HGCM82633A004352";
        when(cache.getAnalyzedText(text.toUpperCase())).thenReturn(Optional.empty());

        Optional<CarInfo> result = analyzerService.analyzeText(text);

        assertTrue(result.isPresent());
        CarInfo car = result.get();
        assertEquals("Unknown", car.getMake());
        assertEquals("Unknown", car.getModel());
        verify(cache).putAnalyzedText(text.toUpperCase(), result);
    }
}