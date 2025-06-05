package com.example.medicalservice.mapper;

import com.example.medicalservice.dto.VaccinationDto;
import com.example.medicalservice.entity.Vaccination;
import org.springframework.stereotype.Component;

@Component
public class VaccinationMapper {

    public VaccinationDto toDto(Vaccination vaccination) {
        if (vaccination == null) {
            return null;
        }

        VaccinationDto dto = new VaccinationDto();
        dto.setId(vaccination.getId());
        dto.setMedicalRecordId(vaccination.getMedicalRecord() != null ? vaccination.getMedicalRecord().getId() : null);
        dto.setVaccineName(vaccination.getVaccineName());
        dto.setVaccineBrand(vaccination.getVaccineBrand());
        dto.setBatchNumber(vaccination.getBatchNumber());
        dto.setApplicationDate(vaccination.getApplicationDate());
        dto.setNextDoseDate(vaccination.getNextDoseDate());
        dto.setObservations(vaccination.getObservations());
        dto.setCreatedAt(vaccination.getCreatedAt());

        return dto;
    }

    public Vaccination toEntity(VaccinationDto dto) {
        if (dto == null) {
            return null;
        }

        Vaccination vaccination = new Vaccination();
        vaccination.setId(dto.getId());
        vaccination.setVaccineName(dto.getVaccineName());
        vaccination.setVaccineBrand(dto.getVaccineBrand());
        vaccination.setBatchNumber(dto.getBatchNumber());
        vaccination.setApplicationDate(dto.getApplicationDate());
        vaccination.setNextDoseDate(dto.getNextDoseDate());
        vaccination.setObservations(dto.getObservations());

        return vaccination;
    }
}