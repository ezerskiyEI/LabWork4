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

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private AppCache appCache;

    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }

    public Optional<Owner> getOwnerById(Long id) {
        Owner cached = appCache.getOwner(id);
        if (cached != null) {
            return Optional.of(cached);
        }

        Optional<Owner> owner = ownerRepository.findById(id);
        owner.ifPresent(o -> appCache.putOwner(id, o));
        return owner;
    }

    public Owner addOwner(Owner owner) {
        Owner saved = ownerRepository.save(owner);
        appCache.putOwner(saved.getId(), saved);
        return saved;
    }

    public Owner updateOwner(Long id, Owner owner) {
        owner.setId(id);
        Owner updated = ownerRepository.save(owner);
        appCache.putOwner(id, updated);
        return updated;
    }

    public void deleteOwner(Long id) {
        ownerRepository.deleteById(id);
        appCache.evictOwner(id);
    }
}