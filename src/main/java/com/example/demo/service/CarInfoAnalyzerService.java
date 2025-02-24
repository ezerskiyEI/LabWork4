package com.example.demo.service;

import com.example.demo.model.CarInfo;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CarInfoAnalyzerService {
    private static final Pattern VIN_PATTERN = Pattern.compile("[A-HJ-NPR-Z0-9]{17}");
    private static final Pattern MAKE_MODEL_PATTERN = Pattern.compile("(mercedes-benz|bmw|honda|ford) ([\\w-]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern YEAR_PATTERN = Pattern.compile("(19[0-9]{2}|20[0-2][0-9])");

    public Optional<CarInfo> analyzeText(String text) {
        Matcher vinMatcher = VIN_PATTERN.matcher(text);
        Matcher makeModelMatcher = MAKE_MODEL_PATTERN.matcher(text);
        Matcher yearMatcher = YEAR_PATTERN.matcher(text);

        if (vinMatcher.find() && makeModelMatcher.find() && yearMatcher.find()) {
            String vin = vinMatcher.group();
            String make = makeModelMatcher.group(1);
            String model = makeModelMatcher.group(2);
            int year = Integer.parseInt(yearMatcher.group());

            return Optional.of(new CarInfo(vin, make, model, year));
        }

        return Optional.empty();
    }
}
