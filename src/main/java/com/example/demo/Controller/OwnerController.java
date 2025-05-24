package com.example.demo.Controller;

import com.example.demo.model.Owner;
import com.example.demo.service.OwnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping
    public ResponseEntity<List<Owner>> getAll() {
        return ResponseEntity.ok(ownerService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Owner>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ownerService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Owner> create(@RequestBody Owner owner) {
        return ResponseEntity.ok(ownerService.create(owner));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Owner> update(@PathVariable Long id, @RequestBody Owner owner) {
        owner.setId(id);
        return ResponseEntity.ok(ownerService.update(owner));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ownerService.delete(id);
        return ResponseEntity.ok().build();
    }
}