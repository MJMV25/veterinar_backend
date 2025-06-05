package com.example.medicalservice.repository;


import com.example.medicalservice.entity.MedicalRecord;
import com.example.medicalservice.entity.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    Page<MedicalRecord> findByPetId(Long petId, Pageable pageable);
    Page<MedicalRecord> findByVeterinarianId(Long veterinarianId, Pageable pageable);
    Page<MedicalRecord> findByType(RecordType type, Pageable pageable);

    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.consultationDate BETWEEN :startDate AND :endDate")
    Page<MedicalRecord> findByConsultationDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.petId = :petId ORDER BY mr.consultationDate DESC")
    List<MedicalRecord> findLatestRecordsByPetId(@Param("petId") Long petId);

    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.diagnosis LIKE %:keyword% OR mr.symptoms LIKE %:keyword%")
    Page<MedicalRecord> findByDiagnosisOrSymptomsContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.nextVisitDate BETWEEN :startDate AND :endDate")
    List<MedicalRecord> findUpcomingVisits(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(mr) FROM MedicalRecord mr WHERE mr.veterinarianId = :veterinarianId AND mr.consultationDate BETWEEN :startDate AND :endDate")
    Long countConsultationsByVeterinarianAndDateRange(
            @Param("veterinarianId") Long veterinarianId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}