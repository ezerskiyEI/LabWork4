package com.example.demo.service;

import com.example.demo.Cache.AppCache;
import com.example.demo.model.Owner;
import com.example.demo.repository.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock
    private OwnerRepository repository;

    @Mock
    private AppCache cache;

    @InjectMocks
    private OwnerService ownerService;

    private Owner owner;

    @BeforeEach
    void setUp() {
        owner = new Owner();
        owner.setId(1L);
        owner.setName("John Doe");
    }

    @Test
    void getAllOwners_fromCache() {
        List<Owner> owners = Arrays.asList(owner);
        when(cache.getOwnerList("all_owners")).thenReturn(Optional.of(owners));

        List<Owner> result = ownerService.getAllOwners();

        assertEquals(owners, result);
        verify(repository, never()).findAll();
    }

    @Test
    void getAllOwners_fromRepository() {
        List<Owner> owners = Arrays.asList(owner);
        when(cache.getOwnerList("all_owners")).thenReturn(Optional.empty());
        when(repository.findAll()).thenReturn(owners);

        List<Owner> result = ownerService.getAllOwners();

        assertEquals(owners, result);
        verify(cache).putOwnerList("all_owners", owners);
    }

    @Test
    void getOwner_fromCache() {
        when(cache.getOwner(1L)).thenReturn(Optional.of(owner));

        Optional<Owner> result = ownerService.getOwner(1L);

        assertTrue(result.isPresent());
        assertEquals(owner, result.get());
        verify(repository, never()).findById(anyLong());
    }

    @Test
    void getOwner_fromRepository() {
        when(cache.getOwner(1L)).thenReturn(Optional.empty());
        when(repository.findById(1L)).thenReturn(Optional.of(owner));

        Optional<Owner> result = ownerService.getOwner(1L);

        assertTrue(result.isPresent());
        assertEquals(owner, result.get());
        verify(cache).putOwner(owner);
    }

    @Test
    void getOwnersByIdsBulk() {
        Owner owner2 = new Owner();
        owner2.setId(2L);
        List<Long> ids = Arrays.asList(1L, 2L);
        when(cache.getOwner(1L)).thenReturn(Optional.of(owner));
        when(cache.getOwner(2L)).thenReturn(Optional.empty());
        when(repository.findById(2L)).thenReturn(Optional.of(owner2));

        List<Owner> result = ownerService.getOwnersByIdsBulk(ids);

        assertEquals(2, result.size());
        verify(cache).putOwner(owner2);
    }

    @Test
    void addOwner() {
        when(repository.save(owner)).thenReturn(owner);

        Owner result = ownerService.addOwner(owner);

        assertEquals(owner, result);
        verify(cache).putOwner(owner);
        verify(cache).evictAllOwnerLists();
        verify(cache).evictAllCarLists();
    }

    @Test
    void addOwnersBulk() {
        Owner owner2 = new Owner();
        owner2.setId(2L);
        List<Owner> owners = Arrays.asList(owner, owner2);
        when(repository.save(owner)).thenReturn(owner);
        when(repository.save(owner2)).thenReturn(owner2);

        List<Owner> result = ownerService.addOwnersBulk(owners);

        assertEquals(2, result.size());
        verify(cache, times(2)).putOwner(any(Owner.class));
    }

    @Test
    void updateOwner_exists() {
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.save(owner)).thenReturn(owner);

        Optional<Owner> result = ownerService.updateOwner(1L, owner);

        assertTrue(result.isPresent());
        assertEquals(owner, result.get());
        verify(cache).putOwner(owner);
        verify(cache).evictAllOwnerLists();
        verify(cache).evictAllCarLists();
    }

    @Test
    void updateOwner_notExists() {
        when(repository.existsById(1L)).thenReturn(false);

        Optional<Owner> result = ownerService.updateOwner(1L, owner);

        assertFalse(result.isPresent());
    }

    @Test
    void updateOwnersBulk() {
        Owner owner2 = new Owner();
        owner2.setId(2L);
        List<Owner> owners = Arrays.asList(owner, owner2);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.existsById(2L)).thenReturn(true);
        when(repository.save(owner)).thenReturn(owner);
        when(repository.save(owner2)).thenReturn(owner2);

        List<Owner> result = ownerService.updateOwnersBulk(owners);

        assertEquals(2, result.size());
        verify(cache, times(2)).putOwner(any(Owner.class));
    }

    @Test
    void deleteOwner_exists() {
        when(repository.existsById(1L)).thenReturn(true);

        boolean result = ownerService.deleteOwner(1L);

        assertTrue(result);
        verify(repository).deleteById(1L);
        verify(cache).evictOwner(1L);
    }

    @Test
    void deleteOwner_notExists() {
        when(repository.existsById(1L)).thenReturn(false);

        boolean result = ownerService.deleteOwner(1L);

        assertFalse(result);
        verify(repository, never()).deleteById(anyLong());
    }
}