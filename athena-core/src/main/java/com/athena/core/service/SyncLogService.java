package com.athena.core.service;

import com.athena.core.dto.SyncLogCreateDTO;
import com.athena.core.dto.SyncLogResponseDTO;
import com.athena.core.dto.SyncLogUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for SyncLog entity operations.
 * Handles SAM.gov synchronization tracking and audit logs.
 */
public interface SyncLogService {

    /**
     * Create a new sync log entry.
     *
     * @param dto the sync log creation data
     * @return the created sync log
     */
    SyncLogResponseDTO create(SyncLogCreateDTO dto);

    /**
     * Find sync log by ID.
     *
     * @param id the sync log UUID
     * @return Optional containing the sync log if found
     */
    Optional<SyncLogResponseDTO> findById(UUID id);

    /**
     * Find all sync logs with pagination.
     *
     * @param pageable pagination parameters
     * @return page of sync logs
     */
    Page<SyncLogResponseDTO> findAll(Pageable pageable);

    /**
     * Update an existing sync log entry.
     *
     * @param id the sync log UUID
     * @param dto the update data
     * @return the updated sync log
     * @throws com.athena.core.exception.EntityNotFoundException if sync log not found
     */
    SyncLogResponseDTO update(UUID id, SyncLogUpdateDTO dto);

    /**
     * Delete a sync log entry.
     *
     * @param id the sync log UUID
     * @throws com.athena.core.exception.EntityNotFoundException if sync log not found
     */
    void delete(UUID id);

    /**
     * Find sync logs by status.
     *
     * @param status the status to search for
     * @return list of sync logs with matching status
     */
    List<SyncLogResponseDTO> findByStatus(String status);

    /**
     * Find sync logs by sync type.
     *
     * @param syncType the sync type to search for
     * @return list of sync logs with matching sync type
     */
    List<SyncLogResponseDTO> findBySyncType(String syncType);

    /**
     * Find sync logs within a date range.
     *
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of sync logs within the date range
     */
    List<SyncLogResponseDTO> findByDateRange(Instant startDate, Instant endDate);

    /**
     * Find recent sync logs.
     *
     * @param limit the maximum number of logs to return
     * @return list of recent sync logs
     */
    List<SyncLogResponseDTO> findRecentSyncs(int limit);

    /**
     * Find failed sync logs.
     *
     * @return list of sync logs with failed status
     */
    List<SyncLogResponseDTO> findFailedSyncs();
}
