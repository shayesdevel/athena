package com.athena.core.service;

import com.athena.core.dto.UserCreateDTO;
import com.athena.core.dto.UserResponseDTO;
import com.athena.core.dto.UserUpdateDTO;
import com.athena.core.entity.User;
import com.athena.core.exception.DuplicateEntityException;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of UserService.
 * Note: Password hashing should be handled by a security service in production.
 */
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserResponseDTO create(UserCreateDTO dto) {
        // Check for duplicates
        if (userRepository.existsByEmail(dto.email())) {
            throw new DuplicateEntityException("User", "email", dto.email());
        }
        if (userRepository.existsByUsername(dto.username())) {
            throw new DuplicateEntityException("User", "username", dto.username());
        }

        // Create user entity
        // Note: In production, password should be hashed using BCrypt or similar
        User user = new User(dto.email(), dto.username(), dto.password());
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setIsAdmin(dto.isAdmin() != null ? dto.isAdmin() : false);

        // Save and return
        User savedUser = userRepository.save(user);
        return UserResponseDTO.fromEntity(savedUser);
    }

    @Override
    public Optional<UserResponseDTO> findById(UUID id) {
        return userRepository.findById(id)
            .map(UserResponseDTO::fromEntity);
    }

    @Override
    public Page<UserResponseDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(UserResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public UserResponseDTO update(UUID id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User", id));

        // Update fields if provided
        if (dto.email() != null && !dto.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.email())) {
                throw new DuplicateEntityException("User", "email", dto.email());
            }
            user.setEmail(dto.email());
        }

        if (dto.username() != null && !dto.username().equals(user.getUsername())) {
            if (userRepository.existsByUsername(dto.username())) {
                throw new DuplicateEntityException("User", "username", dto.username());
            }
            user.setUsername(dto.username());
        }

        if (dto.firstName() != null) {
            user.setFirstName(dto.firstName());
        }

        if (dto.lastName() != null) {
            user.setLastName(dto.lastName());
        }

        if (dto.isActive() != null) {
            user.setIsActive(dto.isActive());
        }

        if (dto.isAdmin() != null) {
            user.setIsAdmin(dto.isAdmin());
        }

        User updatedUser = userRepository.save(user);
        return UserResponseDTO.fromEntity(updatedUser);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User", id));

        // Soft delete
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public Optional<UserResponseDTO> findByEmail(String email) {
        return userRepository.findByEmail(email)
            .map(UserResponseDTO::fromEntity);
    }

    @Override
    public Optional<UserResponseDTO> findByUsername(String username) {
        return userRepository.findByUsername(username)
            .map(UserResponseDTO::fromEntity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional
    public void updateLastLogin(UUID id, Instant loginTime) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User", id));

        user.setLastLoginAt(loginTime);
        userRepository.save(user);
    }
}
