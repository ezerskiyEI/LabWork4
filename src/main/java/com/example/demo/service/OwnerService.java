package com.example.demo.service;

import com.example.demo.Cache.AppCache;
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
        String cacheKey = "all_owners";
        List<Owner> cached = cache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<Owner> owners = repository.findAll();
        cache.put(cacheKey, owners);
        return owners;
    }

    public Owner getOwner(Long id) {
        Owner cached = cache.getOwner(id);
        if (cached != null) {
            return cached;
        }

        Owner owner = repository.findById(id).orElse(null);
        if (owner != null) {
            cache.putOwner(id, owner);
        }
        return owner;
    }

    public Owner addOwner(Owner owner) {
        Owner saved = repository.save(owner);
        cache.putOwner(saved.getId(), saved);
        cache.evict("all_owners");
        return saved;
    }

    public Owner updateOwner(Long id, Owner owner) {
        owner.setId(id);
        Owner updated = repository.save(owner);
        cache.putOwner(id, updated);
        cache.evict("all_owners");
        return updated;
    }

    public void deleteOwner(Long id) {
        repository.deleteById(id);
        cache.evictOwner(id);
        cache.evict("all_owners");
    }
}