package com.athena.core.service;

import com.athena.core.dto.AlertCreateDTO;
import com.athena.core.dto.AlertResponseDTO;
import com.athena.core.dto.AlertUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for Alert entity operations.
 * Handles user notification preferences and alert triggers.
 */
public interface AlertService {

    /**
     * Create a new alert.
     *
     * @param dto the alert creation data
     * @return the created alert
     */
    AlertResponseDTO create(AlertCreateDTO dto);

    /**
     * Find alert by ID.
     *
     * @param id the alert UUID
     * @return Optional containing the alert if found
     */
    Optional<AlertResponseDTO> findById(UUID id);

    /**
     * Find all alerts with pagination.
     *
     * @param pageable pagination parameters
     * @return page of alerts
     */
    Page<AlertResponseDTO> findAll(Pageable pageable);

    /**
     * Update an existing alert.
     *
     * @param id the alert UUID
     * @param dto the update data
     * @return the updated alert
     * @throws com.athena.core.exception.EntityNotFoundException if alert not found
     */
    AlertResponseDTO update(UUID id, AlertUpdateDTO dto);

    /**
     * Delete an alert (soft delete by setting isActive to false).
     *
     * @param id the alert UUID
     * @throws com.athena.core.exception.EntityNotFoundException if alert not found
     */
    void delete(UUID id);

    /**
     * Find alerts by user ID.
     *
     * @param userId the user UUID
     * @return list of alerts for the user
     */
    List<AlertResponseDTO> findByUserId(UUID userId);

    /**
     * Find active alerts by user ID.
     *
     * @param userId the user UUID
     * @return list of active alerts for the user
     */
    List<AlertResponseDTO> findActiveByUserId(UUID userId);

    /**
     * Find alerts by alert type.
     *
     * @param alertType the alert type
     * @return list of alerts with matching type
     */
    List<AlertResponseDTO> findByAlertType(String alertType);

    /**
     * Record alert trigger (update lastTriggered timestamp).
     *
     * @param id the alert UUID
     * @throws com.athena.core.exception.EntityNotFoundException if alert not found
     */
    void recordTrigger(UUID id);
}
