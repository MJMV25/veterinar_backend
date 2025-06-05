package com.example.medicalservice.repository;

import com.example.medicalservice.entity.Vaccination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VaccinationRepository extends JpaRepository<Vaccination, Long> {
    List<Vaccination> findByMedicalRecordId(Long medicalRecordId);

    @Query("SELECT v FROM Vaccination v WHERE v.nextDoseDate BETWEEN :startDate AND :endDate")
    List<Vaccination> findUpcomingVaccinations(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT v FROM Vaccination v JOIN v.medicalRecord mr WHERE mr.petId = :petId")
    List<Vaccination> findByPetId(@Param("petId") Long petId);

    @Query("SELECT v FROM Vaccination v WHERE v.vaccineName = :vaccineName")
    List<Vaccination> findByVaccineName(@Param("vaccineName") String vaccineName);
}