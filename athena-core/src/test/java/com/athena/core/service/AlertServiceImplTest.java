package com.athena.core.service;

import com.athena.core.dto.*;
import com.athena.core.entity.Alert;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.AlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceImplTest {
    @Mock private AlertRepository alertRepository;
    @InjectMocks private AlertServiceImpl alertService;
    private Alert testAlert;
    private UUID testAlertId, testUserId;
    private Map<String, Object> testCriteria;

    @BeforeEach
    void setUp() {
        testAlertId = UUID.randomUUID();
        testUserId = UUID.randomUUID();
        testCriteria = new HashMap<>();
        testCriteria.put("keyword", "software");
        testAlert = new Alert(testUserId, "new_opportunity", testCriteria, "daily");
        testAlert.setId(testAlertId);
    }

    @Test void create_ShouldCreateAlert() {
        when(alertRepository.save(any(Alert.class))).thenReturn(testAlert);
        AlertResponseDTO result = alertService.create(new AlertCreateDTO(testUserId, "new_opportunity", testCriteria, "daily", true));
        assertThat(result).isNotNull();
        verify(alertRepository).save(any(Alert.class));
    }

    @Test void findById_ShouldReturnAlert() {
        when(alertRepository.findById(testAlertId)).thenReturn(Optional.of(testAlert));
        Optional<AlertResponseDTO> result = alertService.findById(testAlertId);
        assertThat(result).isPresent();
    }

    @Test void delete_ShouldSoftDelete() {
        when(alertRepository.findById(testAlertId)).thenReturn(Optional.of(testAlert));
        when(alertRepository.save(any(Alert.class))).thenReturn(testAlert);
        alertService.delete(testAlertId);
        verify(alertRepository).save(testAlert);
    }

    @Test void findByUserId_ShouldReturnList() {
        when(alertRepository.findByUserId(testUserId)).thenReturn(Arrays.asList(testAlert));
        List<AlertResponseDTO> result = alertService.findByUserId(testUserId);
        assertThat(result).hasSize(1);
    }

    @Test void recordTrigger_ShouldUpdateTimestamp() {
        when(alertRepository.findById(testAlertId)).thenReturn(Optional.of(testAlert));
        when(alertRepository.save(any(Alert.class))).thenReturn(testAlert);
        alertService.recordTrigger(testAlertId);
        verify(alertRepository).save(testAlert);
    }
}
