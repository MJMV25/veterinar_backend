package com.example.clientservice.repository;

import com.example.clientservice.entity.Client;
import com.example.clientservice.entity.ClientStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByDocumentNumber(String documentNumber);
    Optional<Client> findByEmail(String email);
    boolean existsByDocumentNumber(String documentNumber);
    boolean existsByEmail(String email);

    Page<Client> findByStatus(ClientStatus status, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE c.firstName LIKE %:name% OR c.lastName LIKE %:name%")
    Page<Client> findByNameContaining(@Param("name") String name, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE c.city = :city")
    Page<Client> findByCity(@Param("city") String city, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE c.phone LIKE %:phone%")
    Page<Client> findByPhoneContaining(@Param("phone") String phone, Pageable pageable);
}