package com.example.demo.repository;

import com.example.demo.model.CarInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarInfoRepository extends JpaRepository<CarInfo, String> {
    @Query("SELECT c FROM CarInfo c JOIN c.owners o WHERE o.name = :ownerName")
    List<CarInfo> findByOwnerName(@Param("ownerName") String ownerName);

    @Query("SELECT c FROM CarInfo c WHERE c.make = :make AND c.year > :minYear")
    List<CarInfo> findByMakeAndYearAfter(
            @Param("make") String make,
            @Param("minYear") int minYear
    );
}