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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarInfoAnalyzerServiceTest {

    @Mock
    private AppCache cache;

    @InjectMocks
    private CarInfoAnalyzerService analyzerService;

    @Mock
    private CarInfo car;

    @BeforeEach
    void setUp() {
        when(car.getVin()).thenReturn("1HGCM82633A004352");
        when(car.getMake()).thenReturn("Honda");
        when(car.getModel()).thenReturn("Accord");
        when(car.getYear()).thenReturn(2003);
    }

    @Test
    @DisplayName("Should return empty when text is null")
    void shouldReturnEmptyWhenTextIsNull() {
        Optional<CarInfo> result = analyzerService.analyzeText(null);

        assertFalse(result.isPresent());
        verify(cache, never()).getAnalyzedText(anyString());
    }

    @Test
    @DisplayName("Should return empty when text is empty")
    void shouldReturnEmptyWhenTextIsEmpty() {
        Optional<CarInfo> result = analyzerService.analyzeText("");

        assertFalse(result.isPresent());
        verify(cache, never()).getAnalyzedText(anyString());
    }

    @Test
    @DisplayName("Should return car from cache when text is cached")
    void shouldReturnCarFromCacheWhenTextIsCached() {
        String text = "Honda Accord 2003 1HGCM82633A004352";
        when(cache.getAnalyzedText(text.toUpperCase())).thenReturn(Optional.of(car));

        Optional<CarInfo> result = analyzerService.analyzeText(text);

        assertTrue(result.isPresent());
        assertEquals(car, result.get());
        verify(cache, never()).putAnalyzedText(anyString(), any());
    }

    @Test
    @DisplayName("Should analyze valid text and cache result")
    void shouldAnalyzeValidTextAndCacheResult() {
        String text = "Honda Accord 2003 1HGCM82633A004352";
        when(cache.getAnalyzedText(text.toUpperCase())).thenReturn(Optional.empty());

        Optional<CarInfo> result = analyzerService.analyzeText(text);

        assertTrue(result.isPresent());
        verify(cache).putAnalyzedText(eq(text.toUpperCase()), any(Optional.class));
    }

    @Test
    @DisplayName("Should return empty for invalid VIN and cache result")
    void shouldReturnEmptyForInvalidVinAndCacheResult() {
        String text = "Honda Accord 2003 INVALID_VIN";
        when(cache.getAnalyzedText(text.toUpperCase())).thenReturn(Optional.empty());

        Optional<CarInfo> result = analyzerService.analyzeText(text);

        assertFalse(result.isPresent());
        verify(cache).putAnalyzedText(text.toUpperCase(), Optional.empty());
    }

    @Test
    @DisplayName("Should analyze text with no make or model and cache result")
    void shouldAnalyzeTextWithNoMakeOrModelAndCacheResult() {
        String text = "Car 2003 1HGCM82633A004352";
        when(cache.getAnalyzedText(text.toUpperCase())).thenReturn(Optional.empty());

        Optional<CarInfo> result = analyzerService.analyzeText(text);

        assertTrue(result.isPresent());
        verify(cache).putAnalyzedText(eq(text.toUpperCase()), any(Optional.class));
    }

    @Test
    @DisplayName("Should return empty for invalid year and cache result")
    void shouldReturnEmptyForInvalidYearAndCacheResult() {
        String text = "Honda Accord invalid 1HGCM82633A004352";
        when(cache.getAnalyzedText(text.toUpperCase())).thenReturn(Optional.empty());

        Optional<CarInfo> result = analyzerService.analyzeText(text);

        assertFalse(result.isPresent());
        verify(cache).putAnalyzedText(text.toUpperCase(), Optional.empty());
    }
}