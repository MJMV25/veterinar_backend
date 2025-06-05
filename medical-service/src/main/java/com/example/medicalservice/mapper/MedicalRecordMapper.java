package com.example.medicalservice.mapper;

import com.example.medicalservice.dto.MedicalRecordDto;
import com.example.medicalservice.entity.MedicalRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class MedicalRecordMapper {

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Autowired
    private VaccinationMapper vaccinationMapper;

    public MedicalRecordDto toDto(MedicalRecord medicalRecord) {
        if (medicalRecord == null) {
            return null;
        }

        MedicalRecordDto dto = new MedicalRecordDto();
        dto.setId(medicalRecord.getId());
        dto.setPetId(medicalRecord.getPetId());
        dto.setVeterinarianId(medicalRecord.getVeterinarianId());
        dto.setAppointmentId(medicalRecord.getAppointmentId());
        dto.setConsultationDate(medicalRecord.getConsultationDate());
        dto.setSymptoms(medicalRecord.getSymptoms());
        dto.setDiagnosis(medicalRecord.getDiagnosis());
        dto.setTreatment(medicalRecord.getTreatment());
        dto.setObservations(medicalRecord.getObservations());
        dto.setRecommendations(medicalRecord.getRecommendations());
        dto.setWeight(medicalRecord.getWeight());
        dto.setTemperature(medicalRecord.getTemperature());
        dto.setHeartRate(medicalRecord.getHeartRate());
        dto.setRespiratoryRate(medicalRecord.getRespiratoryRate());
        dto.setType(medicalRecord.getType());
        dto.setNextVisitDate(medicalRecord.getNextVisitDate());
        dto.setCreatedAt(medicalRecord.getCreatedAt());
        dto.setUpdatedAt(medicalRecord.getUpdatedAt());

        // Map prescriptions and vaccinations
        if (medicalRecord.getPrescriptions() != null) {
            dto.setPrescriptions(medicalRecord.getPrescriptions().stream()
                    .map(prescriptionMapper::toDto)
                    .collect(Collectors.toList()));
        }

        if (medicalRecord.getVaccinations() != null) {
            dto.setVaccinations(medicalRecord.getVaccinations().stream()
                    .map(vaccinationMapper::toDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public MedicalRecord toEntity(MedicalRecordDto dto) {
        if (dto == null) {
            return null;
        }

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setId(dto.getId());
        medicalRecord.setPetId(dto.getPetId());
        medicalRecord.setVeterinarianId(dto.getVeterinarianId());
        medicalRecord.setAppointmentId(dto.getAppointmentId());
        medicalRecord.setConsultationDate(dto.getConsultationDate());
        medicalRecord.setSymptoms(dto.getSymptoms());
        medicalRecord.setDiagnosis(dto.getDiagnosis());
        medicalRecord.setTreatment(dto.getTreatment());
        medicalRecord.setObservations(dto.getObservations());
        medicalRecord.setRecommendations(dto.getRecommendations());
        medicalRecord.setWeight(dto.getWeight());
        medicalRecord.setTemperature(dto.getTemperature());
        medicalRecord.setHeartRate(dto.getHeartRate());
        medicalRecord.setRespiratoryRate(dto.getRespiratoryRate());
        medicalRecord.setType(dto.getType());
        medicalRecord.setNextVisitDate(dto.getNextVisitDate());

        return medicalRecord;
    }
}