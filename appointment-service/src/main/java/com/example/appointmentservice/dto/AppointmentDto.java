package com.example.appointmentservice.dto;

import com.example.appointmentservice.entity.AppointmentStatus;
import com.example.appointmentservice.entity.AppointmentType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AppointmentDto {
    private Long id;

    @NotNull(message = "Pet ID is required")
    private Long petId;

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotNull(message = "Veterinarian ID is required")
    private Long veterinarianId;

    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    private LocalDateTime appointmentDate;

    private AppointmentType type;
    private AppointmentStatus status;
    private String reason;
    private String observations;
    private BigDecimal estimatedCost;
    private Integer durationMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Related information (from Feign calls)
    private PetInfo petInfo;
    private ClientInfo clientInfo;
    private VeterinarianInfo veterinarianInfo;

    // Constructors
    public AppointmentDto() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public Long getVeterinarianId() { return veterinarianId; }
    public void setVeterinarianId(Long veterinarianId) { this.veterinarianId = veterinarianId; }

    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDateTime appointmentDate) { this.appointmentDate = appointmentDate; }

    public AppointmentType getType() { return type; }
    public void setType(AppointmentType type) { this.type = type; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public PetInfo getPetInfo() { return petInfo; }
    public void setPetInfo(PetInfo petInfo) { this.petInfo = petInfo; }

    public ClientInfo getClientInfo() { return clientInfo; }
    public void setClientInfo(ClientInfo clientInfo) { this.clientInfo = clientInfo; }

    public VeterinarianInfo getVeterinarianInfo() { return veterinarianInfo; }
    public void setVeterinarianInfo(VeterinarianInfo veterinarianInfo) { this.veterinarianInfo = veterinarianInfo; }

    // Inner classes for related information
    public static class PetInfo {
        private Long id;
        private String name;
        private String species;
        private String breed;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getSpecies() { return species; }
        public void setSpecies(String species) { this.species = species; }

        public String getBreed() { return breed; }
        public void setBreed(String breed) { this.breed = breed; }
    }

    public static class ClientInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
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