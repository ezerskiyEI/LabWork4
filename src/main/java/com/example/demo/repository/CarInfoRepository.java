package com.example.demo.repository;

import com.example.demo.model.CarInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarInfoRepository extends JpaRepository<CarInfo, String> {
    Optional<CarInfo> findByVin(String vin);
}