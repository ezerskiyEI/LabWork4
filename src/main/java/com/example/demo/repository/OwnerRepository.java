package com.example.demo.repository;

import com.example.demo.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    @Query("SELECT o FROM Owner o JOIN o.cars c WHERE c.year > :minYear")
    List<Owner> findOwnersWithCarsNewerThan(@Param("minYear") int minYear);
}