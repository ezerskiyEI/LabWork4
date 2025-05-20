package com.example.demo.service;

import com.example.demo.Cache.AppCache;
import com.example.demo.model.CarInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CarInfoAnalyzerService {
    private static final Pattern VIN_PATTERN = Pattern.compile("[A-HJ-NPR-Z0-9]{17}");
    private static final Pattern YEAR_PATTERN = Pattern.compile("\\b(19|20)\\d{2}\\b");
    private static final Pattern MAKE_MODEL_PATTERN = Pattern.compile("(Toyota|Honda|BMW|Audi|Mercedes|Ford|Volkswagen|Chevrolet)\\s+(\\w+)");

    private final AppCache cache;
    private final RequestCounterService counterService;

    @Autowired
    public CarInfoAnalyzerService(AppCache cache, RequestCounterService counterService) {
        this.cache = cache;
        this.counterService = counterService;
    }

    public Optional<CarInfo> analyzeText(String text) {
        counterService.increment();
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }

        String normalizedText = text.trim().toUpperCase();
        Optional<CarInfo> cachedResult = cache.getAnalyzedText(normalizedText);
        if (cachedResult.isPresent()) {
            return cachedResult;
        }

        String vin = findVin(normalizedText);
        if (vin == null) {
            cache.putAnalyzedText(normalizedText, Optional.empty());
            return Optional.empty();
        }

        int year = findYear(normalizedText);
        String[] makeModel = findMakeAndModel(normalizedText);

        CarInfo carInfo = new CarInfo();
        carInfo.setVin(vin);
        carInfo.setMake(makeModel[0]);
        carInfo.setModel(makeModel[1]);
        carInfo.setYear(year);

        Optional<CarInfo> result = Optional.of(carInfo);
        cache.putAnalyzedText(normalizedText, result);

        return result;
    }

    private String findVin(String text) {
        Matcher matcher = VIN_PATTERN.matcher(text);
        return matcher.find() ? matcher.group() : null;
    }

    private int findYear(String text) {
        Matcher matcher = YEAR_PATTERN.matcher(text);
        return matcher.find() ? Integer.parseInt(matcher.group()) : 2000;
    }

    private String[] findMakeAndModel(String text) {
        Matcher matcher = MAKE_MODEL_PATTERN.matcher(text);
        if (matcher.find()) {
            return new String[]{matcher.group(1), matcher.group(2)};
        }
        return new String[]{"Unknown", "Unknown"};
    }
}