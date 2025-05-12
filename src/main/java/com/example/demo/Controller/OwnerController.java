package com.example.demo.Controller;

import com.example.demo.model.Owner;
import com.example.demo.service.OwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/owners")
@Tag(name = "Owner Management", description = "API for managing car owners")
public class OwnerController {
    private static final Logger logger = LoggerFactory.getLogger(OwnerController.class);

    private final OwnerService ownerService;

    @Autowired
    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @Operation(summary = "Get all owners")
    @GetMapping
    public ResponseEntity<List<Owner>> getAll() {
        logger.info("Getting all owners");
        return ResponseEntity.ok(ownerService.getAllOwners());
    }

    @Operation(summary = "Get owner by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the owner"),
            @ApiResponse(responseCode = "404", description = "Owner not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Owner> getById(
            @Parameter(description = "ID of the owner") @PathVariable Long id) {
        logger.info("Getting owner by ID: {}", id);
        return ownerService.getOwner(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Add a new owner")
    @PostMapping
    public ResponseEntity<Owner> add(
            @Parameter(description = "Owner information to add") @RequestBody Owner owner) {
        logger.info("Adding new owner with name: {}", owner.getName());
        return ResponseEntity.ok(ownerService.addOwner(owner));
    }

    @Operation(summary = "Update owner information")
    @PutMapping("/{id}")
    public ResponseEntity<Owner> update(
            @Parameter(description = "ID of the owner to update") @PathVariable Long id,
            @Parameter(description = "Updated owner information") @RequestBody Owner owner) {
        logger.info("Updating owner with ID: {}", id);
        return ownerService.updateOwner(id, owner)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete an owner")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the owner to delete") @PathVariable Long id) {
        logger.info("Deleting owner with ID: {}", id);
        return ownerService.deleteOwner(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}