package com.example.petservice.controller;

import com.example.petservice.dto.PetDto;
import com.example.petservice.entity.PetStatus;
import com.example.petservice.entity.Species;
import com.example.petservice.service.PetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/pets")
@CrossOrigin(origins = "*")
public class PetController {

    @Autowired
    private PetService petService;

    @PostMapping
    public ResponseEntity<PetDto> createPet(@Valid @RequestBody PetDto petDto) {
        PetDto createdPet = petService.createPet(petDto);
        return new ResponseEntity<>(createdPet, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDto> getPetById(@PathVariable Long id) {
        return petService.getPetById(id)
                .map(pet -> ResponseEntity.ok(pet))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<PetDto>> getPetsByClientId(@PathVariable Long clientId) {
        List<PetDto> pets = petService.getPetsByClientId(clientId);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/client/{clientId}/paginated")
    public ResponseEntity<Page<PetDto>> getPetsByClientIdPaginated(
            @PathVariable Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PetDto> pets = petService.getPetsByClientId(clientId, pageable);
        return ResponseEntity.ok(pets);
    }

    @GetMapping
    public ResponseEntity<Page<PetDto>> getAllPets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PetDto> pets = petService.getAllPets(pageable);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<PetDto>> getPetsByStatus(
            @PathVariable PetStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PetDto> pets = petService.getPetsByStatus(status, pageable);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/species/{species}")
    public ResponseEntity<Page<PetDto>> getPetsBySpecies(
            @PathVariable Species species,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PetDto> pets = petService.getPetsBySpecies(species, pageable);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/search/name")
    public ResponseEntity<Page<PetDto>> searchPetsByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PetDto> pets = petService.searchPetsByName(name, pageable);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/search/breed")
    public ResponseEntity<Page<PetDto>> searchPetsByBreed(
            @RequestParam String breed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PetDto> pets = petService.searchPetsByBreed(breed, pageable);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/microchip/{microchipNumber}")
    public ResponseEntity<PetDto> getPetByMicrochip(@PathVariable String microchipNumber) {
        return petService.getPetByMicrochip(microchipNumber)
                .map(pet -> ResponseEntity.ok(pet))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetDto> updatePet(@PathVariable Long id, @Valid @RequestBody PetDto petDto) {
        PetDto updatedPet = petService.updatePet(id, petDto);
        return ResponseEntity.ok(updatedPet);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changePetStatus(@PathVariable Long id, @RequestParam PetStatus status) {
        petService.changePetStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/client/{clientId}/count")
    public ResponseEntity<Long> countPetsByClientAndStatus(
            @PathVariable Long clientId,
            @RequestParam PetStatus status) {
        Long count = petService.countPetsByClientAndStatus(clientId, status);
        return ResponseEntity.ok(count);
    }
}