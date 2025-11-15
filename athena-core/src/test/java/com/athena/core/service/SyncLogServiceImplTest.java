package com.athena.core.service;

import com.athena.core.dto.SyncLogCreateDTO;
import com.athena.core.dto.SyncLogResponseDTO;
import com.athena.core.dto.SyncLogUpdateDTO;
import com.athena.core.entity.SyncLog;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.SyncLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncLogServiceImplTest {

    @Mock
    private SyncLogRepository syncLogRepository;

    @InjectMocks
    private SyncLogServiceImpl syncLogService;

    private SyncLog testSyncLog;
    private UUID testSyncLogId;
    private Instant testStartTime;
    private Instant testCompletedTime;

    @BeforeEach
    void setUp() {
        testSyncLogId = UUID.randomUUID();
        testStartTime = Instant.now().minusSeconds(3600);
        testCompletedTime = Instant.now();

        testSyncLog = new SyncLog("SAM_OPPORTUNITIES", "COMPLETED");
        testSyncLog.setId(testSyncLogId);
        testSyncLog.setStartedAt(testStartTime);
        testSyncLog.setCompletedAt(testCompletedTime);
        testSyncLog.setRecordsProcessed(150);
        testSyncLog.setErrorCount(0);
        testSyncLog.setCreatedAt(testStartTime);
        testSyncLog.setUpdatedAt(testCompletedTime);
    }

    @Test
    void create_ShouldCreateSyncLog_WhenValidData() {
        // Given
        SyncLogCreateDTO dto = new SyncLogCreateDTO(
            "SAM_OPPORTUNITIES",
            "IN_PROGRESS",
            testStartTime,
            0,
            0,
            null
        );

        when(syncLogRepository.save(any(SyncLog.class))).thenReturn(testSyncLog);

        // When
        SyncLogResponseDTO result = syncLogService.create(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.syncType()).isEqualTo("SAM_OPPORTUNITIES");
        verify(syncLogRepository).save(any(SyncLog.class));
    }

    @Test
    void findById_ShouldReturnSyncLog_WhenExists() {
        // Given
        when(syncLogRepository.findById(testSyncLogId)).thenReturn(Optional.of(testSyncLog));

        // When
        Optional<SyncLogResponseDTO> result = syncLogService.findById(testSyncLogId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(testSyncLogId);
        assertThat(result.get().syncType()).isEqualTo("SAM_OPPORTUNITIES");
        verify(syncLogRepository).findById(testSyncLogId);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(syncLogRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<SyncLogResponseDTO> result = syncLogService.findById(nonExistentId);

        // Then
        assertThat(result).isEmpty();
        verify(syncLogRepository).findById(nonExistentId);
    }

    @Test
    void findAll_ShouldReturnPageOfSyncLogs() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<SyncLog> page = new PageImpl<>(Arrays.asList(testSyncLog));
        when(syncLogRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<SyncLogResponseDTO> result = syncLogService.findAll(pageable);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).syncType()).isEqualTo("SAM_OPPORTUNITIES");
        verify(syncLogRepository).findAll(pageable);
    }

    @Test
    void update_ShouldUpdateSyncLog_WhenValidData() {
        // Given
        SyncLogUpdateDTO dto = new SyncLogUpdateDTO(
            null,
            "COMPLETED",
            testCompletedTime,
            150,
            0,
            null
        );

        when(syncLogRepository.findById(testSyncLogId)).thenReturn(Optional.of(testSyncLog));
        when(syncLogRepository.save(any(SyncLog.class))).thenReturn(testSyncLog);

        // When
        SyncLogResponseDTO result = syncLogService.update(testSyncLogId, dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo("COMPLETED");
        verify(syncLogRepository).findById(testSyncLogId);
        verify(syncLogRepository).save(testSyncLog);
    }

    @Test
    void update_ShouldThrowException_WhenNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        SyncLogUpdateDTO dto = new SyncLogUpdateDTO(null, "COMPLETED", null, null, null, null);
        when(syncLogRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> syncLogService.update(nonExistentId, dto))
            .isInstanceOf(EntityNotFoundException.class);

        verify(syncLogRepository).findById(nonExistentId);
        verify(syncLogRepository, never()).save(any());
    }

    @Test
    void delete_ShouldDeleteSyncLog_WhenExists() {
        // Given
        when(syncLogRepository.existsById(testSyncLogId)).thenReturn(true);

        // When
        syncLogService.delete(testSyncLogId);

        // Then
        verify(syncLogRepository).existsById(testSyncLogId);
        verify(syncLogRepository).deleteById(testSyncLogId);
    }

    @Test
    void delete_ShouldThrowException_WhenNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(syncLogRepository.existsById(nonExistentId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> syncLogService.delete(nonExistentId))
            .isInstanceOf(EntityNotFoundException.class);

        verify(syncLogRepository).existsById(nonExistentId);
        verify(syncLogRepository, never()).deleteById(any());
    }

    @Test
    void findByStatus_ShouldReturnMatchingSyncLogs() {
        // Given
        String status = "COMPLETED";
        when(syncLogRepository.findByStatus(status)).thenReturn(Arrays.asList(testSyncLog));

        // When
        List<SyncLogResponseDTO> result = syncLogService.findByStatus(status);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(status);
        verify(syncLogRepository).findByStatus(status);
    }

    @Test
    void findBySyncType_ShouldReturnMatchingSyncLogs() {
        // Given
        String syncType = "SAM_OPPORTUNITIES";
        when(syncLogRepository.findBySyncType(syncType)).thenReturn(Arrays.asList(testSyncLog));

        // When
        List<SyncLogResponseDTO> result = syncLogService.findBySyncType(syncType);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).syncType()).isEqualTo(syncType);
        verify(syncLogRepository).findBySyncType(syncType);
    }

    @Test
    void findByDateRange_ShouldReturnSyncLogsInRange() {
        // Given
        Instant startDate = testStartTime.minusSeconds(7200);
        Instant endDate = testStartTime.plusSeconds(7200);
        when(syncLogRepository.findByTimeRange(startDate, endDate))
            .thenReturn(Arrays.asList(testSyncLog));

        // When
        List<SyncLogResponseDTO> result = syncLogService.findByDateRange(startDate, endDate);

        // Then
        assertThat(result).hasSize(1);
        verify(syncLogRepository).findByTimeRange(startDate, endDate);
    }

    @Test
    void findRecentSyncs_ShouldReturnLimitedResults() {
        // Given
        int limit = 5;
        Page<SyncLog> page = new PageImpl<>(Arrays.asList(testSyncLog));
        when(syncLogRepository.findAll(any(Pageable.class))).thenReturn(page);

        // When
        List<SyncLogResponseDTO> result = syncLogService.findRecentSyncs(limit);

        // Then
        assertThat(result).hasSize(1);
        verify(syncLogRepository).findAll(any(Pageable.class));
    }

    @Test
    void findFailedSyncs_ShouldReturnFailedSyncLogs() {
        // Given
        SyncLog failedSyncLog = new SyncLog("SAM_AWARDS", "FAILED");
        failedSyncLog.setId(UUID.randomUUID());
        failedSyncLog.setErrorCount(5);
        failedSyncLog.setErrorLog("Connection timeout");

        when(syncLogRepository.findByStatus("FAILED")).thenReturn(Arrays.asList(failedSyncLog));

        // When
        List<SyncLogResponseDTO> result = syncLogService.findFailedSyncs();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo("FAILED");
        verify(syncLogRepository).findByStatus("FAILED");
    }
}
