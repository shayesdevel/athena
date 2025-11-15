package com.athena.core.service;

import com.athena.core.dto.AlertCreateDTO;
import com.athena.core.dto.AlertResponseDTO;
import com.athena.core.dto.AlertUpdateDTO;
import com.athena.core.entity.Alert;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.AlertRepository;
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
 * Implementation of AlertService.
 */
@Service
@Transactional(readOnly = true)
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;

    public AlertServiceImpl(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Override
    @Transactional
    public AlertResponseDTO create(AlertCreateDTO dto) {
        Alert alert = new Alert(dto.userId(), dto.alertType(), dto.criteria(), dto.frequency());

        if (dto.isActive() != null) {
            alert.setIsActive(dto.isActive());
        }

        Alert saved = alertRepository.save(alert);
        return AlertResponseDTO.fromEntity(saved);
    }

    @Override
    public Optional<AlertResponseDTO> findById(UUID id) {
        return alertRepository.findById(id)
            .map(AlertResponseDTO::fromEntity);
    }

    @Override
    public Page<AlertResponseDTO> findAll(Pageable pageable) {
        return alertRepository.findAll(pageable)
            .map(AlertResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public AlertResponseDTO update(UUID id, AlertUpdateDTO dto) {
        Alert alert = alertRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Alert", id));

        updateEntityFromDto(dto, alert);

        Alert updated = alertRepository.save(alert);
        return AlertResponseDTO.fromEntity(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Alert alert = alertRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Alert", id));

        alert.setIsActive(false);
        alertRepository.save(alert);
    }

    @Override
    public List<AlertResponseDTO> findByUserId(UUID userId) {
        return alertRepository.findByUserId(userId)
            .stream()
            .map(AlertResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<AlertResponseDTO> findActiveByUserId(UUID userId) {
        return alertRepository.findByUserIdAndIsActive(userId, true)
            .stream()
            .map(AlertResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<AlertResponseDTO> findByAlertType(String alertType) {
        return alertRepository.findByAlertType(alertType)
            .stream()
            .map(AlertResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void recordTrigger(UUID id) {
        Alert alert = alertRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Alert", id));

        alert.setLastTriggered(Instant.now());
        alertRepository.save(alert);
    }

    /**
     * Update entity fields from update DTO (only non-null fields).
     */
    private void updateEntityFromDto(AlertUpdateDTO dto, Alert alert) {
        if (dto.alertType() != null) alert.setAlertType(dto.alertType());
        if (dto.criteria() != null) alert.setCriteria(dto.criteria());
        if (dto.frequency() != null) alert.setFrequency(dto.frequency());
        if (dto.isActive() != null) alert.setIsActive(dto.isActive());
        if (dto.lastTriggered() != null) alert.setLastTriggered(dto.lastTriggered());
    }
}
