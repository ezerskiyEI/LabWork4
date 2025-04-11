package com.example.demo.service;


import com.example.demo.Cache.EntityCache;
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
    private EntityCache entityCache;

    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }

    public Optional<Owner> getOwnerById(Long id) {
        String key = "owner:" + id;
        if (entityCache.contains(key)) {
            return Optional.ofNullable(entityCache.get(key, Owner.class));
        }

        Optional<Owner> owner = ownerRepository.findById(id);
        owner.ifPresent(o -> entityCache.put(key, o));
        return owner;
    }

    public Owner addOwner(Owner owner) {
        Owner saved = ownerRepository.save(owner);
        entityCache.put("owner:" + saved.getId(), saved);
        return saved;
    }

    public Owner updateOwner(Long id, Owner updated) {
        updated.setId(id);
        Owner saved = ownerRepository.save(updated);
        entityCache.put("owner:" + id, saved);
        return saved;
    }

    public void deleteOwner(Long id) {
        ownerRepository.deleteById(id);
        entityCache.evict("owner:" + id);
    }
}