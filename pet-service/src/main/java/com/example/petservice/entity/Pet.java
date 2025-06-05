package com.example.petservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pets")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Pet name is required")
    private String name;

    @NotNull(message = "Client ID is required")
    @Column(name = "client_id")
    private Long clientId;

    @Enumerated(EnumType.STRING)
    private Species species;

    private String breed;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Positive(message = "Weight must be positive")
    private BigDecimal weight;

    private String color;

    @Column(name = "microchip_number")
    private String microchipNumber;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Enumerated(EnumType.STRING)
    private PetStatus status = PetStatus.ACTIVE;

    @Column(name = "vaccination_card")
    private String vaccinationCard;

    @Column(name = "is_sterilized")
    private Boolean isSterilized = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Pet() {}

    public Pet(String name, Long clientId, Species species, String breed) {
        this.name = name;
        this.clientId = clientId;
        this.species = species;
        this.breed = breed;
    }

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
}