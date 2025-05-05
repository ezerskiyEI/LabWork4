package com.example.demo.repository;

import com.example.demo.model.CarInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarInfoRepository extends JpaRepository<CarInfo, String> {
    @Query("SELECT c FROM CarInfo c WHERE c.year = :year AND c.make = :make")
    List<CarInfo> findByYearAndMake(@Param("year") int year, @Param("make") String make);

    @Query("SELECT c FROM CarInfo c JOIN c.owners o WHERE o.id = :ownerId")
    List<CarInfo> findByOwnerId(@Param("ownerId") Long ownerId);
}