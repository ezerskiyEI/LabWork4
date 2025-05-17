package com.example.demo.Controller;

import com.example.demo.dto.BulkOperationRequest;
import com.example.demo.model.Owner;
import com.example.demo.service.OwnerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {

    @Mock
    private OwnerService ownerService;

    @InjectMocks
    private OwnerController ownerController;

    private MockMvc mockMvc;

    @Mock
    private Owner owner;

    @Mock
    private Owner owner2;

    @Mock
    private BulkOperationRequest bulkRequest;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ownerController).build();
        objectMapper = new ObjectMapper();
        when(owner.getId()).thenReturn(1L);
        when(owner2.getId()).thenReturn(2L);
    }

    @Test
    @DisplayName("Should return all owners when getting all owners")
    void shouldReturnAllOwnersWhenGettingAllOwners() throws Exception {
        List<Owner> owners = Arrays.asList(owner, owner2);
        when(ownerService.getAllOwners()).thenReturn(owners);

        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(ownerService).getAllOwners();
    }

    @Test
    @DisplayName("Should return owner when getting by ID")
    void shouldReturnOwnerWhenGettingById() throws Exception {
        when(ownerService.getOwner(1L)).thenReturn(Optional.of(owner));

        mockMvc.perform(get("/owners/1"))
                .andExpect(status().isOk());

        verify(ownerService).getOwner(1L);
    }

    @Test
    @DisplayName("Should return not found when getting non-existing owner by ID")
    void shouldReturnNotFoundWhenGettingNonExistingOwnerById() throws Exception {
        when(ownerService.getOwner(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/owners/999"))
                .andExpect(status().isNotFound());

        verify(ownerService).getOwner(999L);
    }

    @Test
    @DisplayName("Should add owner when posting valid owner")
    void shouldAddOwnerWhenPostingValidOwner() throws Exception {
        when(ownerService.addOwner(any(Owner.class))).thenReturn(owner);

        mockMvc.perform(post("/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isOk());

        verify(ownerService).addOwner(any(Owner.class));
    }

    @Test
    @DisplayName("Should add multiple owners when posting bulk")
    void shouldAddOwnersWhenPostingBulk() throws Exception {
        List<Owner> owners = Arrays.asList(owner, owner2);
        when(ownerService.addOwnersBulk(anyList())).thenReturn(owners);

        mockMvc.perform(post("/owners/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owners)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(ownerService).addOwnersBulk(anyList());
    }

    @Test
    @DisplayName("Should return owners when posting bulk IDs")
    void shouldReturnOwnersWhenPostingBulkIds() throws Exception {
        List<String> ids = Arrays.asList("1", "2");
        List<Long> longIds = Arrays.asList(1L, 2L);
        List<Owner> owners = Arrays.asList(owner, owner2);
        when(bulkRequest.getIdentifiers()).thenReturn(ids);
        when(ownerService.getOwnersByIdsBulk(longIds)).thenReturn(owners);

        mockMvc.perform(post("/owners/bulk-by-ids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bulkRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(ownerService).getOwnersByIdsBulk(longIds);
    }

    @Test
    @DisplayName("Should return bad request when posting invalid IDs in bulk")
    void shouldReturnBadRequestWhenPostingInvalidIdsInBulk() throws Exception {
        List<String> ids = Arrays.asList("1", "invalid");
        when(bulkRequest.getIdentifiers()).thenReturn(ids);

        mockMvc.perform(post("/owners/bulk-by-ids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bulkRequest)))
                .andExpect(status().isBadRequest());

        verify(ownerService, never()).getOwnersByIdsBulk(anyList());
    }

    @Test
    @DisplayName("Should update owner when putting valid owner")
    void shouldUpdateOwnerWhenPuttingValidOwner() throws Exception {
        when(ownerService.updateOwner(eq(1L), any(Owner.class))).thenReturn(Optional.of(owner));

        mockMvc.perform(put("/owners/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isOk());

        verify(ownerService).updateOwner(eq(1L), any(Owner.class));
    }

    @Test
    @DisplayName("Should return not found when updating non-existing owner")
    void shouldReturnNotFoundWhenUpdatingNonExistingOwner() throws Exception {
        when(ownerService.updateOwner(eq(999L), any(Owner.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/owners/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isNotFound());

        verify(ownerService).updateOwner(eq(999L), any(Owner.class));
    }

    @Test
    @DisplayName("Should update multiple owners when putting bulk")
    void shouldUpdateOwnersWhenPuttingBulk() throws Exception {
        List<Owner> owners = Arrays.asList(owner, owner2);
        when(ownerService.updateOwnersBulk(anyList())).thenReturn(owners);

        mockMvc.perform(put("/owners/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owners)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(ownerService).updateOwnersBulk(anyList());
    }

    @Test
    @DisplayName("Should delete owner when deleting existing owner")
    void shouldDeleteOwnerWhenDeletingExistingOwner() throws Exception {
        when(ownerService.deleteOwner(1L)).thenReturn(true);

        mockMvc.perform(delete("/owners/1"))
                .andExpect(status().isNoContent());

        verify(ownerService).deleteOwner(1L);
    }

    @Test
    @DisplayName("Should return not found when deleting non-existing owner")
    void shouldReturnNotFoundWhenDeletingNonExistingOwner() throws Exception {
        when(ownerService.deleteOwner(999L)).thenReturn(false);

        mockMvc.perform(delete("/owners/999"))
                .andExpect(status().isNotFound());

        verify(ownerService).deleteOwner(999L);
    }
}