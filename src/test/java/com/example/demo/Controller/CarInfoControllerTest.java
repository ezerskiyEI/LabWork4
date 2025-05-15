package com.example.demo.Controller;

import com.example.demo.model.CarInfo;
import com.example.demo.service.CarInfoAnalyzerService;
import com.example.demo.service.CarInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarInfoController.class)
public class CarInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarInfoService carInfoService;

    @MockBean
    private CarInfoAnalyzerService analyzerService;

    private CarInfo carInfo;

    @BeforeEach
    void setUp() {
        carInfo = new CarInfo();
        carInfo.setVin("1HGCM82633A004352");
        carInfo.setMake("Honda");
        carInfo.setModel("Accord");
        carInfo.setYear(2003);
    }

    @Test
    void testAnalyzeText_Success() throws Exception {
        when(analyzerService.analyzeText("Honda Accord 2003 1HGCM82633A004352"))
                .thenReturn(Optional.of(carInfo));

        mockMvc.perform(get("/api/cars/analyze")
                        .param("text", "Honda Accord 2003 1HGCM82633A004352"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vin").value("1HGCM82633A004352"));
    }

    @Test
    void testAnalyzeText_Failure() throws Exception {
        when(analyzerService.analyzeText("Invalid text")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/cars/analyze")
                        .param("text", "Invalid text"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetCarsByYearAndMake() throws Exception {
        when(carInfoService.findByYearAndMake(2003, "Honda"))
                .thenReturn(Collections.singletonList(carInfo));

        mockMvc.perform(get("/api/cars/by-year-and-make")
                        .param("year", "2003")
                        .param("make", "Honda"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].vin").value("1HGCM82633A004352"));
    }

    @Test
    void testAddCar() throws Exception {
        when(carInfoService.addCar(any(CarInfo.class))).thenReturn(carInfo);

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"vin\":\"1HGCM82633A004352\",\"make\":\"Honda\",\"model\":\"Accord\",\"year\":2003}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vin").value("1HGCM82633A004352"));
    }

    @Test
    void testBulkOperations() throws Exception {
        when(carInfoService.addCar(any(CarInfo.class))).thenReturn(carInfo);
        when(carInfoService.updateCar(eq("1HGCM82633A004352"), any(CarInfo.class)))
                .thenReturn(Optional.of(carInfo));
        doNothing().when(carInfoService).deleteCar("1HGCM82633A004352");

        String requestBody = "[{\"action\":\"add\",\"carInfo\":{\"vin\":\"1HGCM82633A004352\",\"make\":\"Honda\",\"model\":\"Accord\",\"year\":2003}}," +
                "{\"action\":\"update\",\"carInfo\":{\"vin\":\"1HGCM82633A004352\",\"make\":\"Honda\",\"model\":\"Accord\",\"year\":2004}}," +
                "{\"action\":\"delete\",\"carInfo\":{\"vin\":\"1HGCM82633A004352\",\"make\":\"Honda\",\"model\":\"Accord\",\"year\":2003}}]";

        mockMvc.perform(post("/api/cars/bulk-operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void testGetCarByVin_Success() throws Exception {
        when(carInfoService.getCarByVin("1HGCM82633A004352")).thenReturn(Optional.of(carInfo));

        mockMvc.perform(get("/api/cars/1HGCM82633A004352"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vin").value("1HGCM82633A004352"));
    }

    @Test
    void testGetCarByVin_NotFound() throws Exception {
        when(carInfoService.getCarByVin("1HGCM82633A004352")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/cars/1HGCM82633A004352"))
                .andExpect(status().isNotFound());
    }
}