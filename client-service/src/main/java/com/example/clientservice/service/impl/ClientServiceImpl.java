package com.example.clientservice.service.impl;

import com.example.clientservice.dto.ClientDto;
import com.example.clientservice.entity.Client;
import com.example.clientservice.entity.ClientStatus;
import com.example.clientservice.exception.DuplicateResourceException;
import com.example.clientservice.exception.ResourceNotFoundException;
import com.example.clientservice.mapper.ClientMapper;
import com.example.clientservice.repository.ClientRepository;
import com.example.clientservice.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientMapper clientMapper;

    @Override
    public ClientDto createClient(ClientDto clientDto) {
        // Validate unique constraints
        if (clientRepository.existsByDocumentNumber(clientDto.getDocumentNumber())) {
            throw new DuplicateResourceException("Document number already exists: " + clientDto.getDocumentNumber());
        }

        if (clientRepository.existsByEmail(clientDto.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + clientDto.getEmail());
        }

        Client client = clientMapper.toEntity(clientDto);
        client.setStatus(ClientStatus.ACTIVE);

        Client savedClient = clientRepository.save(client);
        return clientMapper.toDto(savedClient);
    }

    @Override
    public ClientDto updateClient(Long id, ClientDto clientDto) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        // Check if email is being changed and if it's already taken
        if (!existingClient.getEmail().equals(clientDto.getEmail()) &&
                clientRepository.existsByEmail(clientDto.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + clientDto.getEmail());
        }

        // Update fields
        existingClient.setFirstName(clientDto.getFirstName());
        existingClient.setLastName(clientDto.getLastName());
        existingClient.setEmail(clientDto.getEmail());
        existingClient.setPhone(clientDto.getPhone());
        existingClient.setAddress(clientDto.getAddress());
        existingClient.setCity(clientDto.getCity());
        existingClient.setDepartment(clientDto.getDepartment());

        Client updatedClient = clientRepository.save(existingClient);
        return clientMapper.toDto(updatedClient);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClientDto> getClientById(Long id) {
        return clientRepository.findById(id)
                .map(clientMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClientDto> getClientByDocumentNumber(String documentNumber) {
        return clientRepository.findByDocumentNumber(documentNumber)
                .map(clientMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientDto> getAllClients(Pageable pageable) {
        return clientRepository.findAll(pageable)
                .map(clientMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientDto> getClientsByStatus(ClientStatus status, Pageable pageable) {
        return clientRepository.findByStatus(status, pageable)
                .map(clientMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientDto> searchClientsByName(String name, Pageable pageable) {
        return clientRepository.findByNameContaining(name, pageable)
                .map(clientMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientDto> getClientsByCity(String city, Pageable pageable) {
        return clientRepository.findByCity(city, pageable)
                .map(clientMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientDto> searchClientsByPhone(String phone, Pageable pageable) {
        return clientRepository.findByPhoneContaining(phone, pageable)
                .map(clientMapper::toDto);
    }

    @Override
    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client not found with id: " + id);
        }
        clientRepository.deleteById(id);
    }

    @Override
    public void changeClientStatus(Long id, ClientStatus status) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        client.setStatus(status);
        clientRepository.save(client);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByDocumentNumber(String documentNumber) {
        return clientRepository.existsByDocumentNumber(documentNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return clientRepository.existsByEmail(email);
    }
}