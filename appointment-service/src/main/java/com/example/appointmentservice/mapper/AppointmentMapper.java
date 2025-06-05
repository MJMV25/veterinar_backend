package com.example.appointmentservice.mapper;

import com.example.appointmentservice.dto.AppointmentDto;
import com.example.appointmentservice.entity.Appointment;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentDto toDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        AppointmentDto dto = new AppointmentDto();
        dto.setId(appointment.getId());
        dto.setPetId(appointment.getPetId());
        dto.setClientId(appointment.getClientId());
        dto.setVeterinarianId(appointment.getVeterinarianId());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setType(appointment.getType());
        dto.setStatus(appointment.getStatus());
        dto.setReason(appointment.getReason());
        dto.setObservations(appointment.getObservations());
        dto.setEstimatedCost(appointment.getEstimatedCost());
        dto.setDurationMinutes(appointment.getDurationMinutes());
        dto.setCreatedAt(appointment.getCreatedAt());
        dto.setUpdatedAt(appointment.getUpdatedAt());

        return dto;
    }

    public Appointment toEntity(AppointmentDto dto) {
        if (dto == null) {
            return null;
        }

        Appointment appointment = new Appointment();
        appointment.setId(dto.getId());
        appointment.setPetId(dto.getPetId());
        appointment.setClientId(dto.getClientId());
        appointment.setVeterinarianId(dto.getVeterinarianId());
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setType(dto.getType());
        appointment.setStatus(dto.getStatus());
        appointment.setReason(dto.getReason());
        appointment.setObservations(dto.getObservations());
        appointment.setEstimatedCost(dto.getEstimatedCost());
        appointment.setDurationMinutes(dto.getDurationMinutes());

        return appointment;
    }
}