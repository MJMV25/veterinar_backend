package com.example.medicalservice.repository;

import com.example.medicalservice.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByMedicalRecordId(Long medicalRecordId);

    @Query("SELECT p FROM Prescription p WHERE p.endDate BETWEEN :startDate AND :endDate")
    List<Prescription> findEndingPrescriptions(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT p FROM Prescription p JOIN p.medicalRecord mr WHERE mr.petId = :petId")
    List<Prescription> findByPetId(@Param("petId") Long petId);
}