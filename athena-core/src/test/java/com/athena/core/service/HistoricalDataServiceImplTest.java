package com.athena.core.service;

import com.athena.core.dto.HistoricalDataCreateDTO;
import com.athena.core.dto.HistoricalDataResponseDTO;
import com.athena.core.dto.HistoricalDataUpdateDTO;
import com.athena.core.entity.HistoricalData;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.HistoricalDataRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoricalDataServiceImplTest {

    @Mock
    private HistoricalDataRepository historicalDataRepository;

    @InjectMocks
    private HistoricalDataServiceImpl historicalDataService;

    private HistoricalData testHistoricalData;
    private UUID testHistoricalDataId;
    private UUID testEntityId;
    private Map<String, Object> testDataValue;
    private Instant testCapturedAt;

    @BeforeEach
    void setUp() {
        testHistoricalDataId = UUID.randomUUID();
        testEntityId = UUID.randomUUID();
        testCapturedAt = Instant.now();

        testDataValue = new HashMap<>();
        testDataValue.put("totalValue", 1000000);
        testDataValue.put("status", "active");
        testDataValue.put("awardCount", 5);

        testHistoricalData = new HistoricalData(
            "Opportunity",
            testEntityId,
            "snapshot",
            testDataValue
        );
        testHistoricalData.setId(testHistoricalDataId);
        testHistoricalData.setCapturedAt(testCapturedAt);
        testHistoricalData.setCreatedAt(testCapturedAt);
    }

    @Test
    void create_ShouldCreateHistoricalData_WhenValidData() {
        // Given
        HistoricalDataCreateDTO dto = new HistoricalDataCreateDTO(
            "Opportunity",
            testEntityId,
            "snapshot",
            testDataValue,
            testCapturedAt
        );

        when(historicalDataRepository.save(any(HistoricalData.class))).thenReturn(testHistoricalData);

        // When
        HistoricalDataResponseDTO result = historicalDataService.create(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.entityType()).isEqualTo("Opportunity");
        assertThat(result.dataType()).isEqualTo("snapshot");
        verify(historicalDataRepository).save(any(HistoricalData.class));
    }

    @Test
    void findById_ShouldReturnHistoricalData_WhenExists() {
        // Given
        when(historicalDataRepository.findById(testHistoricalDataId))
            .thenReturn(Optional.of(testHistoricalData));

        // When
        Optional<HistoricalDataResponseDTO> result = historicalDataService.findById(testHistoricalDataId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(testHistoricalDataId);
        assertThat(result.get().entityId()).isEqualTo(testEntityId);
        verify(historicalDataRepository).findById(testHistoricalDataId);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(historicalDataRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<HistoricalDataResponseDTO> result = historicalDataService.findById(nonExistentId);

        // Then
        assertThat(result).isEmpty();
        verify(historicalDataRepository).findById(nonExistentId);
    }

    @Test
    void findAll_ShouldReturnPageOfHistoricalData() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<HistoricalData> page = new PageImpl<>(Arrays.asList(testHistoricalData));
        when(historicalDataRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<HistoricalDataResponseDTO> result = historicalDataService.findAll(pageable);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).entityType()).isEqualTo("Opportunity");
        verify(historicalDataRepository).findAll(pageable);
    }

    @Test
    void update_ShouldUpdateHistoricalData_WhenValidData() {
        // Given
        Map<String, Object> newDataValue = new HashMap<>(testDataValue);
        newDataValue.put("totalValue", 2000000);

        HistoricalDataUpdateDTO dto = new HistoricalDataUpdateDTO(
            null,
            null,
            "updated_snapshot",
            newDataValue,
            null
        );

        when(historicalDataRepository.findById(testHistoricalDataId))
            .thenReturn(Optional.of(testHistoricalData));
        when(historicalDataRepository.save(any(HistoricalData.class))).thenReturn(testHistoricalData);

        // When
        HistoricalDataResponseDTO result = historicalDataService.update(testHistoricalDataId, dto);

        // Then
        assertThat(result).isNotNull();
        verify(historicalDataRepository).findById(testHistoricalDataId);
        verify(historicalDataRepository).save(testHistoricalData);
    }

    @Test
    void update_ShouldThrowException_WhenNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        HistoricalDataUpdateDTO dto = new HistoricalDataUpdateDTO(null, null, null, null, null);
        when(historicalDataRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> historicalDataService.update(nonExistentId, dto))
            .isInstanceOf(EntityNotFoundException.class);

        verify(historicalDataRepository).findById(nonExistentId);
        verify(historicalDataRepository, never()).save(any());
    }

    @Test
    void delete_ShouldDeleteHistoricalData_WhenExists() {
        // Given
        when(historicalDataRepository.existsById(testHistoricalDataId)).thenReturn(true);

        // When
        historicalDataService.delete(testHistoricalDataId);

        // Then
        verify(historicalDataRepository).existsById(testHistoricalDataId);
        verify(historicalDataRepository).deleteById(testHistoricalDataId);
    }

    @Test
    void delete_ShouldThrowException_WhenNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(historicalDataRepository.existsById(nonExistentId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> historicalDataService.delete(nonExistentId))
            .isInstanceOf(EntityNotFoundException.class);

        verify(historicalDataRepository).existsById(nonExistentId);
        verify(historicalDataRepository, never()).deleteById(any());
    }

    @Test
    void findByEntityId_ShouldReturnMatchingHistoricalData() {
        // Given
        when(historicalDataRepository.findAll())
            .thenReturn(Arrays.asList(testHistoricalData));

        // When
        List<HistoricalDataResponseDTO> result = historicalDataService.findByEntityId(testEntityId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).entityId()).isEqualTo(testEntityId);
        verify(historicalDataRepository).findAll();
    }

    @Test
    void findByEntityTypeAndEntityId_ShouldReturnMatchingHistoricalData() {
        // Given
        String entityType = "Opportunity";
        when(historicalDataRepository.findByEntityTypeAndEntityId(entityType, testEntityId))
            .thenReturn(Arrays.asList(testHistoricalData));

        // When
        List<HistoricalDataResponseDTO> result =
            historicalDataService.findByEntityTypeAndEntityId(entityType, testEntityId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).entityType()).isEqualTo(entityType);
        assertThat(result.get(0).entityId()).isEqualTo(testEntityId);
        verify(historicalDataRepository).findByEntityTypeAndEntityId(entityType, testEntityId);
    }

    @Test
    void findByDataType_ShouldReturnMatchingHistoricalData() {
        // Given
        String dataType = "snapshot";
        when(historicalDataRepository.findAll())
            .thenReturn(Arrays.asList(testHistoricalData));

        // When
        List<HistoricalDataResponseDTO> result = historicalDataService.findByDataType(dataType);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).dataType()).isEqualTo(dataType);
        verify(historicalDataRepository).findAll();
    }

    @Test
    void findByDateRange_ShouldReturnHistoricalDataInRange() {
        // Given
        Instant startDate = testCapturedAt.minusSeconds(3600);
        Instant endDate = testCapturedAt.plusSeconds(3600);
        when(historicalDataRepository.findAll())
            .thenReturn(Arrays.asList(testHistoricalData));

        // When
        List<HistoricalDataResponseDTO> result =
            historicalDataService.findByDateRange(startDate, endDate);

        // Then
        assertThat(result).hasSize(1);
        verify(historicalDataRepository).findAll();
    }

    @Test
    void findByEntityType_ShouldReturnMatchingHistoricalData() {
        // Given
        String entityType = "Opportunity";
        when(historicalDataRepository.findAll())
            .thenReturn(Arrays.asList(testHistoricalData));

        // When
        List<HistoricalDataResponseDTO> result = historicalDataService.findByEntityType(entityType);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).entityType()).isEqualTo(entityType);
        verify(historicalDataRepository).findAll();
    }
}
