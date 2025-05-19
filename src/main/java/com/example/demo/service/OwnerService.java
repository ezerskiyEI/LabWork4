package com.example.demo.service;

import com.example.demo.Cache.AppCache;
import com.example.demo.model.Owner;
import com.example.demo.repository.OwnerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OwnerService {

    private final OwnerRepository repository;
    private final AppCache cache;
    private final RequestCounterService counterService;

    public OwnerService(OwnerRepository repository, AppCache cache, RequestCounterService counterService) {
        this.repository = repository;
        this.cache = cache;
        this.counterService = counterService;
    }

    public List<Owner> getAllOwners() {
        counterService.increment();
        Optional<List<Owner>> cachedOwners = cache.getOwnerList("all_owners");
        if (cachedOwners.isPresent()) {
            return cachedOwners.get();
        }
        List<Owner> owners = repository.findAll();
        cache.putOwnerList("all_owners", owners);
        return owners;
    }

    public Optional<Owner> getOwner(Long id) {
        counterService.increment();
        Optional<Owner> cachedOwner = cache.getOwner(id);
        if (cachedOwner.isPresent()) {
            return cachedOwner;
        }
        Optional<Owner> owner = repository.findById(id);
        owner.ifPresent(cache::putOwner);
        return owner;
    }

    public List<Owner> getOwnersByIdsBulk(List<Long> ids) {
        counterService.increment();
        return ids.stream()
                .map(this::getOwner)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public Owner addOwner(Owner owner) {
        counterService.increment();
        Owner savedOwner = repository.save(owner);
        cache.putOwner(savedOwner);
        cache.evictAllOwnerLists();
        cache.evictAllCarLists();
        return savedOwner;
    }

    public List<Owner> addOwnersBulk(List<Owner> owners) {
        counterService.increment();
        return owners.stream()
                .map(this::addOwner)
                .collect(Collectors.toList());
    }

    public Optional<Owner> updateOwner(Long id, Owner owner) {
        counterService.increment();
        if (!repository.existsById(id)) {
            return Optional.empty();
        }
        owner.setId(id);
        Owner updatedOwner = repository.save(owner);
        cache.putOwner(updatedOwner);
        cache.evictAllOwnerLists();
        cache.evictAllCarLists();
        return Optional.of(updatedOwner);
    }

    public List<Owner> updateOwnersBulk(List<Owner> owners) {
        counterService.increment();
        return owners.stream()
                .filter(owner -> owner.getId() != null)
                .map(owner -> updateOwner(owner.getId(), owner))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public boolean deleteOwner(Long id) {
        counterService.increment();
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        cache.evictOwner(id);
        cache.evictAllOwnerLists();
        cache.evictAllCarLists();
        return true;
    }
}