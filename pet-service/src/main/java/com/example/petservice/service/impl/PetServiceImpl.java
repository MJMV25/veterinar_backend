package com.example.petservice.service.impl;

import com.example.petservice.client.ClientServiceClient;
import com.example.petservice.dto.PetDto;
import com.example.petservice.entity.Pet;
import com.example.petservice.entity.PetStatus;
import com.example.petservice.entity.Species;
import com.example.petservice.exception.ResourceNotFoundException;
import com.example.petservice.mapper.PetMapper;
import com.example.petservice.repository.PetRepository;
import com.example.petservice.service.PetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PetServiceImpl implements PetService {

    private static final Logger logger = LoggerFactory.getLogger(PetServiceImpl.class);

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private ClientServiceClient clientServiceClient;

    @Override
    public PetDto createPet(PetDto petDto) {
        // Verify client exists
        try {
            ResponseEntity<PetDto.ClientInfo> clientResponse = clientServiceClient.getClientById(petDto.getClientId());
            if (!clientResponse.getStatusCode().is2xxSuccessful() || clientResponse.getBody() == null) {
                throw new ResourceNotFoundException("Client not found with id: " + petDto.getClientId());
            }
        } catch (Exception e) {
            logger.error("Error verifying client existence: ", e);
            throw new ResourceNotFoundException("Client not found with id: " + petDto.getClientId());
        }

        Pet pet = petMapper.toEntity(petDto);
        pet.setStatus(PetStatus.ACTIVE);

        Pet savedPet = petRepository.save(pet);
        PetDto result = petMapper.toDto(savedPet);

        // Enrich with client information
        enrichWithClientInfo(result);

        return result;
    }

    @Override
    public PetDto updatePet(Long id, PetDto petDto) {
        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));

        // Update fields
        existingPet.setName(petDto.getName());
        existingPet.setSpecies(petDto.getSpecies());
        existingPet.setBreed(petDto.getBreed());
        existingPet.setGender(petDto.getGender());
        existingPet.setBirthDate(petDto.getBirthDate());
        existingPet.setWeight(petDto.getWeight());
        existingPet.setColor(petDto.getColor());
        existingPet.setMicrochipNumber(petDto.getMicrochipNumber());
        existingPet.setObservations(petDto.getObservations());
        existingPet.setVaccinationCard(petDto.getVaccinationCard());
        existingPet.setIsSterilized(petDto.getIsSterilized());

        Pet updatedPet = petRepository.save(existingPet);
        PetDto result = petMapper.toDto(updatedPet);

        // Enrich with client information
        enrichWithClientInfo(result);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PetDto> getPetById(Long id) {
        Optional<Pet> petOpt = petRepository.findById(id);
        if (petOpt.isPresent()) {
            PetDto petDto = petMapper.toDto(petOpt.get());
            enrichWithClientInfo(petDto);
            return Optional.of(petDto);
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PetDto> getPetsByClientId(Long clientId) {
        List<Pet> pets = petRepository.findByClientId(clientId);
        return pets.stream()
                .map(petMapper::toDto)
                .map(this::enrichWithClientInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetDto> getPetsByClientId(Long clientId, Pageable pageable) {
        return petRepository.findByClientId(clientId, pageable)
                .map(petMapper::toDto)
                .map(this::enrichWithClientInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetDto> getAllPets(Pageable pageable) {
        return petRepository.findAll(pageable)
                .map(petMapper::toDto)
                .map(this::enrichWithClientInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetDto> getPetsByStatus(PetStatus status, Pageable pageable) {
        return petRepository.findByStatus(status, pageable)
                .map(petMapper::toDto)
                .map(this::enrichWithClientInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetDto> getPetsBySpecies(Species species, Pageable pageable) {
        return petRepository.findBySpecies(species, pageable)
                .map(petMapper::toDto)
                .map(this::enrichWithClientInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetDto> searchPetsByName(String name, Pageable pageable) {
        return petRepository.findByNameContaining(name, pageable)
                .map(petMapper::toDto)
                .map(this::enrichWithClientInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetDto> searchPetsByBreed(String breed, Pageable pageable) {
        return petRepository.findByBreedContaining(breed, pageable)
                .map(petMapper::toDto)
                .map(this::enrichWithClientInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PetDto> getPetByMicrochip(String microchipNumber) {
        Optional<Pet> petOpt = petRepository.findByMicrochipNumber(microchipNumber);
        if (petOpt.isPresent()) {
            PetDto petDto = petMapper.toDto(petOpt.get());
            enrichWithClientInfo(petDto);
            return Optional.of(petDto);
        }
        return Optional.empty();
    }

    @Override
    public void deletePet(Long id) {
        if (!petRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pet not found with id: " + id);
        }
        petRepository.deleteById(id);
    }

    @Override
    public void changePetStatus(Long id, PetStatus status) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));

        pet.setStatus(status);
        petRepository.save(pet);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countPetsByClientAndStatus(Long clientId, PetStatus status) {
        return petRepository.countByClientIdAndStatus(clientId, status);
    }

    private PetDto enrichWithClientInfo(PetDto petDto) {
        try {
            ResponseEntity<PetDto.ClientInfo> clientResponse = clientServiceClient.getClientById(petDto.getClientId());
            if (clientResponse.getStatusCode().is2xxSuccessful() && clientResponse.getBody() != null) {
                petDto.setClientInfo(clientResponse.getBody());
            }
        } catch (Exception e) {
            logger.warn("Could not fetch client information for client ID: " + petDto.getClientId(), e);
            // Create a fallback client info
            PetDto.ClientInfo fallbackInfo = new PetDto.ClientInfo();
            fallbackInfo.setId(petDto.getClientId());
            fallbackInfo.setFirstName("Unknown");
            fallbackInfo.setLastName("Client");
            petDto.setClientInfo(fallbackInfo);
        }
        return petDto;
    }
}