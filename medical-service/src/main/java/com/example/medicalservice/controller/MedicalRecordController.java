package com.example.medicalservice.controller;

import com.example.medicalservice.dto.MedicalRecordDto;
import com.example.medicalservice.dto.PrescriptionDto;
import com.example.medicalservice.dto.VaccinationDto;
import com.example.medicalservice.entity.RecordType;
import com.example.medicalservice.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/medical")
@CrossOrigin(origins = "*")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    // Medical Record endpoints
    @PostMapping("/records")
    public ResponseEntity<MedicalRecordDto> createMedicalRecord(@Valid @RequestBody MedicalRecordDto medicalRecordDto) {
        MedicalRecordDto createdRecord = medicalRecordService.createMedicalRecord(medicalRecordDto);
        return new ResponseEntity<>(createdRecord, HttpStatus.CREATED);
    }

    @GetMapping("/records/{id}")
    public ResponseEntity<MedicalRecordDto> getMedicalRecordById(@PathVariable Long id) {
        return medicalRecordService.getMedicalRecordById(id)
                .map(record -> ResponseEntity.ok(record))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/records")
    public ResponseEntity<Page<MedicalRecordDto>> getAllMedicalRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "consultationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<MedicalRecordDto> records = medicalRecordService.getAllMedicalRecords(pageable);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/records/pet/{petId}")
    public ResponseEntity<Page<MedicalRecordDto>> getMedicalRecordsByPet(
            @PathVariable Long petId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("consultationDate").descending());
        Page<MedicalRecordDto> records = medicalRecordService.getMedicalRecordsByPet(petId, pageable);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/records/pet/{petId}/latest")
    public ResponseEntity<List<MedicalRecordDto>> getLatestRecordsByPet(@PathVariable Long petId) {
        List<MedicalRecordDto> records = medicalRecordService.getLatestRecordsByPet(petId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/records/veterinarian/{veterinarianId}")
    public ResponseEntity<Page<MedicalRecordDto>> getMedicalRecordsByVeterinarian(
            @PathVariable Long veterinarianId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("consultationDate").descending());
        Page<MedicalRecordDto> records = medicalRecordService.getMedicalRecordsByVeterinarian(veterinarianId, pageable);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/records/type/{type}")
    public ResponseEntity<Page<MedicalRecordDto>> getMedicalRecordsByType(
            @PathVariable RecordType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("consultationDate").descending());
        Page<MedicalRecordDto> records = medicalRecordService.getMedicalRecordsByType(type, pageable);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/records/date-range")
    public ResponseEntity<Page<MedicalRecordDto>> getMedicalRecordsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("consultationDate").ascending());
        Page<MedicalRecordDto> records = medicalRecordService.getMedicalRecordsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/records/search")
    public ResponseEntity<Page<MedicalRecordDto>> searchMedicalRecords(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("consultationDate").descending());
        Page<MedicalRecordDto> records = medicalRecordService.searchMedicalRecords(keyword, pageable);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/records/upcoming-visits")
    public ResponseEntity<List<MedicalRecordDto>> getUpcomingVisits(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<MedicalRecordDto> records = medicalRecordService.getUpcomingVisits(startDate, endDate);
        return ResponseEntity.ok(records);
    }

    @PutMapping("/records/{id}")
    public ResponseEntity<MedicalRecordDto> updateMedicalRecord(@PathVariable Long id, @Valid @RequestBody MedicalRecordDto medicalRecordDto) {
        MedicalRecordDto updatedRecord = medicalRecordService.updateMedicalRecord(id, medicalRecordDto);
        return ResponseEntity.ok(updatedRecord);
    }

    @DeleteMapping("/records/{id}")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable Long id) {
        medicalRecordService.deleteMedicalRecord(id);
        return ResponseEntity.noContent().build();
    }

    // Prescription endpoints
    @PostMapping("/records/{medicalRecordId}/prescriptions")
    public ResponseEntity<PrescriptionDto> addPrescription(
            @PathVariable Long medicalRecordId,
            @Valid @RequestBody PrescriptionDto prescriptionDto) {
        PrescriptionDto createdPrescription = medicalRecordService.addPrescription(medicalRecordId, prescriptionDto);
        return new ResponseEntity<>(createdPrescription, HttpStatus.CREATED);
    }

    @GetMapping("/records/{medicalRecordId}/prescriptions")
    public ResponseEntity<List<PrescriptionDto>> getPrescriptionsByMedicalRecord(@PathVariable Long medicalRecordId) {
        List<PrescriptionDto> prescriptions = medicalRecordService.getPrescriptionsByMedicalRecord(medicalRecordId);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/prescriptions/pet/{petId}")
    public ResponseEntity<List<PrescriptionDto>> getPrescriptionsByPet(@PathVariable Long petId) {
        List<PrescriptionDto> prescriptions = medicalRecordService.getPrescriptionsByPet(petId);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/prescriptions/ending")
    public ResponseEntity<List<PrescriptionDto>> getEndingPrescriptions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<PrescriptionDto> prescriptions = medicalRecordService.getEndingPrescriptions(startDate, endDate);
        return ResponseEntity.ok(prescriptions);
    }

    @DeleteMapping("/prescriptions/{prescriptionId}")
    public ResponseEntity<Void> deletePrescription(@PathVariable Long prescriptionId) {
        medicalRecordService.deletePrescription(prescriptionId);
        return ResponseEntity.noContent().build();
    }

    // Vaccination endpoints
    @PostMapping("/records/{medicalRecordId}/vaccinations")
    public ResponseEntity<VaccinationDto> addVaccination(
            @PathVariable Long medicalRecordId,
            @Valid @RequestBody VaccinationDto vaccinationDto) {
        VaccinationDto createdVaccination = medicalRecordService.addVaccination(medicalRecordId, vaccinationDto);
        return new ResponseEntity<>(createdVaccination, HttpStatus.CREATED);
    }

    @GetMapping("/records/{medicalRecordId}/vaccinations")
    public ResponseEntity<List<VaccinationDto>> getVaccinationsByMedicalRecord(@PathVariable Long medicalRecordId) {
        List<VaccinationDto> vaccinations = medicalRecordService.getVaccinationsByMedicalRecord(medicalRecordId);
        return ResponseEntity.ok(vaccinations);
    }

    @GetMapping("/vaccinations/pet/{petId}")
    public ResponseEntity<List<VaccinationDto>> getVaccinationsByPet(@PathVariable Long petId) {
        List<VaccinationDto> vaccinations = medicalRecordService.getVaccinationsByPet(petId);
        return ResponseEntity.ok(vaccinations);
    }

    @GetMapping("/vaccinations/upcoming")
    public ResponseEntity<List<VaccinationDto>> getUpcomingVaccinations(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<VaccinationDto> vaccinations = medicalRecordService.getUpcomingVaccinations(startDate, endDate);
        return ResponseEntity.ok(vaccinations);
    }

    @DeleteMapping("/vaccinations/{vaccinationId}")
    public ResponseEntity<Void> deleteVaccination(@PathVariable Long vaccinationId) {
        medicalRecordService.deleteVaccination(vaccinationId);
        return ResponseEntity.noContent().build();
    }
}
