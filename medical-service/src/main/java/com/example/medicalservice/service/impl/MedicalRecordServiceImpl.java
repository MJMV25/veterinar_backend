package com.example.medicalservice.service.impl;

import com.example.medicalservice.client.PetServiceClient;
import com.example.medicalservice.client.UserServiceClient;
import com.example.medicalservice.dto.MedicalRecordDto;
import com.example.medicalservice.dto.PrescriptionDto;
import com.example.medicalservice.dto.VaccinationDto;
import com.example.medicalservice.entity.MedicalRecord;
import com.example.medicalservice.entity.Prescription;
import com.example.medicalservice.entity.RecordType;
import com.example.medicalservice.entity.Vaccination;
import com.example.medicalservice.exception.ResourceNotFoundException;
import com.example.medicalservice.mapper.MedicalRecordMapper;
import com.example.medicalservice.mapper.PrescriptionMapper;
import com.example.medicalservice.mapper.VaccinationMapper;
import com.example.medicalservice.repository.MedicalRecordRepository;
import com.example.medicalservice.repository.PrescriptionRepository;
import com.example.medicalservice.repository.VaccinationRepository;
import com.example.medicalservice.service.MedicalRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordServiceImpl.class);

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private VaccinationRepository vaccinationRepository;

    @Autowired
    private MedicalRecordMapper medicalRecordMapper;

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Autowired
    private VaccinationMapper vaccinationMapper;

    @Autowired
    private PetServiceClient petServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;

    @Override
    public MedicalRecordDto createMedicalRecord(MedicalRecordDto medicalRecordDto) {
        // Verify that pet and veterinarian exist
        verifyRelatedEntities(medicalRecordDto);

        MedicalRecord medicalRecord = medicalRecordMapper.toEntity(medicalRecordDto);

        MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);
        MedicalRecordDto result = medicalRecordMapper.toDto(savedRecord);

        // Enrich with related information
        enrichWithRelatedInfo(result);

        return result;
    }

    @Override
    public MedicalRecordDto updateMedicalRecord(Long id, MedicalRecordDto medicalRecordDto) {
        MedicalRecord existingRecord = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found with id: " + id));

        // Update fields
        existingRecord.setSymptoms(medicalRecordDto.getSymptoms());
        existingRecord.setDiagnosis(medicalRecordDto.getDiagnosis());
        existingRecord.setTreatment(medicalRecordDto.getTreatment());
        existingRecord.setObservations(medicalRecordDto.getObservations());
        existingRecord.setRecommendations(medicalRecordDto.getRecommendations());
        existingRecord.setWeight(medicalRecordDto.getWeight());
        existingRecord.setTemperature(medicalRecordDto.getTemperature());
        existingRecord.setHeartRate(medicalRecordDto.getHeartRate());
        existingRecord.setRespiratoryRate(medicalRecordDto.getRespiratoryRate());
        existingRecord.setType(medicalRecordDto.getType());
        existingRecord.setNextVisitDate(medicalRecordDto.getNextVisitDate());

        MedicalRecord updatedRecord = medicalRecordRepository.save(existingRecord);
        MedicalRecordDto result = medicalRecordMapper.toDto(updatedRecord);

        // Enrich with related information
        enrichWithRelatedInfo(result);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MedicalRecordDto> getMedicalRecordById(Long id) {
        Optional<MedicalRecord> recordOpt = medicalRecordRepository.findById(id);
        if (recordOpt.isPresent()) {
            MedicalRecordDto dto = medicalRecordMapper.toDto(recordOpt.get());
            enrichWithRelatedInfo(dto);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicalRecordDto> getAllMedicalRecords(Pageable pageable) {
        return medicalRecordRepository.findAll(pageable)
                .map(medicalRecordMapper::toDto)
                .map(this::enrichWithRelatedInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicalRecordDto> getMedicalRecordsByPet(Long petId, Pageable pageable) {
        return medicalRecordRepository.findByPetId(petId, pageable)
                .map(medicalRecordMapper::toDto)
                .map(this::enrichWithRelatedInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicalRecordDto> getMedicalRecordsByVeterinarian(Long veterinarianId, Pageable pageable) {
        return medicalRecordRepository.findByVeterinarianId(veterinarianId, pageable)
                .map(medicalRecordMapper::toDto)
                .map(this::enrichWithRelatedInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicalRecordDto> getMedicalRecordsByType(RecordType type, Pageable pageable) {
        return medicalRecordRepository.findByType(type, pageable)
                .map(medicalRecordMapper::toDto)
                .map(this::enrichWithRelatedInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicalRecordDto> getMedicalRecordsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return medicalRecordRepository.findByConsultationDateBetween(startDate, endDate, pageable)
                .map(medicalRecordMapper::toDto)
                .map(this::enrichWithRelatedInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicalRecordDto> searchMedicalRecords(String keyword, Pageable pageable) {
        return medicalRecordRepository.findByDiagnosisOrSymptomsContaining(keyword, pageable)
                .map(medicalRecordMapper::toDto)
                .map(this::enrichWithRelatedInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordDto> getLatestRecordsByPet(Long petId) {
        List<MedicalRecord> records = medicalRecordRepository.findLatestRecordsByPetId(petId);
        return records.stream()
                .map(medicalRecordMapper::toDto)
                .map(this::enrichWithRelatedInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordDto> getUpcomingVisits(LocalDateTime startDate, LocalDateTime endDate) {
        List<MedicalRecord> records = medicalRecordRepository.findUpcomingVisits(startDate, endDate);
        return records.stream()
                .map(medicalRecordMapper::toDto)
                .map(this::enrichWithRelatedInfo)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMedicalRecord(Long id) {
        if (!medicalRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Medical record not found with id: " + id);
        }
        medicalRecordRepository.deleteById(id);
    }

    // Prescription methods
    @Override
    public PrescriptionDto addPrescription(Long medicalRecordId, PrescriptionDto prescriptionDto) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(medicalRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found with id: " + medicalRecordId));

        Prescription prescription = prescriptionMapper.toEntity(prescriptionDto);
        prescription.setMedicalRecord(medicalRecord);

        Prescription savedPrescription = prescriptionRepository.save(prescription);
        return prescriptionMapper.toDto(savedPrescription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionDto> getPrescriptionsByMedicalRecord(Long medicalRecordId) {
        List<Prescription> prescriptions = prescriptionRepository.findByMedicalRecordId(medicalRecordId);
        return prescriptions.stream()
                .map(prescriptionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionDto> getPrescriptionsByPet(Long petId) {
        List<Prescription> prescriptions = prescriptionRepository.findByPetId(petId);
        return prescriptions.stream()
                .map(prescriptionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionDto> getEndingPrescriptions(LocalDateTime startDate, LocalDateTime endDate) {
        List<Prescription> prescriptions = prescriptionRepository.findEndingPrescriptions(startDate, endDate);
        return prescriptions.stream()
                .map(prescriptionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePrescription(Long prescriptionId) {
        if (!prescriptionRepository.existsById(prescriptionId)) {
            throw new ResourceNotFoundException("Prescription not found with id: " + prescriptionId);
        }
        prescriptionRepository.deleteById(prescriptionId);
    }

    // Vaccination methods
    @Override
    public VaccinationDto addVaccination(Long medicalRecordId, VaccinationDto vaccinationDto) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(medicalRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found with id: " + medicalRecordId));

        Vaccination vaccination = vaccinationMapper.toEntity(vaccinationDto);
        vaccination.setMedicalRecord(medicalRecord);

        Vaccination savedVaccination = vaccinationRepository.save(vaccination);
        return vaccinationMapper.toDto(savedVaccination);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VaccinationDto> getVaccinationsByMedicalRecord(Long medicalRecordId) {
        List<Vaccination> vaccinations = vaccinationRepository.findByMedicalRecordId(medicalRecordId);
        return vaccinations.stream()
                .map(vaccinationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VaccinationDto> getVaccinationsByPet(Long petId) {
        List<Vaccination> vaccinations = vaccinationRepository.findByPetId(petId);
        return vaccinations.stream()
                .map(vaccinationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VaccinationDto> getUpcomingVaccinations(LocalDateTime startDate, LocalDateTime endDate) {
        List<Vaccination> vaccinations = vaccinationRepository.findUpcomingVaccinations(startDate, endDate);
        return vaccinations.stream()
                .map(vaccinationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteVaccination(Long vaccinationId) {
        if (!vaccinationRepository.existsById(vaccinationId)) {
            throw new ResourceNotFoundException("Vaccination not found with id: " + vaccinationId);
        }
        vaccinationRepository.deleteById(vaccinationId);
    }

    private void verifyRelatedEntities(MedicalRecordDto medicalRecordDto) {
        try {
            // Verify pet exists
            ResponseEntity<MedicalRecordDto.PetInfo> petResponse = petServiceClient.getPetById(medicalRecordDto.getPetId());
            if (!petResponse.getStatusCode().is2xxSuccessful() || petResponse.getBody() == null) {
                throw new ResourceNotFoundException("Pet not found with id: " + medicalRecordDto.getPetId());
            }

            // Verify veterinarian exists
            ResponseEntity<MedicalRecordDto.VeterinarianInfo> vetResponse = userServiceClient.getUserById(medicalRecordDto.getVeterinarianId());
            if (!vetResponse.getStatusCode().is2xxSuccessful() || vetResponse.getBody() == null) {
                throw new ResourceNotFoundException("Veterinarian not found with id: " + medicalRecordDto.getVeterinarianId());
            }

        } catch (Exception e) {
            logger.error("Error verifying related entities: ", e);
            throw new ResourceNotFoundException("Error verifying related entities: " + e.getMessage());
        }
    }

    private MedicalRecordDto enrichWithRelatedInfo(MedicalRecordDto medicalRecordDto) {
        try {
            // Enrich with pet information
            ResponseEntity<MedicalRecordDto.PetInfo> petResponse = petServiceClient.getPetById(medicalRecordDto.getPetId());
            if (petResponse.getStatusCode().is2xxSuccessful() && petResponse.getBody() != null) {
                medicalRecordDto.setPetInfo(petResponse.getBody());
            }

            // Enrich with veterinarian information
            ResponseEntity<MedicalRecordDto.VeterinarianInfo> vetResponse = userServiceClient.getUserById(medicalRecordDto.getVeterinarianId());
            if (vetResponse.getStatusCode().is2xxSuccessful() && vetResponse.getBody() != null) {
                medicalRecordDto.setVeterinarianInfo(vetResponse.getBody());
            }

        } catch (Exception e) {
            logger.warn("Could not fetch related information for medical record ID: " + medicalRecordDto.getId(), e);
        }

        return medicalRecordDto;
    }
}