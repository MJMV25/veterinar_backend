package com.example.medicalservice.client;

import com.example.medicalservice.dto.MedicalRecordDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pet-service")
public interface PetServiceClient {

    @GetMapping("/pets/{id}")
    ResponseEntity<MedicalRecordDto.PetInfo> getPetById(@PathVariable("id") Long id);
}