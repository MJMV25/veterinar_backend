package com.example.petservice.service;

import com.example.petservice.dto.PetDto;
import com.example.petservice.entity.PetStatus;
import com.example.petservice.entity.Species;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface PetService {
    PetDto createPet(PetDto petDto);
    PetDto updatePet(Long id, PetDto petDto);
    Optional<PetDto> getPetById(Long id);
    List<PetDto> getPetsByClientId(Long clientId);
    Page<PetDto> getPetsByClientId(Long clientId, Pageable pageable);
    Page<PetDto> getAllPets(Pageable pageable);
    Page<PetDto> getPetsByStatus(PetStatus status, Pageable pageable);
    Page<PetDto> getPetsBySpecies(Species species, Pageable pageable);
    Page<PetDto> searchPetsByName(String name, Pageable pageable);
    Page<PetDto> searchPetsByBreed(String breed, Pageable pageable);
    Optional<PetDto> getPetByMicrochip(String microchipNumber);
    void deletePet(Long id);
    void changePetStatus(Long id, PetStatus status);
    Long countPetsByClientAndStatus(Long clientId, PetStatus status);
}