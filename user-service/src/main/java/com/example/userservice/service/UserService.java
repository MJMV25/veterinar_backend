package com.example.userservice.service;

import com.example.userservice.dto.UserCreateDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.User;
import com.example.userservice.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface UserService {
    UserDto createUser(UserCreateDto userCreateDto);
    UserDto updateUser(Long id, UserDto userDto);
    Optional<UserDto> getUserById(Long id);
    Optional<UserDto> getUserByUsername(String username);
    Page<UserDto> getAllUsers(Pageable pageable);
    Page<UserDto> getUsersByStatus(UserStatus status, Pageable pageable);
    Page<UserDto> searchUsersByName(String name, Pageable pageable);
    void deleteUser(Long id);
    void changeUserStatus(Long id, UserStatus status);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}