package com.example.medicalservice.service;

import com.example.medicalservice.dto.MedicalRecordDto;
import com.example.medicalservice.dto.PrescriptionDto;
import com.example.medicalservice.dto.VaccinationDto;
import com.example.medicalservice.entity.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MedicalRecordService {
    MedicalRecordDto createMedicalRecord(MedicalRecordDto medicalRecordDto);
    MedicalRecordDto updateMedicalRecord(Long id, MedicalRecordDto medicalRecordDto);
    Optional<MedicalRecordDto> getMedicalRecordById(Long id);
    Page<MedicalRecordDto> getAllMedicalRecords(Pageable pageable);
    Page<MedicalRecordDto> getMedicalRecordsByPet(Long petId, Pageable pageable);
    Page<MedicalRecordDto> getMedicalRecordsByVeterinarian(Long veterinarianId, Pageable pageable);
    Page<MedicalRecordDto> getMedicalRecordsByType(RecordType type, Pageable pageable);
    Page<MedicalRecordDto> getMedicalRecordsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Page<MedicalRecordDto> searchMedicalRecords(String keyword, Pageable pageable);
    List<MedicalRecordDto> getLatestRecordsByPet(Long petId);
    List<MedicalRecordDto> getUpcomingVisits(LocalDateTime startDate, LocalDateTime endDate);
    void deleteMedicalRecord(Long id);

    // Prescription methods
    PrescriptionDto addPrescription(Long medicalRecordId, PrescriptionDto prescriptionDto);
    List<PrescriptionDto> getPrescriptionsByMedicalRecord(Long medicalRecordId);
    List<PrescriptionDto> getPrescriptionsByPet(Long petId);
    List<PrescriptionDto> getEndingPrescriptions(LocalDateTime startDate, LocalDateTime endDate);
    void deletePrescription(Long prescriptionId);

    // Vaccination methods
    VaccinationDto addVaccination(Long medicalRecordId, VaccinationDto vaccinationDto);
    List<VaccinationDto> getVaccinationsByMedicalRecord(Long medicalRecordId);
    List<VaccinationDto> getVaccinationsByPet(Long petId);
    List<VaccinationDto> getUpcomingVaccinations(LocalDateTime startDate, LocalDateTime endDate);
    void deleteVaccination(Long vaccinationId);
}
