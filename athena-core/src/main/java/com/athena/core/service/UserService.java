package com.athena.core.service;

import com.athena.core.dto.UserCreateDTO;
import com.athena.core.dto.UserResponseDTO;
import com.athena.core.dto.UserUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for User entity operations.
 * Handles authentication credentials and user profile management.
 */
public interface UserService {

    /**
     * Create a new user.
     *
     * @param dto the user creation data
     * @return the created user
     * @throws com.athena.core.exception.DuplicateEntityException if email or username already exists
     */
    UserResponseDTO create(UserCreateDTO dto);

    /**
     * Find user by ID.
     *
     * @param id the user UUID
     * @return Optional containing the user if found
     */
    Optional<UserResponseDTO> findById(UUID id);

    /**
     * Find all users with pagination.
     *
     * @param pageable pagination parameters
     * @return page of users
     */
    Page<UserResponseDTO> findAll(Pageable pageable);

    /**
     * Update an existing user.
     *
     * @param id the user UUID
     * @param dto the update data
     * @return the updated user
     * @throws com.athena.core.exception.EntityNotFoundException if user not found
     */
    UserResponseDTO update(UUID id, UserUpdateDTO dto);

    /**
     * Delete a user (soft delete by setting isActive to false).
     *
     * @param id the user UUID
     * @throws com.athena.core.exception.EntityNotFoundException if user not found
     */
    void delete(UUID id);

    /**
     * Find user by email address.
     *
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<UserResponseDTO> findByEmail(String email);

    /**
     * Find user by username.
     *
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<UserResponseDTO> findByUsername(String username);

    /**
     * Check if email already exists.
     *
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Check if username already exists.
     *
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Update last login timestamp for a user.
     *
     * @param id the user UUID
     * @param loginTime the login timestamp
     * @throws com.athena.core.exception.EntityNotFoundException if user not found
     */
    void updateLastLogin(UUID id, Instant loginTime);
}
