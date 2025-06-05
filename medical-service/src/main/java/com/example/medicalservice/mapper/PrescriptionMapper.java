package com.example.medicalservice.mapper;

import com.example.medicalservice.dto.PrescriptionDto;
import com.example.medicalservice.entity.Prescription;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionMapper {

    public PrescriptionDto toDto(Prescription prescription) {
        if (prescription == null) {
            return null;
        }

        PrescriptionDto dto = new PrescriptionDto();
        dto.setId(prescription.getId());
        dto.setMedicalRecordId(prescription.getMedicalRecord() != null ? prescription.getMedicalRecord().getId() : null);
        dto.setMedicineName(prescription.getMedicineName());
        dto.setDosage(prescription.getDosage());
        dto.setFrequency(prescription.getFrequency());
        dto.setDuration(prescription.getDuration());
        dto.setInstructions(prescription.getInstructions());
        dto.setStartDate(prescription.getStartDate());
        dto.setEndDate(prescription.getEndDate());
        dto.setCreatedAt(prescription.getCreatedAt());

        return dto;
    }

    public Prescription toEntity(PrescriptionDto dto) {
        if (dto == null) {
            return null;
        }

        Prescription prescription = new Prescription();
        prescription.setId(dto.getId());
        prescription.setMedicineName(dto.getMedicineName());
        prescription.setDosage(dto.getDosage());
        prescription.setFrequency(dto.getFrequency());
        prescription.setDuration(dto.getDuration());
        prescription.setInstructions(dto.getInstructions());
        prescription.setStartDate(dto.getStartDate());
        prescription.setEndDate(dto.getEndDate());

        return prescription;
    }
}
