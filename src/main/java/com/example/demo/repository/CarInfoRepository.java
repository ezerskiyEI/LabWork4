package com.example.demo.repository;

import com.example.demo.model.CarInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CarInfoRepository extends JpaRepository<CarInfo, String> {

    Optional<CarInfo> findByVin(String vin);

    void deleteByVin(String vin);

    @Query("SELECT c FROM CarInfo c JOIN c.owners o WHERE o.name = :ownerName")
    List<CarInfo> findCarsByOwnerName(@Param("ownerName") String ownerName);
}