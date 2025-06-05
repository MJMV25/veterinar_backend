package com.example.appointmentservice.service;

import com.example.appointmentservice.dto.AppointmentDto;
import com.example.appointmentservice.entity.AppointmentStatus;
import com.example.appointmentservice.entity.AppointmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    AppointmentDto createAppointment(AppointmentDto appointmentDto);
    AppointmentDto updateAppointment(Long id, AppointmentDto appointmentDto);
    Optional<AppointmentDto> getAppointmentById(Long id);
    Page<AppointmentDto> getAllAppointments(Pageable pageable);
    Page<AppointmentDto> getAppointmentsByClient(Long clientId, Pageable pageable);
    Page<AppointmentDto> getAppointmentsByPet(Long petId, Pageable pageable);
    Page<AppointmentDto> getAppointmentsByVeterinarian(Long veterinarianId, Pageable pageable);
    Page<AppointmentDto> getAppointmentsByStatus(AppointmentStatus status, Pageable pageable);
    Page<AppointmentDto> getAppointmentsByType(AppointmentType type, Pageable pageable);
    Page<AppointmentDto> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    List<AppointmentDto> getVeterinarianSchedule(Long veterinarianId, LocalDateTime startDate, LocalDateTime endDate);
    void deleteAppointment(Long id);
    void changeAppointmentStatus(Long id, AppointmentStatus status);
    boolean isVeterinarianAvailable(Long veterinarianId, LocalDateTime appointmentDate, Integer durationMinutes);
    List<AppointmentDto> getOverdueAppointments();
    List<AppointmentDto> getAppointmentsForReminder(LocalDateTime startDate, LocalDateTime endDate);
}