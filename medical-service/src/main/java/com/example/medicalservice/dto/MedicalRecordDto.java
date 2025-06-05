package com.example.medicalservice.dto;

import com.example.medicalservice.entity.RecordType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class MedicalRecordDto {
    private Long id;

    @NotNull(message = "Pet ID is required")
    private Long petId;

    @NotNull(message = "Veterinarian ID is required")
    private Long veterinarianId;

    private Long appointmentId;
    private LocalDateTime consultationDate;
    private String symptoms;
    private String diagnosis;
    private String treatment;
    private String observations;
    private String recommendations;
    private BigDecimal weight;
    private BigDecimal temperature;
    private Integer heartRate;
    private Integer respiratoryRate;
    private RecordType type;
    private LocalDateTime nextVisitDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<PrescriptionDto> prescriptions;
    private List<VaccinationDto> vaccinations;

    // Related information
    private PetInfo petInfo;
    private VeterinarianInfo veterinarianInfo;

    // Constructors
    public MedicalRecordDto() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }

    public Long getVeterinarianId() { return veterinarianId; }
    public void setVeterinarianId(Long veterinarianId) { this.veterinarianId = veterinarianId; }

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    public LocalDateTime getConsultationDate() { return consultationDate; }
    public void setConsultationDate(LocalDateTime consultationDate) { this.consultationDate = consultationDate; }

    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }

    public Integer getHeartRate() { return heartRate; }
    public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }

    public Integer getRespiratoryRate() { return respiratoryRate; }
    public void setRespiratoryRate(Integer respiratoryRate) { this.respiratoryRate = respiratoryRate; }

    public RecordType getType() { return type; }
    public void setType(RecordType type) { this.type = type; }

    public LocalDateTime getNextVisitDate() { return nextVisitDate; }
    public void setNextVisitDate(LocalDateTime nextVisitDate) { this.nextVisitDate = nextVisitDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<PrescriptionDto> getPrescriptions() { return prescriptions; }
    public void setPrescriptions(List<PrescriptionDto> prescriptions) { this.prescriptions = prescriptions; }

    public List<VaccinationDto> getVaccinations() { return vaccinations; }
    public void setVaccinations(List<VaccinationDto> vaccinations) { this.vaccinations = vaccinations; }

    public PetInfo getPetInfo() { return petInfo; }
    public void setPetInfo(PetInfo petInfo) { this.petInfo = petInfo; }

    public VeterinarianInfo getVeterinarianInfo() { return veterinarianInfo; }
    public void setVeterinarianInfo(VeterinarianInfo veterinarianInfo) { this.veterinarianInfo = veterinarianInfo; }

    // Inner classes for related information
    public static class PetInfo {
        private Long id;
        private String name;
        private String species;
        private String breed;
        private Long clientId;
        private String clientName;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getSpecies() { return species; }
        public void setSpecies(String species) { this.species = species; }

        public String getBreed() { return breed; }
        public void setBreed(String breed) { this.breed = breed; }

        public Long getClientId() { return clientId; }
        public void setClientId(Long clientId) { this.clientId = clientId; }

        public String getClientName() { return clientName; }
        public void setClientName(String clientName) { this.clientName = clientName; }
    }

    public static class VeterinarianInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}