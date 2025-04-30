package com.example.demo.service;


import com.example.demo.Cache.AppCache;
import com.example.demo.model.Owner;
import com.example.demo.repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnerService {

    @Autowired
    private OwnerRepository ownerRepo;

    @Autowired
    private AppCache cache;

    public List<Owner> getAllOwners() {
        return ownerRepo.findAll();
    }

    public Owner getOwner(Long id) {
        Owner cached = cache.getOwner(id);
        if (cached != null) return cached;

        return ownerRepo.findById(id).map(owner -> {
            cache.putOwner(id, owner);
            return owner;
        }).orElse(null);
    }

    public Owner addOwner(Owner owner) {
        Owner saved = ownerRepo.save(owner);
        cache.putOwner(saved.getId(), saved);
        return saved;
    }

    public Owner updateOwner(Long id, Owner owner) {
        if (!ownerRepo.existsById(id)) return null;
        owner.setId(id);
        Owner updated = ownerRepo.save(owner);
        cache.putOwner(id, updated);
        return updated;
    }

    public void deleteOwner(Long id) {
        ownerRepo.deleteById(id);
        cache.evictOwner(id);
    }
}