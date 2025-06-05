package com.example.clientservice.mapper;

import com.example.clientservice.dto.ClientDto;
import com.example.clientservice.entity.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public ClientDto toDto(Client client) {
        if (client == null) {
            return null;
        }

        ClientDto dto = new ClientDto();
        dto.setId(client.getId());
        dto.setDocumentNumber(client.getDocumentNumber());
        dto.setDocumentType(client.getDocumentType());
        dto.setFirstName(client.getFirstName());
        dto.setLastName(client.getLastName());
        dto.setEmail(client.getEmail());
        dto.setPhone(client.getPhone());
        dto.setAddress(client.getAddress());
        dto.setCity(client.getCity());
        dto.setDepartment(client.getDepartment());
        dto.setStatus(client.getStatus());
        dto.setCreatedAt(client.getCreatedAt());
        dto.setUpdatedAt(client.getUpdatedAt());

        return dto;
    }

    public Client toEntity(ClientDto dto) {
        if (dto == null) {
            return null;
        }

        Client client = new Client();
        client.setId(dto.getId());
        client.setDocumentNumber(dto.getDocumentNumber());
        client.setDocumentType(dto.getDocumentType());
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());
        client.setAddress(dto.getAddress());
        client.setCity(dto.getCity());
        client.setDepartment(dto.getDepartment());
        client.setStatus(dto.getStatus());

        return client;
    }
}