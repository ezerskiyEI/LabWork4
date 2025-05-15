package com.example.demo.service;

import com.example.demo.Cache.AppCache;
import com.example.demo.model.CarInfo;
import com.example.demo.repository.CarInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @Mock
    private CarInfo car;

    @Mock
    private CarInfo car2;

    @BeforeEach
    void setUp() {
        when(car.getVin()).thenReturn("1HGCM82633A004352");
        when(car2.getVin()).thenReturn("2HGCM82633A004353");
    }

    @Test
    @DisplayName("Should return cars from cache when finding by year and make")
    void shouldReturnCarsFromCacheWhenFindByYearAndMake() {
        List<CarInfo> cars = Arrays.asList(car);
        when(cache.getCarList("cars_by_year_2003_make_Honda")).thenReturn(Optional.of(cars));

        List<CarInfo> result = carInfoService.findByYearAndMake(2003, "Honda");

        assertEquals(cars, result);
        verify(repository, never()).findByYearAndMake(anyInt(), anyString());
    }

    @Test
    @DisplayName("Should return cars from repository when cache is empty for year and make")
    void shouldReturnCarsFromRepositoryWhenCacheEmptyForYearAndMake() {
        List<CarInfo> cars = Arrays.asList(car);
        when(cache.getCarList("cars_by_year_2003_make_Honda")).thenReturn(Optional.empty());
        when(repository.findByYearAndMake(2003, "Honda")).thenReturn(cars);

        List<CarInfo> result = carInfoService.findByYearAndMake(2003, "Honda");

        assertEquals(cars, result);
        verify(cache).putCarList("cars_by_year_2003_make_Honda", cars);
    }

    @Test
    @DisplayName("Should return cars from cache when finding by owner ID")
    void shouldReturnCarsFromCacheWhenFindByOwnerId() {
        List<CarInfo> cars = Arrays.asList(car);
        when(cache.getCarList("cars_by_owner_1")).thenReturn(Optional.of(cars));

        List<CarInfo> result = carInfoService.findByOwnerId(1L);

        assertEquals(cars, result);
        verify(repository, never()).findByOwnerId(anyLong());
    }

    @Test
    @DisplayName("Should return cars from repository when cache is empty for owner ID")
    void shouldReturnCarsFromRepositoryWhenCacheEmptyForOwnerId() {
        List<CarInfo> cars = Arrays.asList(car);
        when(cache.getCarList("cars_by_owner_1")).thenReturn(Optional.empty());
        when(repository.findByOwnerId(1L)).thenReturn(cars);

        List<CarInfo> result = carInfoService.findByOwnerId(1L);

        assertEquals(cars, result);
        verify(cache).putCarList("cars_by_owner_1", cars);
    }

    @Test
    @DisplayName("Should return car from cache when getting by VIN")
    void shouldReturnCarFromCacheWhenGetByVin() {
        when(cache.getCar("1HGCM82633A004352")).thenReturn(Optional.of(car));

        Optional<CarInfo> result = carInfoService.getCarByVin("1HGCM82633A004352");

        assertTrue(result.isPresent());
        assertEquals(car, result.get());
        verify(repository, never()).findById(anyString());
    }

    @Test
    @DisplayName("Should return car from repository when cache is empty for VIN")
    void shouldReturnCarFromRepositoryWhenCacheEmptyForVin() {
        when(cache.getCar("1HGCM82633A004352")).thenReturn(Optional.empty());
        when(repository.findById("1HGCM82633A004352")).thenReturn(Optional.of(car));

        Optional<CarInfo> result = carInfoService.getCarByVin("1HGCM82633A004352");

        assertTrue(result.isPresent());
        assertEquals(car, result.get());
        verify(cache).putCar(car);
    }

    @Test
    @DisplayName("Should return cars by VINs in bulk")
    void shouldReturnCarsByVinsBulk() {
        List<String> vins = Arrays.asList("1HGCM82633A004352", "2HGCM82633A004353");
        when(cache.getCar("1HGCM82633A004352")).thenReturn(Optional.of(car));
        when(cache.getCar("2HGCM82633A004353")).thenReturn(Optional.empty());
        when(repository.findById("2HGCM82633A004353")).thenReturn(Optional.of(car2));

        List<CarInfo> result = carInfoService.getCarsByVinsBulk(vins);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(car, car2)));
        verify(cache).putCar(car2);
    }

    @Test
    @DisplayName("Should add car and update cache")
    void shouldAddCarAndUpdateCache() {
        when(repository.save(car)).thenReturn(car);

        CarInfo result = carInfoService.addCar(car);

        assertEquals(car, result);
        verify(cache).putCar(car);
        verify(cache).evictAllCarLists();
        verify(cache).evictAllOwnerLists();
    }

    @Test
    @DisplayName("Should add multiple cars in bulk")
    void shouldAddCarsBulk() {
        List<CarInfo> cars = Arrays.asList(car, car2);
        when(repository.save(car)).thenReturn(car);
        when(repository.save(car2)).thenReturn(car2);

        List<CarInfo> result = carInfoService.addCarsBulk(cars);

        assertEquals(2, result.size());
        verify(cache, times(2)).putCar(any(CarInfo.class));
        verify(cache, times(2)).evictAllCarLists();
        verify(cache, times(2)).evictAllOwnerLists();
    }

    @Test
    @DisplayName("Should update car when it exists")
    void shouldUpdateCarWhenExists() {
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
    @DisplayName("Should return empty when updating non-existing car")
    void shouldReturnEmptyWhenUpdatingNonExistingCar() {
        when(repository.existsById("1HGCM82633A004352")).thenReturn(false);

        Optional<CarInfo> result = carInfoService.updateCar("1HGCM82633A004352", car);

        assertFalse(result.isPresent());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should update multiple cars in bulk")
    void shouldUpdateCarsBulk() {
        List<CarInfo> cars = Arrays.asList(car, car2);
        when(repository.existsById("1HGCM82633A004352")).thenReturn(true);
        when(repository.existsById("2HGCM82633A004353")).thenReturn(true);
        when(repository.save(car)).thenReturn(car);
        when(repository.save(car2)).thenReturn(car2);

        List<CarInfo> result = carInfoService.updateCarsBulk(cars);

        assertEquals(2, result.size());
        verify(cache, times(2)).putCar(any(CarInfo.class));
        verify(cache, times(2)).evictAllCarLists();
        verify(cache, times(2)).evictAllOwnerLists();
    }

    @Test
    @DisplayName("Should delete car when it exists")
    void shouldDeleteCarWhenExists() {
        when(repository.existsById("1HGCM82633A004352")).thenReturn(true);

        boolean result = carInfoService.deleteCar("1HGCM82633A004352");

        assertTrue(result);
        verify(repository).deleteById("1HGCM82633A004352");
        verify(cache).evictCar("1HGCM82633A004352");
        verify(cache).evictAllCarLists();
        verify(cache).evictAllOwnerLists();
    }

    @Test
    @DisplayName("Should return false when deleting non-existing car")
    void shouldReturnFalseWhenDeletingNonExistingCar() {
        when(repository.existsById("1HGCM82633A004352")).thenReturn(false);

        boolean result = carInfoService.deleteCar("1HGCM82633A004352");

        assertFalse(result);
        verify(repository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("Should return all cars from cache")
    void shouldReturnAllCarsFromCache() {
        List<CarInfo> cars = Arrays.asList(car);
        when(cache.getCarList("all_cars")).thenReturn(Optional.of(cars));

        List<CarInfo> result = carInfoService.getAllCars();

        assertEquals(cars, result);
        verify(repository, never()).findAll();
    }

    @Test
    @DisplayName("Should return all cars from repository when cache is empty")
    void shouldReturnAllCarsFromRepositoryWhenCacheEmpty() {
        List<CarInfo> cars = Arrays.asList(car);
        when(cache.getCarList("all_cars")).thenReturn(Optional.empty());
        when(repository.findAll()).thenReturn(cars);

        List<CarInfo> result = carInfoService.getAllCars();

        assertEquals(cars, result);
        verify(cache).putCarList("all_cars", cars);
    }
}