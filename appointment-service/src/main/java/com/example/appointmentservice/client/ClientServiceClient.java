package com.example.appointmentservice.client;

import com.example.appointmentservice.dto.AppointmentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "client-service")
public interface ClientServiceClient {

    @GetMapping("/clients/{id}")
    ResponseEntity<AppointmentDto.ClientInfo> getClientById(@PathVariable("id") Long id);
}