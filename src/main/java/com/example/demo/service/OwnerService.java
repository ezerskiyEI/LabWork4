package com.example.demo.service;

import com.example.demo.model.CarInfo;
import com.example.demo.model.Owner;
import com.example.demo.repository.CarInfoRepository;
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
    private CarInfoRepository carInfoRepository;

    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }

    public Optional<Owner> getOwnerById(Long id) {
        return ownerRepository.findById(id);
    }

    public Owner addOwner(Owner owner) {
        return ownerRepository.save(owner);
    }

    public Optional<Owner> updateOwner(Long id, Owner ownerDetails) {
        return ownerRepository.findById(id).map(existingOwner -> {
            existingOwner.setName(ownerDetails.getName());
            return ownerRepository.save(existingOwner);
        });
    }

    public boolean deleteOwner(Long id) {
        if (ownerRepository.existsById(id)) {
            ownerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Owner> addCarToOwner(Long ownerId, String vin) {
        Optional<Owner> ownerOptional = ownerRepository.findById(ownerId);
        Optional<CarInfo> carOptional = carInfoRepository.findByVin(vin);

        if (ownerOptional.isPresent() && carOptional.isPresent()) {
            Owner owner = ownerOptional.get();
            owner.getCars().add(carOptional.get());
            return Optional.of(ownerRepository.save(owner));
        }
        return Optional.empty();
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 03f5d34f8d291d57e2ae16c0d816222fffb062d1
