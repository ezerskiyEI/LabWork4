package com.example.demo.Controller;

import com.example.demo.model.Owner;
import com.example.demo.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/owners")
public class OwnerController {
    private final OwnerService ownerService;

    @Autowired
    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping
    public List<Owner> getAll() {
        return ownerService.getAllOwners();
    }

    @GetMapping("/{id}")
    public Owner getById(@PathVariable Long id) {
        return ownerService.getOwner(id);
    }

    @PostMapping
    public Owner add(@RequestBody Owner owner) {
        return ownerService.addOwner(owner);
    }

    @PutMapping("/{id}")
    public Owner update(@PathVariable Long id, @RequestBody Owner owner) {
        return ownerService.updateOwner(id, owner);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ownerService.deleteOwner(id);
    }
}