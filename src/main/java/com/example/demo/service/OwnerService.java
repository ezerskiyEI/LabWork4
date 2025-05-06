package com.example.demo.service;

import com.example.demo.Cache.AppCache;
import com.example.demo.model.Owner;
import com.example.demo.repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<Owner> getOwner(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }

        Optional<Owner> cachedOwner = cache.getOwner(id);
        if (cachedOwner.isPresent()) {
            return cachedOwner;
        }
        Optional<Owner> dbOwner = repository.findById(id);
        dbOwner.ifPresent(owner -> cache.putOwner(owner));
        return dbOwner;
    }

    public Owner addOwner(Owner owner) {
        Owner saved = repository.save(owner);
        cache.putOwner(saved);
        cache.evictAllOwnerLists();
        return saved;
    }

    public Optional<Owner> updateOwner(Long id, Owner owner) {
        if (!repository.existsById(id)) {
            return Optional.empty();
        }
        owner.setId(id);
        Owner updated = repository.save(owner);
        cache.putOwner(updated);
        cache.evictAllOwnerLists();
        return Optional.of(updated);
    }

    public boolean deleteOwner(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        cache.evictOwner(id);
        cache.evictAllOwnerLists();
        return true;
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