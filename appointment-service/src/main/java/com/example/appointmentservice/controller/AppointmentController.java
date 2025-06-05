package com.example.appointmentservice.controller;

import com.example.appointmentservice.dto.AppointmentDto;
import com.example.appointmentservice.entity.AppointmentStatus;
import com.example.appointmentservice.entity.AppointmentType;
import com.example.appointmentservice.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentDto> createAppointment(@Valid @RequestBody AppointmentDto appointmentDto) {
        AppointmentDto createdAppointment = appointmentService.createAppointment(appointmentDto);
        return new ResponseEntity<>(createdAppointment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDto> getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id)
                .map(appointment -> ResponseEntity.ok(appointment))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<AppointmentDto>> getAllAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AppointmentDto> appointments = appointmentService.getAllAppointments(pageable);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<Page<AppointmentDto>> getAppointmentsByClient(
            @PathVariable Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate").descending());
        Page<AppointmentDto> appointments = appointmentService.getAppointmentsByClient(clientId, pageable);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<Page<AppointmentDto>> getAppointmentsByPet(
            @PathVariable Long petId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate").descending());
        Page<AppointmentDto> appointments = appointmentService.getAppointmentsByPet(petId, pageable);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/veterinarian/{veterinarianId}")
    public ResponseEntity<Page<AppointmentDto>> getAppointmentsByVeterinarian(
            @PathVariable Long veterinarianId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate").ascending());
        Page<AppointmentDto> appointments = appointmentService.getAppointmentsByVeterinarian(veterinarianId, pageable);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/veterinarian/{veterinarianId}/schedule")
    public ResponseEntity<List<AppointmentDto>> getVeterinarianSchedule(
            @PathVariable Long veterinarianId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<AppointmentDto> schedule = appointmentService.getVeterinarianSchedule(veterinarianId, startDate, endDate);
        return ResponseEntity.ok(schedule);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<AppointmentDto>> getAppointmentsByStatus(
            @PathVariable AppointmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate").ascending());
        Page<AppointmentDto> appointments = appointmentService.getAppointmentsByStatus(status, pageable);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<Page<AppointmentDto>> getAppointmentsByType(
            @PathVariable AppointmentType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate").ascending());
        Page<AppointmentDto> appointments = appointmentService.getAppointmentsByType(type, pageable);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/date-range")
    public ResponseEntity<Page<AppointmentDto>> getAppointmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate").ascending());
        Page<AppointmentDto> appointments = appointmentService.getAppointmentsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/availability/{veterinarianId}")
    public ResponseEntity<Boolean> checkVeterinarianAvailability(
            @PathVariable Long veterinarianId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDate,
            @RequestParam(defaultValue = "30") Integer durationMinutes) {

        boolean isAvailable = appointmentService.isVeterinarianAvailable(veterinarianId, appointmentDate, durationMinutes);
        return ResponseEntity.ok(isAvailable);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<AppointmentDto>> getOverdueAppointments() {
        List<AppointmentDto> overdueAppointments = appointmentService.getOverdueAppointments();
        return ResponseEntity.ok(overdueAppointments);
    }

    @GetMapping("/reminder")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsForReminder(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<AppointmentDto> appointments = appointmentService.getAppointmentsForReminder(startDate, endDate);
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDto> updateAppointment(@PathVariable Long id, @Valid @RequestBody AppointmentDto appointmentDto) {
        AppointmentDto updatedAppointment = appointmentService.updateAppointment(id, appointmentDto);
        return ResponseEntity.ok(updatedAppointment);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeAppointmentStatus(@PathVariable Long id, @RequestParam AppointmentStatus status) {
        appointmentService.changeAppointmentStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
