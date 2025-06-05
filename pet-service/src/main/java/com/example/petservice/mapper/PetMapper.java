package com.example.petservice.mapper;

import com.example.petservice.dto.PetDto;
import com.example.petservice.entity.Pet;
import org.springframework.stereotype.Component;

@Component
public class PetMapper {

    public PetDto toDto(Pet pet) {
        if (pet == null) {
            return null;
        }

        PetDto dto = new PetDto();
        dto.setId(pet.getId());
        dto.setName(pet.getName());
        dto.setClientId(pet.getClientId());
        dto.setSpecies(pet.getSpecies());
        dto.setBreed(pet.getBreed());
        dto.setGender(pet.getGender());
        dto.setBirthDate(pet.getBirthDate());
        dto.setWeight(pet.getWeight());
        dto.setColor(pet.getColor());
        dto.setMicrochipNumber(pet.getMicrochipNumber());
        dto.setObservations(pet.getObservations());
        dto.setStatus(pet.getStatus());
        dto.setVaccinationCard(pet.getVaccinationCard());
        dto.setIsSterilized(pet.getIsSterilized());
        dto.setCreatedAt(pet.getCreatedAt());
        dto.setUpdatedAt(pet.getUpdatedAt());

        return dto;
    }

    public Pet toEntity(PetDto dto) {
        if (dto == null) {
            return null;
        }

        Pet pet = new Pet();
        pet.setId(dto.getId());
        pet.setName(dto.getName());
        pet.setClientId(dto.getClientId());
        pet.setSpecies(dto.getSpecies());
        pet.setBreed(dto.getBreed());
        pet.setGender(dto.getGender());
        pet.setBirthDate(dto.getBirthDate());
        pet.setWeight(dto.getWeight());
        pet.setColor(dto.getColor());
        pet.setMicrochipNumber(dto.getMicrochipNumber());
        pet.setObservations(dto.getObservations());
        pet.setStatus(dto.getStatus());
        pet.setVaccinationCard(dto.getVaccinationCard());
        pet.setIsSterilized(dto.getIsSterilized());

        return pet;
    }
}