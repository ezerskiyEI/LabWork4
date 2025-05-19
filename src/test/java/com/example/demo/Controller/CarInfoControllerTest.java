package com.example.demo.Controller;

import com.example.demo.dto.BulkOperationRequest;
import com.example.demo.model.CarInfo;
import com.example.demo.service.CarInfoAnalyzerService;
import com.example.demo.service.CarInfoService;
import com.example.demo.service.RequestCounterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CarInfoControllerTest {

    @Mock
    private CarInfoService carInfoService;

    @Mock
    private CarInfoAnalyzerService analyzerService;

    @Mock
    private RequestCounterService counterService;

    @InjectMocks
    private CarInfoController carInfoController;

    private MockMvc mockMvc;

    @Mock
    private CarInfo car;

    @Mock
    private CarInfo car2;

    @Mock
    private BulkOperationRequest bulkRequest;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(carInfoController).build();
        objectMapper = new ObjectMapper();
        when(car.getVin()).thenReturn("1HGCM82633A004352");
        when(car2.getVin()).thenReturn("2HGCM82633A004353");
    }

    @Test
    @DisplayName("Should return all cars when getting all cars")
    void shouldReturnAllCarsWhenGettingAllCars() throws Exception {
        List<CarInfo> cars = Arrays.asList(car, car2);
        when(carInfoService.getAllCars()).thenReturn(cars);

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(carInfoService).getAllCars();
        verify(counterService).increment();
    }

    @Test
    @DisplayName("Should return car when getting by VIN")
    void shouldReturnCarWhenGettingByVin() throws Exception {
        when(carInfoService.getCarByVin("1HGCM82633A004352")).thenReturn(Optional.of(car));

        mockMvc.perform(get("/api/cars/1HGCM82633A004352"))
                .andExpect(status().isOk());

        verify(carInfoService).getCarByVin("1HGCM82633A004352");
        verify(counterService).increment();
    }

    @Test
    @DisplayName("Should return not found when getting non-existing car by VIN")
    void shouldReturnNotFoundWhenGettingNonExistingCarByVin() throws Exception {
        when(carInfoService.getCarByVin("INVALID_VIN")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/cars/INVALID_VIN"))
                .andExpect(status().isNotFound());

        verify(carInfoService).getCarByVin("INVALID_VIN");
        verify(counterService).increment();
    }

    @Test
    @DisplayName("Should return cars when getting by year and make")
    void shouldReturnCarsWhenGettingByYearAndMake() throws Exception {
        List<CarInfo> cars = Arrays.asList(car);
        when(carInfoService.findByYearAndMake(2003, "Honda")).thenReturn(cars);

        mockMvc.perform(get("/api/cars/by-year-and-make")
                        .param("year", "2003")
                        .param("make", "Honda"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(carInfoService).findByYearAndMake(2003, "Honda");
        verify(counterService).increment();
    }

    @Test
    @DisplayName("Should return cars when getting by owner ID")
    void shouldReturnCarsWhenGettingByOwnerId() throws Exception {
        List<CarInfo> cars = Arrays.asList(car);
        when(carInfoService.findByOwnerId(1L)).thenReturn(cars);

        mockMvc.perform(get("/api/cars/by-owner/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(carInfoService).findByOwnerId(1L);
        verify(counterService).increment();
    }

    @Test
    @DisplayName("Should return car when analyzing valid text")
    void shouldReturnCarWhenAnalyzingValidText() throws Exception {
        when(analyzerService.analyzeText("Honda Accord 2003 1HGCM82633A004352")).thenReturn(Optional.of(car));

        mockMvc.perform(get("/api/cars/analyze")
                        .param("text", "Honda Accord 2003 1HGCM82633A004352"))
                .andExpect(status().isOk());

        verify(analyzerService).analyzeText("Honda Accord 2003 1HGCM82633A004352");
        verify(counterService).increment();
    }

    @Test
    @DisplayName("Should return bad request when analyzing invalid text")
    void shouldReturnBadRequestWhenAnalyzingInvalidText() throws Exception {
        when(analyzerService.analyzeText("invalid")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/cars/analyze")
                        .param("text", "invalid"))
                .andExpect(status().isBadRequest());

        verify(analyzerService).analyzeText("invalid");
        verify(counterService).increment();
    }

    @Test
    @DisplayName("Should add car when posting valid car")
    void shouldAddCarWhenPostingValidCar() throws Exception {
        when(carInfoService.addCar(any(CarInfo.class))).thenReturn(car);

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isOk());

        verify(carInfoService).addCar(any(CarInfo.class));
        verify(counterService).increment();
    }

    @Test
    @DisplayName("Should add multiple cars when posting bulk")
    void shouldAddCarsWhenPostingBulk() throws Exception {
        List<CarInfo> cars = Arrays.asList(car, car2);
        when(carInfoService.addCarsBulk(anyList())).thenReturn(cars);

        mockMvc.perform(post("/api/cars/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cars)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(carInfoService).addCarsBulk(anyList());
        verify(counterService).increment();
    }

    @Test
    @DisplayName("Should return cars when posting bulk VINs")
    void shouldReturnCarsWhenPostingBulkVins() throws Exception {
        List<String> vins = Arrays.asList("1HGCM82633A004352", "2HGCM82633A004353");
        List<CarInfo> cars = Arrays.asList(car, car2);
        when(bulkRequest.getIdentifiers()).thenReturn(vins);
        when(carInfoService.getCarsByVinsBulk(vins)).thenReturn(cars);

        mockMvc.perform(post("/api/cars/bulk-by-vins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bulkRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(carInfoService).getCarsByVinsBulk(vins);
        verify(counterService).increment();
    }

    @Test
    @DisplayName("Should update car when putting valid car")
    void shouldUpdateCarWhenPuttingValidCar() throws Exception {
        when(carInfoService.updateCar(eq("1HGCM82633A004352"), any(CarInfo.class))).thenReturn(Optional.of(car));

        mockMvc.perform(put("/api/cars/1HGCM82633A004352")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isOk());

        verify(carInfoService).updateCar(eq("1HGCM82633A004352"), any(CarInfo.class));
        verify(counterService).increment();
    }

    @Test
    @DisplayName("Should return not found when updating non-existing car")
    void shouldReturnNotFoundWhenUpdatingNonExistingCar() throws Exception {
        when(carInfoService.updateCar(eq("INVALID_VIN"), any(CarInfo.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/cars/INVALID_VIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isNotFound());

        verify(carInfoService).updateCar(eq("INVALID_VIN"), any(CarInfo.class));
        verify(counterService).increment();
    }

    @Test
    @DisplayName("Should update multiple cars when putting bulk")
    void shouldUpdateCarsWhenPuttingBulk() throws Exception {
        List<CarInfo> cars = Arrays.asList(car, car2);
        when(carInfoService.updateCarsBulk(anyList())).thenReturn(cars);

        mockMvc.perform(put("/api/cars/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cars)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(carInfoService).updateCarsBulk(anyList());
        verify(counterService).increment();
    }

    @Test
    @DisplayName("Should delete car when deleting existing car")
    void shouldDeleteCarWhenDeletingExistingCar() throws Exception {
        when(carInfoService.deleteCar("1HGCM82633A004352")).thenReturn(true);

        mockMvc.perform(delete("/api/cars/1HGCM82633A004352"))
                .andExpect(status().isNoContent());

        verify(carInfoService).deleteCar("1HGCM82633A004352");
        verify(counterService).increment();
    }

    @Test
    @DisplayName("Should return not found when deleting non-existing car")
    void shouldReturnNotFoundWhenDeletingNonExistingCar() throws Exception {
        when(carInfoService.deleteCar("INVALID_VIN")).thenReturn(false);

        mockMvc.perform(delete("/api/cars/INVALID_VIN"))
                .andExpect(status().isNotFound());

        verify(carInfoService).deleteCar("INVALID_VIN");
        verify(counterService).increment();
    }
}