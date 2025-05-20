package com.example.demo.service;

import com.example.demo.Cache.AppCache;
import com.example.demo.model.CarInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @Mock
    private RequestCounterService counterService;

    @InjectMocks
    private CarInfoAnalyzerService analyzerService;

    @BeforeEach
    void setUp() {
        // InjectMocks автоматически создаёт экземпляр analyzerService с моком cache и counterService
    }

    @Test
    @DisplayName("Should return car info when text is valid and not cached")
    void shouldReturnCarInfoWhenTextIsValidAndNotCached() {
        String validText = "Honda Accord 2003 1HGCM82633A004352";
        String normalizedText = validText.trim().toUpperCase();
        when(cache.getAnalyzedText(normalizedText)).thenReturn(Optional.empty());

        Optional<CarInfo> result = analyzerService.analyzeText(validText);

        assertTrue(result.isPresent());
        CarInfo car = result.get();
        assertEquals("Honda", car.getMake());
        assertEquals("Accord", car.getModel());
        assertEquals(2003, car.getYear());
        assertEquals("1HGCM82633A004352", car.getVin());
        verify(cache).getAnalyzedText(normalizedText);
        verify(cache).putAnalyzedText(eq(normalizedText), any(Optional.class));
        verify(counterService).increment();
    }

    @Test
    @DisplayName("Should return cached car info when text is valid and cached")
    void shouldReturnCachedCarInfoWhenTextIsValidAndCached() {
        String validText = "Honda Accord 2003 1HGCM82633A004352";
        String normalizedText = validText.trim().toUpperCase();
        CarInfo cachedCar = new CarInfo();
        cachedCar.setVin("1HGCM82633A004352");
        cachedCar.setMake("Honda");
        cachedCar.setModel("Accord");
        cachedCar.setYear(2003);
        when(cache.getAnalyzedText(normalizedText)).thenReturn(Optional.of(cachedCar));

        Optional<CarInfo> result = analyzerService.analyzeText(validText);

        assertTrue(result.isPresent());
        assertEquals(cachedCar, result.get());
        verify(cache).getAnalyzedText(normalizedText);
        verify(cache, never()).putAnalyzedText(anyString(), any());
        verify(counterService).increment();
    }

    @Test
    @DisplayName("Should return empty when text is null")
    void shouldReturnEmptyWhenTextIsNull() {
        Optional<CarInfo> result = analyzerService.analyzeText(null);

        assertFalse(result.isPresent());
        verify(cache, never()).getAnalyzedText(anyString());
        verify(cache, never()).putAnalyzedText(anyString(), any());
        verify(counterService).increment();
    }

    @Test
    @DisplayName("Should return empty when text is blank")
    void shouldReturnEmptyWhenTextIsBlank() {
        Optional<CarInfo> result = analyzerService.analyzeText(" ");

        assertFalse(result.isPresent());
        verify(cache, never()).getAnalyzedText(anyString());
        verify(cache, never()).putAnalyzedText(anyString(), any());
        verify(counterService).increment();
    }

    @Test
    @DisplayName("Should return empty when VIN is invalid")
    void shouldReturnEmptyWhenVinIsInvalid() {
        String invalidText = "Honda Accord 2003 INVALID_VIN";
        String normalizedText = invalidText.trim().toUpperCase();
        when(cache.getAnalyzedText(normalizedText)).thenReturn(Optional.empty());

        Optional<CarInfo> result = analyzerService.analyzeText(invalidText);

        assertFalse(result.isPresent());
        verify(cache).getAnalyzedText(normalizedText);
        verify(cache).putAnalyzedText(normalizedText, Optional.empty());
        verify(counterService).increment();
    }

    @Test
    @DisplayName("Should return default year and unknown make/model when not found")
    void shouldReturnDefaultYearAndUnknownMakeModelWhenNotFound() {
        String text = "Unknown Brand 1HGCM82633A004352";
        String normalizedText = text.trim().toUpperCase();
        when(cache.getAnalyzedText(normalizedText)).thenReturn(Optional.empty());

        Optional<CarInfo> result = analyzerService.analyzeText(text);

        assertTrue(result.isPresent());
        CarInfo car = result.get();
        assertEquals("1HGCM82633A004352", car.getVin());
        assertEquals("Unknown", car.getMake());
        assertEquals("Unknown", car.getModel());
        assertEquals(2000, car.getYear());
        verify(cache).getAnalyzedText(normalizedText);
        verify(cache).putAnalyzedText(eq(normalizedText), any(Optional.class));
        verify(counterService).increment();
    }
}