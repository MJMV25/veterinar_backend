package com.example.medicalservice.client;

import com.example.medicalservice.dto.MedicalRecordDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/users/{id}")
    ResponseEntity<MedicalRecordDto.VeterinarianInfo> getUserById(@PathVariable("id") Long id);
}