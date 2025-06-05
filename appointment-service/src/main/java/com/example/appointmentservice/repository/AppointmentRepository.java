package com.example.appointmentservice.repository;

import com.example.appointmentservice.entity.Appointment;
import com.example.appointmentservice.entity.AppointmentStatus;
import com.example.appointmentservice.entity.AppointmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Page<Appointment> findByClientId(Long clientId, Pageable pageable);
    Page<Appointment> findByPetId(Long petId, Pageable pageable);
    Page<Appointment> findByVeterinarianId(Long veterinarianId, Pageable pageable);
    Page<Appointment> findByStatus(AppointmentStatus status, Pageable pageable);
    Page<Appointment> findByType(AppointmentType type, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate BETWEEN :startDate AND :endDate")
    Page<Appointment> findByAppointmentDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT a FROM Appointment a WHERE a.veterinarianId = :veterinarianId AND a.appointmentDate BETWEEN :startDate AND :endDate")
    List<Appointment> findByVeterinarianIdAndAppointmentDateBetween(
            @Param("veterinarianId") Long veterinarianId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.veterinarianId = :veterinarianId AND a.status = :status")
    Long countByVeterinarianIdAndStatus(
            @Param("veterinarianId") Long veterinarianId,
            @Param("status") AppointmentStatus status
    );

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate < :dateTime AND a.status = 'SCHEDULED'")
    List<Appointment> findOverdueAppointments(@Param("dateTime") LocalDateTime dateTime);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate BETWEEN :startDate AND :endDate AND a.status IN :statuses")
    List<Appointment> findAppointmentsForReminder(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("statuses") List<AppointmentStatus> statuses
    );
}