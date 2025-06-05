package com.example.medicalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "vaccinations")
public class Vaccination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id")
    private MedicalRecord medicalRecord;

    @NotBlank(message = "Vaccine name is required")
    @Column(name = "vaccine_name")
    private String vaccineName;

    @Column(name = "vaccine_brand")
    private String vaccineBrand;

    @Column(name = "batch_number")
    private String batchNumber;

    @Column(name = "application_date")
    private LocalDateTime applicationDate;

    @Column(name = "next_dose_date")
    private LocalDateTime nextDoseDate;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (applicationDate == null) {
            applicationDate = LocalDateTime.now();
        }
    }

    // Constructors
    public Vaccination() {}

    public Vaccination(String vaccineName, String vaccineBrand, LocalDateTime nextDoseDate) {
        this.vaccineName = vaccineName;
        this.vaccineBrand = vaccineBrand;
        this.nextDoseDate = nextDoseDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MedicalRecord getMedicalRecord() { return medicalRecord; }
    public void setMedicalRecord(MedicalRecord medicalRecord) { this.medicalRecord = medicalRecord; }

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