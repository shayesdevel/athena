package com.athena.core.service;

import com.athena.core.dto.HistoricalDataCreateDTO;
import com.athena.core.dto.HistoricalDataResponseDTO;
import com.athena.core.dto.HistoricalDataUpdateDTO;
import com.athena.core.entity.HistoricalData;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.HistoricalDataRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of HistoricalDataService.
 */
@Service
@Transactional(readOnly = true)
public class HistoricalDataServiceImpl implements HistoricalDataService {

    private final HistoricalDataRepository historicalDataRepository;

    public HistoricalDataServiceImpl(HistoricalDataRepository historicalDataRepository) {
        this.historicalDataRepository = historicalDataRepository;
    }

    @Override
    @Transactional
    public HistoricalDataResponseDTO create(HistoricalDataCreateDTO dto) {
        HistoricalData historicalData = new HistoricalData(
            dto.entityType(),
            dto.entityId(),
            dto.dataType(),
            dto.dataValue()
        );

        if (dto.capturedAt() != null) {
            historicalData.setCapturedAt(dto.capturedAt());
        }

        HistoricalData saved = historicalDataRepository.save(historicalData);
        return HistoricalDataResponseDTO.fromEntity(saved);
    }

    @Override
    public Optional<HistoricalDataResponseDTO> findById(UUID id) {
        return historicalDataRepository.findById(id)
            .map(HistoricalDataResponseDTO::fromEntity);
    }

    @Override
    public Page<HistoricalDataResponseDTO> findAll(Pageable pageable) {
        return historicalDataRepository.findAll(pageable)
            .map(HistoricalDataResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public HistoricalDataResponseDTO update(UUID id, HistoricalDataUpdateDTO dto) {
        HistoricalData historicalData = historicalDataRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("HistoricalData", id));

        updateEntityFromDto(dto, historicalData);

        HistoricalData updated = historicalDataRepository.save(historicalData);
        return HistoricalDataResponseDTO.fromEntity(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!historicalDataRepository.existsById(id)) {
            throw new EntityNotFoundException("HistoricalData", id);
        }
        historicalDataRepository.deleteById(id);
    }

    @Override
    public List<HistoricalDataResponseDTO> findByEntityId(UUID entityId) {
        // Since repository only has findByEntityTypeAndEntityId, we'll get all entity types
        // This is a simplified implementation - in production we might want a custom query
        return historicalDataRepository.findAll()
            .stream()
            .filter(hd -> hd.getEntityId().equals(entityId))
            .map(HistoricalDataResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<HistoricalDataResponseDTO> findByEntityTypeAndEntityId(String entityType, UUID entityId) {
        return historicalDataRepository.findByEntityTypeAndEntityId(entityType, entityId)
            .stream()
            .map(HistoricalDataResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<HistoricalDataResponseDTO> findByDataType(String dataType) {
        // Since repository only has findByEntityTypeAndEntityIdAndDataType, use filtering
        return historicalDataRepository.findAll()
            .stream()
            .filter(hd -> hd.getDataType().equals(dataType))
            .map(HistoricalDataResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<HistoricalDataResponseDTO> findByDateRange(Instant startDate, Instant endDate) {
        // Use filtering since there's no direct date range query without entity type/ID
        return historicalDataRepository.findAll()
            .stream()
            .filter(hd -> !hd.getCapturedAt().isBefore(startDate) &&
                         !hd.getCapturedAt().isAfter(endDate))
            .map(HistoricalDataResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<HistoricalDataResponseDTO> findByEntityType(String entityType) {
        // Use filtering for entity type
        return historicalDataRepository.findAll()
            .stream()
            .filter(hd -> hd.getEntityType().equals(entityType))
            .map(HistoricalDataResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Update entity fields from update DTO (only non-null fields).
     */
    private void updateEntityFromDto(HistoricalDataUpdateDTO dto, HistoricalData historicalData) {
        if (dto.entityType() != null) historicalData.setEntityType(dto.entityType());
        if (dto.entityId() != null) historicalData.setEntityId(dto.entityId());
        if (dto.dataType() != null) historicalData.setDataType(dto.dataType());
        if (dto.dataValue() != null) historicalData.setDataValue(dto.dataValue());
        if (dto.capturedAt() != null) historicalData.setCapturedAt(dto.capturedAt());
    }
}
