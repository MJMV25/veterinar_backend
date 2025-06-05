package com.example.medicalservice.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class VaccinationDto {
    private Long id;
    private Long medicalRecordId;

    @NotBlank(message = "Vaccine name is required")
    private String vaccineName;

    private String vaccineBrand;
    private String batchNumber;
    private LocalDateTime applicationDate;
    private LocalDateTime nextDoseDate;
    private String observations;
    private LocalDateTime createdAt;

    // Constructors
    public VaccinationDto() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMedicalRecordId() { return medicalRecordId; }
    public void setMedicalRecordId(Long medicalRecordId) { this.medicalRecordId = medicalRecordId; }

    public String getVaccineName() { return vaccineName; }
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }

    public String getVaccineBrand() { return vaccineBrand; }
    public void setVaccineBrand(String vaccineBrand) { this.vaccineBrand = vaccineBrand; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public LocalDateTime getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDateTime applicationDate) { this.applicationDate = applicationDate; }

    public LocalDateTime getNextDoseDate() { return nextDoseDate; }
    public void setNextDoseDate(LocalDateTime nextDoseDate) { this.nextDoseDate = nextDoseDate; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}