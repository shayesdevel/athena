package com.athena.core.service;

import com.athena.core.dto.HistoricalDataCreateDTO;
import com.athena.core.dto.HistoricalDataResponseDTO;
import com.athena.core.dto.HistoricalDataUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for HistoricalData entity operations.
 * Handles historical snapshots and time-series data tracking.
 */
public interface HistoricalDataService {

    /**
     * Create a new historical data entry.
     *
     * @param dto the historical data creation data
     * @return the created historical data
     */
    HistoricalDataResponseDTO create(HistoricalDataCreateDTO dto);

    /**
     * Find historical data by ID.
     *
     * @param id the historical data UUID
     * @return Optional containing the historical data if found
     */
    Optional<HistoricalDataResponseDTO> findById(UUID id);

    /**
     * Find all historical data with pagination.
     *
     * @param pageable pagination parameters
     * @return page of historical data
     */
    Page<HistoricalDataResponseDTO> findAll(Pageable pageable);

    /**
     * Update an existing historical data entry.
     *
     * @param id the historical data UUID
     * @param dto the update data
     * @return the updated historical data
     * @throws com.athena.core.exception.EntityNotFoundException if historical data not found
     */
    HistoricalDataResponseDTO update(UUID id, HistoricalDataUpdateDTO dto);

    /**
     * Delete a historical data entry.
     *
     * @param id the historical data UUID
     * @throws com.athena.core.exception.EntityNotFoundException if historical data not found
     */
    void delete(UUID id);

    /**
     * Find historical data by entity ID.
     *
     * @param entityId the entity UUID to search for
     * @return list of historical data entries for the entity
     */
    List<HistoricalDataResponseDTO> findByEntityId(UUID entityId);

    /**
     * Find historical data by entity type and ID.
     *
     * @param entityType the entity type
     * @param entityId the entity UUID
     * @return list of historical data entries for the entity
     */
    List<HistoricalDataResponseDTO> findByEntityTypeAndEntityId(String entityType, UUID entityId);

    /**
     * Find historical data by data type.
     *
     * @param dataType the data type to search for
     * @return list of historical data entries with matching data type
     */
    List<HistoricalDataResponseDTO> findByDataType(String dataType);

    /**
     * Find historical data within a date range.
     *
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of historical data entries within the date range
     */
    List<HistoricalDataResponseDTO> findByDateRange(Instant startDate, Instant endDate);

    /**
     * Find historical data by entity type.
     *
     * @param entityType the entity type
     * @return list of historical data entries for the entity type
     */
    List<HistoricalDataResponseDTO> findByEntityType(String entityType);
}
