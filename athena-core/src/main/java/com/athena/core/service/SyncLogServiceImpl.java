package com.athena.core.service;

import com.athena.core.dto.SyncLogCreateDTO;
import com.athena.core.dto.SyncLogResponseDTO;
import com.athena.core.dto.SyncLogUpdateDTO;
import com.athena.core.entity.SyncLog;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.SyncLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of SyncLogService.
 */
@Service
@Transactional(readOnly = true)
public class SyncLogServiceImpl implements SyncLogService {

    private final SyncLogRepository syncLogRepository;

    public SyncLogServiceImpl(SyncLogRepository syncLogRepository) {
        this.syncLogRepository = syncLogRepository;
    }

    @Override
    @Transactional
    public SyncLogResponseDTO create(SyncLogCreateDTO dto) {
        SyncLog syncLog = new SyncLog(dto.syncType(), dto.status());
        mapDtoToEntity(dto, syncLog);

        SyncLog savedSyncLog = syncLogRepository.save(syncLog);
        return SyncLogResponseDTO.fromEntity(savedSyncLog);
    }

    @Override
    public Optional<SyncLogResponseDTO> findById(UUID id) {
        return syncLogRepository.findById(id)
            .map(SyncLogResponseDTO::fromEntity);
    }

    @Override
    public Page<SyncLogResponseDTO> findAll(Pageable pageable) {
        return syncLogRepository.findAll(pageable)
            .map(SyncLogResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public SyncLogResponseDTO update(UUID id, SyncLogUpdateDTO dto) {
        SyncLog syncLog = syncLogRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("SyncLog", id));

        updateEntityFromDto(dto, syncLog);

        SyncLog updatedSyncLog = syncLogRepository.save(syncLog);
        return SyncLogResponseDTO.fromEntity(updatedSyncLog);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!syncLogRepository.existsById(id)) {
            throw new EntityNotFoundException("SyncLog", id);
        }
        syncLogRepository.deleteById(id);
    }

    @Override
    public List<SyncLogResponseDTO> findByStatus(String status) {
        return syncLogRepository.findByStatus(status)
            .stream()
            .map(SyncLogResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<SyncLogResponseDTO> findBySyncType(String syncType) {
        return syncLogRepository.findBySyncType(syncType)
            .stream()
            .map(SyncLogResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<SyncLogResponseDTO> findByDateRange(Instant startDate, Instant endDate) {
        return syncLogRepository.findByTimeRange(startDate, endDate)
            .stream()
            .map(SyncLogResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<SyncLogResponseDTO> findRecentSyncs(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "startedAt"));
        return syncLogRepository.findAll(pageable)
            .stream()
            .map(SyncLogResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<SyncLogResponseDTO> findFailedSyncs() {
        return findByStatus("FAILED");
    }

    /**
     * Map create DTO fields to entity.
     */
    private void mapDtoToEntity(SyncLogCreateDTO dto, SyncLog syncLog) {
        if (dto.startedAt() != null) {
            syncLog.setStartedAt(dto.startedAt());
        }
        if (dto.recordsProcessed() != null) {
            syncLog.setRecordsProcessed(dto.recordsProcessed());
        }
        if (dto.errorCount() != null) {
            syncLog.setErrorCount(dto.errorCount());
        }
        if (dto.errorLog() != null) {
            syncLog.setErrorLog(dto.errorLog());
        }
    }

    /**
     * Update entity fields from update DTO (only non-null fields).
     */
    private void updateEntityFromDto(SyncLogUpdateDTO dto, SyncLog syncLog) {
        if (dto.syncType() != null) syncLog.setSyncType(dto.syncType());
        if (dto.status() != null) syncLog.setStatus(dto.status());
        if (dto.completedAt() != null) syncLog.setCompletedAt(dto.completedAt());
        if (dto.recordsProcessed() != null) syncLog.setRecordsProcessed(dto.recordsProcessed());
        if (dto.errorCount() != null) syncLog.setErrorCount(dto.errorCount());
        if (dto.errorLog() != null) syncLog.setErrorLog(dto.errorLog());
    }
}
