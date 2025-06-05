package com.example.petservice.repository;

import com.example.petservice.entity.Pet;
import com.example.petservice.entity.PetStatus;
import com.example.petservice.entity.Species;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByClientId(Long clientId);
    Page<Pet> findByClientId(Long clientId, Pageable pageable);
    Page<Pet> findByStatus(PetStatus status, Pageable pageable);
    Page<Pet> findBySpecies(Species species, Pageable pageable);

    @Query("SELECT p FROM Pet p WHERE p.name LIKE %:name%")
    Page<Pet> findByNameContaining(@Param("name") String name, Pageable pageable);

    @Query("SELECT p FROM Pet p WHERE p.breed LIKE %:breed%")
    Page<Pet> findByBreedContaining(@Param("breed") String breed, Pageable pageable);

    Optional<Pet> findByMicrochipNumber(String microchipNumber);

    @Query("SELECT COUNT(p) FROM Pet p WHERE p.clientId = :clientId AND p.status = :status")
    Long countByClientIdAndStatus(@Param("clientId") Long clientId, @Param("status") PetStatus status);

    @Query("SELECT p FROM Pet p WHERE p.clientId = :clientId AND p.status = :status")
    List<Pet> findByClientIdAndStatus(@Param("clientId") Long clientId, @Param("status") PetStatus status);
}