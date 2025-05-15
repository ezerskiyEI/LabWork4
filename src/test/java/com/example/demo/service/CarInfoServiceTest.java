package com.example.demo.service;

import com.example.demo.Cache.AppCache;
import com.example.demo.model.CarInfo;
import com.example.demo.repository.CarInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarInfoServiceTest {

    @Mock
    private CarInfoRepository repository;

    @Mock
    private AppCache cache;

    @InjectMocks
    private CarInfoService carInfoService;

    private CarInfo car;

    @BeforeEach
    void setUp() {
        car = new CarInfo();
        car.setVin("1HGCM82633A004352");
        car.setMake("Honda");
        car.setModel("Accord");
        car.setYear(2003);
    }

    @Test
    void findByYearAndMake_fromCache() {
        List<CarInfo> cars = Arrays.asList(car);
        when(cache.getCarList("cars_by_year_2003_make_Honda")).thenReturn(Optional.of(cars));

        List<CarInfo> result = carInfoService.findByYearAndMake(2003, "Honda");

        assertEquals(cars, result);
        verify(repository, never()).findByYearAndMake(anyInt(), anyString());
    }

    @Test
    void findByYearAndMake_fromRepository() {
        List<CarInfo> cars = Arrays.asList(car);
        when(cache.getCarList("cars_by_year_2003_make_Honda")).thenReturn(Optional.empty());
        when(repository.findByYearAndMake(2003, "Honda")).thenReturn(cars);

        List<CarInfo> result = carInfoService.findByYearAndMake(2003, "Honda");

        assertEquals(cars, result);
        verify(cache).putCarList("cars_by_year_2003_make_Honda", cars);
    }

    @Test
    void findByOwnerId_fromCache() {
        List<CarInfo> cars = Arrays.asList(car);
        when(cache.getCarList("cars_by_owner_1")).thenReturn(Optional.of(cars));

        List<CarInfo> result = carInfoService.findByOwnerId(1L);

        assertEquals(cars, result);
        verify(repository, never()).findByOwnerId(anyLong());
    }

    @Test
    void findByOwnerId_fromRepository() {
        List<CarInfo> cars = Arrays.asList(car);
        when(cache.getCarList("cars_by_owner_1")).thenReturn(Optional.empty());
        when(repository.findByOwnerId(1L)).thenReturn(cars);

        List<CarInfo> result = carInfoService.findByOwnerId(1L);

        assertEquals(cars, result);
        verify(cache).putCarList("cars_by_owner_1", cars);
    }

    @Test
    void getCarByVin_fromCache() {
        when(cache.getCar("1HGCM82633A004352")).thenReturn(Optional.of(car));

        Optional<CarInfo> result = carInfoService.getCarByVin("1HGCM82633A004352");

        assertTrue(result.isPresent());
        assertEquals(car, result.get());
        verify(repository, never()).findById(anyString());
    }

    @Test
    void getCarByVin_fromRepository() {
        when(cache.getCar("1HGCM82633A004352")).thenReturn(Optional.empty());
        when(repository.findById("1HGCM82633A004352")).thenReturn(Optional.of(car));

        Optional<CarInfo> result = carInfoService.getCarByVin("1HGCM82633A004352");

        assertTrue(result.isPresent());
        assertEquals(car, result.get());
        verify(cache).putCar(car);
    }

    @Test
    void getCarsByVinsBulk() {
        CarInfo car2 = new CarInfo();
        car2.setVin("2HGCM82633A004353");
        List<String> vins = Arrays.asList("1HGCM82633A004352", "2HGCM82633A004353");
        when(cache.getCar("1HGCM82633A004352")).thenReturn(Optional.of(car));
        when(cache.getCar("2HGCM82633A004353")).thenReturn(Optional.empty());
        when(repository.findById("2HGCM82633A004353")).thenReturn(Optional.of(car2));

        List<CarInfo> result = carInfoService.getCarsByVinsBulk(vins);

        assertEquals(2, result.size());
        verify(cache).putCar(car2);
    }

    @Test
    void addCar() {
        when(repository.save(car)).thenReturn(car);

        CarInfo result = carInfoService.addCar(car);

        assertEquals(car, result);
        verify(cache).putCar(car);
        verify(cache).evictAllCarLists();
        verify(cache).evictAllOwnerLists();
    }

    @Test
    void addCarsBulk() {
        CarInfo car2 = new CarInfo();
        car2.setVin("2HGCM82633A004353");
        List<CarInfo> cars = Arrays.asList(car, car2);
        when(repository.save(car)).thenReturn(car);
        when(repository.save(car2)).thenReturn(car2);

        List<CarInfo> result = carInfoService.addCarsBulk(cars);

        assertEquals(2, result.size());
        verify(cache, times(2)).putCar(any(CarInfo.class));
    }

    @Test
    void updateCar_exists() {
        when(repository.existsById("1HGCM82633A004352")).thenReturn(true);
        when(repository.save(car)).thenReturn(car);

        Optional<CarInfo> result = carInfoService.updateCar("1HGCM82633A004352", car);

        assertTrue(result.isPresent());
        assertEquals(car, result.get());
        verify(cache).putCar(car);
        verify(cache).evictAllCarLists();
        verify(cache).evictAllOwnerLists();
    }

    @Test
    void updateCar_notExists() {
        when(repository.existsById("1HGCM82633A004352")).thenReturn(false);

        Optional<CarInfo> result = carInfoService.updateCar("1HGCM82633A004352", car);

        assertFalse(result.isPresent());
    }

    @Test
    void updateCarsBulk() {
        CarInfo car2 = new CarInfo();
        car2.setVin("2HGCM82633A004353");
        List<CarInfo> cars = Arrays.asList(car, car2);
        when(repository.existsById("1HGCM82633A004352")).thenReturn(true);
        when(repository.existsById("2HGCM82633A004353")).thenReturn(true);
        when(repository.save(car)).thenReturn(car);
        when(repository.save(car2)).thenReturn(car2);

        List<CarInfo> result = carInfoService.updateCarsBulk(cars);

        assertEquals(2, result.size());
        verify(cache, times(2)).putCar(any(CarInfo.class));
    }

    @Test
    void deleteCar_exists() {
        when(repository.existsById("1HGCM82633A004352")).thenReturn(true);

        boolean result = carInfoService.deleteCar("1HGCM82633A004352");

        assertTrue(result);
        verify(repository).deleteById("1HGCM82633A004352");
        verify(cache).evictCar("1HGCM82633A004352");
    }

    @Test
    void deleteCar_notExists() {
        when(repository.existsById("1HGCM82633A004352")).thenReturn(false);

        boolean result = carInfoService.deleteCar("1HGCM82633A004352");

        assertFalse(result);
        verify(repository, never()).deleteById(anyString());
    }

    @Test
    void getAllCars_fromCache() {
        List<CarInfo> cars = Arrays.asList(car);
        when(cache.getCarList("all_cars")).thenReturn(Optional.of(cars));

        List<CarInfo> result = carInfoService.getAllCars();

        assertEquals(cars, result);
        verify(repository, never()).findAll();
    }

    @Test
    void getAllCars_fromRepository() {
        List<CarInfo> cars = Arrays.asList(car);
        when(cache.getCarList("all_cars")).thenReturn(Optional.empty());
        when(repository.findAll()).thenReturn(cars);

        List<CarInfo> result = carInfoService.getAllCars();

        assertEquals(cars, result);
        verify(cache).putCarList("all_cars", cars);
    }
}