package com.example.appointmentservice.client;

import com.example.appointmentservice.dto.AppointmentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/users/{id}")
    ResponseEntity<AppointmentDto.VeterinarianInfo> getUserById(@PathVariable("id") Long id);
}