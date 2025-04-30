package com.example.demo.Controller;

import com.example.demo.model.Owner;
import com.example.demo.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/owners")
public class OwnerController {

    @Autowired
    private OwnerService service;

    @GetMapping
    public List<Owner> getAll() {
        return service.getAllOwners();
    }

    @GetMapping("/{id}")
    public Owner getById(@PathVariable Long id) {
        return service.getOwner(id);
    }

    @PostMapping
    public Owner add(@RequestBody Owner owner) {
        return service.addOwner(owner);
    }

    @PutMapping("/{id}")
    public Owner update(@PathVariable Long id, @RequestBody Owner owner) {
        return service.updateOwner(id, owner);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteOwner(id);
    }
}