package com.example.clientservice.service;

import com.example.clientservice.dto.ClientDto;
import com.example.clientservice.entity.ClientStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface ClientService {
    ClientDto createClient(ClientDto clientDto);
    ClientDto updateClient(Long id, ClientDto clientDto);
    Optional<ClientDto> getClientById(Long id);
    Optional<ClientDto> getClientByDocumentNumber(String documentNumber);
    Page<ClientDto> getAllClients(Pageable pageable);
    Page<ClientDto> getClientsByStatus(ClientStatus status, Pageable pageable);
    Page<ClientDto> searchClientsByName(String name, Pageable pageable);
    Page<ClientDto> getClientsByCity(String city, Pageable pageable);
    Page<ClientDto> searchClientsByPhone(String phone, Pageable pageable);
    void deleteClient(Long id);
    void changeClientStatus(Long id, ClientStatus status);
    boolean existsByDocumentNumber(String documentNumber);
    boolean existsByEmail(String email);
}
