package com.example.demo.service;

import com.example.demo.Cache.AppCache;
import com.example.demo.model.Owner;
import com.example.demo.repository.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @Mock
    private Owner owner;

    @Mock
    private Owner owner2;

    @BeforeEach
    void setUp() {
        when(owner.getId()).thenReturn(1L);
        when(owner.getName()).thenReturn("John Doe");
        when(owner2.getId()).thenReturn(2L);
        when(owner2.getName()).thenReturn("Jane Doe");
    }

    @Test
    @DisplayName("Should return all owners from cache")
    void shouldReturnAllOwnersFromCache() {
        List<Owner> owners = Arrays.asList(owner);
        when(cache.getOwnerList("all_owners")).thenReturn(Optional.of(owners));

        List<Owner> result = ownerService.getAllOwners();

        assertEquals(owners, result);
        verify(repository, never()).findAll();
    }

    @Test
    @DisplayName("Should return all owners from repository when cache is empty")
    void shouldReturnAllOwnersFromRepositoryWhenCacheEmpty() {
        List<Owner> owners = Arrays.asList(owner);
        when(cache.getOwnerList("all_owners")).thenReturn(Optional.empty());
        when(repository.findAll()).thenReturn(owners);

        List<Owner> result = ownerService.getAllOwners();

        assertEquals(owners, result);
        verify(cache).putOwnerList("all_owners", owners);
    }

    @Test
    @DisplayName("Should return owner from cache when getting by ID")
    void shouldReturnOwnerFromCacheWhenGetById() {
        when(cache.getOwner(1L)).thenReturn(Optional.of(owner));

        Optional<Owner> result = ownerService.getOwner(1L);

        assertTrue(result.isPresent());
        assertEquals(owner, result.get());
        verify(repository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should return owner from repository when cache is empty for ID")
    void shouldReturnOwnerFromRepositoryWhenCacheEmptyForId() {
        when(cache.getOwner(1L)).thenReturn(Optional.empty());
        when(repository.findById(1L)).thenReturn(Optional.of(owner));

        Optional<Owner> result = ownerService.getOwner(1L);

        assertTrue(result.isPresent());
        assertEquals(owner, result.get());
        verify(cache).putOwner(owner);
    }

    @Test
    @DisplayName("Should return owners by IDs in bulk")
    void shouldReturnOwnersByIdsBulk() {
        List<Long> ids = Arrays.asList(1L, 2L);
        when(cache.getOwner(1L)).thenReturn(Optional.of(owner));
        when(cache.getOwner(2L)).thenReturn(Optional.empty());
        when(repository.findById(2L)).thenReturn(Optional.of(owner2));

        List<Owner> result = ownerService.getOwnersByIdsBulk(ids);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(owner, owner2)));
        verify(cache).putOwner(owner2);
    }

    @Test
    @DisplayName("Should add owner and update cache")
    void shouldAddOwnerAndUpdateCache() {
        when(repository.save(owner)).thenReturn(owner);

        Owner result = ownerService.addOwner(owner);

        assertEquals(owner, result);
        verify(cache).putOwner(owner);
        verify(cache).evictAllOwnerLists();
        verify(cache).evictAllCarLists();
    }

    @Test
    @DisplayName("Should add multiple owners in bulk")
    void shouldAddOwnersBulk() {
        List<Owner> owners = Arrays.asList(owner, owner2);
        when(repository.save(owner)).thenReturn(owner);
        when(repository.save(owner2)).thenReturn(owner2);

        List<Owner> result = ownerService.addOwnersBulk(owners);

        assertEquals(2, result.size());
        verify(cache, times(2)).putOwner(any(Owner.class));
        verify(cache, times(2)).evictAllOwnerLists();
        verify(cache, times(2)).evictAllCarLists();
    }

    @Test
    @DisplayName("Should update owner when it exists")
    void shouldUpdateOwnerWhenExists() {
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
    @DisplayName("Should return empty when updating non-existing owner")
    void shouldReturnEmptyWhenUpdatingNonExistingOwner() {
        when(repository.existsById(1L)).thenReturn(false);

        Optional<Owner> result = ownerService.updateOwner(1L, owner);

        assertFalse(result.isPresent());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should update multiple owners in bulk")
    void shouldUpdateOwnersBulk() {
        List<Owner> owners = Arrays.asList(owner, owner2);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.existsById(2L)).thenReturn(true);
        when(repository.save(owner)).thenReturn(owner);
        when(repository.save(owner2)).thenReturn(owner2);

        List<Owner> result = ownerService.updateOwnersBulk(owners);

        assertEquals(2, result.size());
        verify(cache, times(2)).putOwner(any(Owner.class));
        verify(cache, times(2)).evictAllOwnerLists();
        verify(cache, times(2)).evictAllCarLists();
    }

    @Test
    @DisplayName("Should delete owner when it exists")
    void shouldDeleteOwnerWhenExists() {
        when(repository.existsById(1L)).thenReturn(true);

        boolean result = ownerService.deleteOwner(1L);

        assertTrue(result);
        verify(repository).deleteById(1L);
        verify(cache).evictOwner(1L);
        verify(cache).evictAllOwnerLists();
        verify(cache).evictAllCarLists();
    }

    @Test
    @DisplayName("Should return false when deleting non-existing owner")
    void shouldReturnFalseWhenDeletingNonExistingOwner() {
        when(repository.existsById(1L)).thenReturn(false);

        boolean result = ownerService.deleteOwner(1L);

        assertFalse(result);
        verify(repository, never()).deleteById(anyLong());
    }
}