package com.example.appointmentservice.service.impl;

import com.example.appointmentservice.client.ClientServiceClient;
import com.example.appointmentservice.client.PetServiceClient;
import com.example.appointmentservice.client.UserServiceClient;
import com.example.appointmentservice.dto.AppointmentDto;
import com.example.appointmentservice.entity.Appointment;
import com.example.appointmentservice.entity.AppointmentStatus;
import com.example.appointmentservice.entity.AppointmentType;
import com.example.appointmentservice.exception.ResourceNotFoundException;
import com.example.appointmentservice.exception.AppointmentConflictException;
import com.example.appointmentservice.mapper.AppointmentMapper;
import com.example.appointmentservice.repository.AppointmentRepository;
import com.example.appointmentservice.service.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private PetServiceClient petServiceClient;

    @Autowired
    private ClientServiceClient clientServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;

    @Override
    public AppointmentDto createAppointment(AppointmentDto appointmentDto) {
        // Verify availability
        if (!isVeterinarianAvailable(appointmentDto.getVeterinarianId(),
                appointmentDto.getAppointmentDate(), appointmentDto.getDurationMinutes())) {
            throw new AppointmentConflictException("Veterinarian is not available at the requested time");
        }

        // Verify that pet, client and veterinarian exist
        verifyRelatedEntities(appointmentDto);

        Appointment appointment = appointmentMapper.toEntity(appointmentDto);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        AppointmentDto result = appointmentMapper.toDto(savedAppointment);

        // Enrich with related information
        enrichWithRelatedInfo(result);

        return result;
    }

    @Override
    public AppointmentDto updateAppointment(Long id, AppointmentDto appointmentDto) {
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        // If date or veterinarian is being changed, check availability
        if (!existingAppointment.getAppointmentDate().equals(appointmentDto.getAppointmentDate()) ||
                !existingAppointment.getVeterinarianId().equals(appointmentDto.getVeterinarianId())) {

            if (!isVeterinarianAvailable(appointmentDto.getVeterinarianId(),
                    appointmentDto.getAppointmentDate(), appointmentDto.getDurationMinutes())) {
                throw new AppointmentConflictException("Veterinarian is not available at the requested time");
            }
        }

        // Update fields
        existingAppointment.setAppointmentDate(appointmentDto.getAppointmentDate());
        existingAppointment.setType(appointmentDto.getType());
        existingAppointment.setReason(appointmentDto.getReason());
        existingAppointment.setObservations(appointmentDto.getObservations());
        existingAppointment.setEstimatedCost(appointmentDto.getEstimatedCost());
        existingAppointment.setDurationMinutes(appointmentDto.getDurationMinutes());

        Appointment updatedAppointment = appointmentRepository.save(existingAppointment);
        AppointmentDto result = appointmentMapper.toDto(updatedAppointment);

        // Enrich with related information
        enrichWithRelatedInfo(result);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppointmentDto> getAppointmentById(Long id) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isPresent()) {
            AppointmentDto appointmentDto = appointmentMapper.toDto(appointmentOpt.get());
            enrichWithRelatedInfo(appointmentDto);
            return Optional.of(appointmentDto);
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentDto> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable)
                .map(appointmentMapper::toDto)
                .map(this::enrichWithRelatedInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentDto> getAppointmentsByClient(Long clientId, Pageable pageable) {
        return appointmentRepository.findByClientId(clientId, pageable)
                .map(appointmentMapper::toDto)
                .map(this::enrichWithRelatedInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentDto> getAppointmentsByPet(Long petId, Pageable pageable) {
        return appointmentRepository.findByPetId(petId, pageable)
                .map(appointmentMapper::toDto)
                .map(this::enrichWithRelatedInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentDto> getAppointmentsByVeterinarian(Long veterinarianId, Pageable pageable) {
        return appointmentRepository.findByVeterinarianId(veterinarianId, pageable)
                .map(appointmentMapper::toDto)
                .map(this::enrichWithRelatedInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentDto> getAppointmentsByStatus(AppointmentStatus status, Pageable pageable) {
        return appointmentRepository.findByStatus(status, pageable)
                .map(appointmentMapper::toDto)
                .map(this::enrichWithRelatedInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentDto> getAppointmentsByType(AppointmentType type, Pageable pageable) {
        return appointmentRepository.findByType(type, pageable)
                .map(appointmentMapper::toDto)
                .map(this::enrichWithRelatedInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentDto> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return appointmentRepository.findByAppointmentDateBetween(startDate, endDate, pageable)
                .map(appointmentMapper::toDto)
                .map(this::enrichWithRelatedInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDto> getVeterinarianSchedule(Long veterinarianId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Appointment> appointments = appointmentRepository.findByVeterinarianIdAndAppointmentDateBetween(
                veterinarianId, startDate, endDate);

        return appointments.stream()
                .map(appointmentMapper::toDto)
                .map(this::enrichWithRelatedInfo)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Appointment not found with id: " + id);
        }
        appointmentRepository.deleteById(id);
    }

    @Override
    public void changeAppointmentStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        appointment.setStatus(status);
        appointmentRepository.save(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVeterinarianAvailable(Long veterinarianId, LocalDateTime appointmentDate, Integer durationMinutes) {
        if (durationMinutes == null) {
            durationMinutes = 30; // Default duration
        }

        LocalDateTime endTime = appointmentDate.plusMinutes(durationMinutes);

        List<Appointment> conflictingAppointments = appointmentRepository
                .findByVeterinarianIdAndAppointmentDateBetween(
                        veterinarianId,
                        appointmentDate.minusMinutes(30),
                        endTime.plusMinutes(30)
                );

        return conflictingAppointments.stream()
                .noneMatch(appointment ->
                        appointment.getStatus() == AppointmentStatus.SCHEDULED ||
                                appointment.getStatus() == AppointmentStatus.CONFIRMED ||
                                appointment.getStatus() == AppointmentStatus.IN_PROGRESS
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDto> getOverdueAppointments() {
        List<Appointment> overdueAppointments = appointmentRepository.findOverdueAppointments(LocalDateTime.now());

        return overdueAppointments.stream()
                .map(appointmentMapper::toDto)
                .map(this::enrichWithRelatedInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDto> getAppointmentsForReminder(LocalDateTime startDate, LocalDateTime endDate) {
        List<AppointmentStatus> validStatuses = Arrays.asList(
                AppointmentStatus.SCHEDULED,
                AppointmentStatus.CONFIRMED
        );

        List<Appointment> appointments = appointmentRepository.findAppointmentsForReminder(
                startDate, endDate, validStatuses);

        return appointments.stream()
                .map(appointmentMapper::toDto)
                .map(this::enrichWithRelatedInfo)
                .collect(Collectors.toList());
    }

    private void verifyRelatedEntities(AppointmentDto appointmentDto) {
        try {
            // Verify pet exists
            ResponseEntity<AppointmentDto.PetInfo> petResponse = petServiceClient.getPetById(appointmentDto.getPetId());
            if (!petResponse.getStatusCode().is2xxSuccessful() || petResponse.getBody() == null) {
                throw new ResourceNotFoundException("Pet not found with id: " + appointmentDto.getPetId());
            }

            // Verify client exists
            ResponseEntity<AppointmentDto.ClientInfo> clientResponse = clientServiceClient.getClientById(appointmentDto.getClientId());
            if (!clientResponse.getStatusCode().is2xxSuccessful() || clientResponse.getBody() == null) {
                throw new ResourceNotFoundException("Client not found with id: " + appointmentDto.getClientId());
            }

            // Verify veterinarian exists
            ResponseEntity<AppointmentDto.VeterinarianInfo> vetResponse = userServiceClient.getUserById(appointmentDto.getVeterinarianId());
            if (!vetResponse.getStatusCode().is2xxSuccessful() || vetResponse.getBody() == null) {
                throw new ResourceNotFoundException("Veterinarian not found with id: " + appointmentDto.getVeterinarianId());
            }

        } catch (Exception e) {
            logger.error("Error verifying related entities: ", e);
            throw new ResourceNotFoundException("Error verifying related entities: " + e.getMessage());
        }
    }

    private AppointmentDto enrichWithRelatedInfo(AppointmentDto appointmentDto) {
        try {
            // Enrich with pet information
            ResponseEntity<AppointmentDto.PetInfo> petResponse = petServiceClient.getPetById(appointmentDto.getPetId());
            if (petResponse.getStatusCode().is2xxSuccessful() && petResponse.getBody() != null) {
                appointmentDto.setPetInfo(petResponse.getBody());
            }

            // Enrich with client information
            ResponseEntity<AppointmentDto.ClientInfo> clientResponse = clientServiceClient.getClientById(appointmentDto.getClientId());
            if (clientResponse.getStatusCode().is2xxSuccessful() && clientResponse.getBody() != null) {
                appointmentDto.setClientInfo(clientResponse.getBody());
            }

            // Enrich with veterinarian information
            ResponseEntity<AppointmentDto.VeterinarianInfo> vetResponse = userServiceClient.getUserById(appointmentDto.getVeterinarianId());
            if (vetResponse.getStatusCode().is2xxSuccessful() && vetResponse.getBody() != null) {
                appointmentDto.setVeterinarianInfo(vetResponse.getBody());
            }

        } catch (Exception e) {
            logger.warn("Could not fetch related information for appointment ID: " + appointmentDto.getId(), e);
        }

        return appointmentDto;
    }
}
