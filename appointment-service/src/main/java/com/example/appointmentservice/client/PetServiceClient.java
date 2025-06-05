package com.example.appointmentservice.client;

import com.example.appointmentservice.dto.AppointmentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pet-service")
public interface PetServiceClient {

    @GetMapping("/pets/{id}")
    ResponseEntity<AppointmentDto.PetInfo> getPetById(@PathVariable("id") Long id);
}