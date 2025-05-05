package com.example.demo.service;

import com.example.demo.Cache.AppCache;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Owner;
import com.example.demo.repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnerService {
    private final OwnerRepository repository;
    private final AppCache cache;

    @Autowired
    public OwnerService(OwnerRepository repository, AppCache cache) {
        this.repository = repository;
        this.cache = cache;
    }

    public List<Owner> getAllOwners() {
        return cache.getOwnerList("all_owners")
                .orElseGet(() -> {
                    List<Owner> owners = repository.findAll();
                    cache.putOwnerList("all_owners", owners);
                    return owners;
                });
    }

    public Owner getOwner(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid owner ID");
        }

        return cache.getOwner(id)
                .orElseGet(() -> {
                    Owner owner = repository.findById(id)
                            .orElseThrow(() -> new NotFoundException("Owner not found with id: " + id));
                    cache.putOwner(owner);
                    return owner;
                });
    }

    public Owner addOwner(Owner owner) {
        Owner saved = repository.save(owner);
        cache.putOwner(saved);
        cache.evictAllOwnerLists();
        return saved;
    }

    public Owner updateOwner(Long id, Owner owner) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Owner not found with id: " + id);
        }
        owner.setId(id);
        Owner updated = repository.save(owner);
        cache.putOwner(updated);
        cache.evictAllOwnerLists();
        return updated;
    }

    public void deleteOwner(Long id) {
        repository.deleteById(id);
        cache.evictOwner(id);
        cache.evictAllOwnerLists();
    }

    public List<Owner> getOwnersByCarVin(String vin) {
        String cacheKey = "owners_by_car_" + vin;
        return cache.getOwnerList(cacheKey)
                .orElseGet(() -> {
                    List<Owner> owners = repository.findByCarsVin(vin);
                    cache.putOwnerList(cacheKey, owners);
                    return owners;
                });
    }
}