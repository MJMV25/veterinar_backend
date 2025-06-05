package com.example.petservice.dto;

import com.example.petservice.entity.Gender;
import com.example.petservice.entity.PetStatus;
import com.example.petservice.entity.Species;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PetDto {
    private Long id;

    @NotBlank(message = "Pet name is required")
    private String name;

    @NotNull(message = "Client ID is required")
    private Long clientId;

    private Species species;
    private String breed;
    private Gender gender;
    private LocalDate birthDate;

    @Positive(message = "Weight must be positive")
    private BigDecimal weight;

    private String color;
    private String microchipNumber;
    private String observations;
    private PetStatus status;
    private String vaccinationCard;
    private Boolean isSterilized;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Client information (from Feign call)
    private ClientInfo clientInfo;

    // Constructors
    public PetDto() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public Species getSpecies() { return species; }
    public void setSpecies(Species species) { this.species = species; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getMicrochipNumber() { return microchipNumber; }
    public void setMicrochipNumber(String microchipNumber) { this.microchipNumber = microchipNumber; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public PetStatus getStatus() { return status; }
    public void setStatus(PetStatus status) { this.status = status; }

    public String getVaccinationCard() { return vaccinationCard; }
    public void setVaccinationCard(String vaccinationCard) { this.vaccinationCard = vaccinationCard; }

    public Boolean getIsSterilized() { return isSterilized; }
    public void setIsSterilized(Boolean isSterilized) { this.isSterilized = isSterilized; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public ClientInfo getClientInfo() { return clientInfo; }
    public void setClientInfo(ClientInfo clientInfo) { this.clientInfo = clientInfo; }

    // Inner class for client information
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
}
