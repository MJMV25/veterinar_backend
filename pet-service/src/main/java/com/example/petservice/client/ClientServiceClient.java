package com.example.petservice.client;

import com.example.petservice.dto.PetDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "client-service")
public interface ClientServiceClient {

    @GetMapping("/clients/{id}")
    ResponseEntity<PetDto.ClientInfo> getClientById(@PathVariable("id") Long id);
}