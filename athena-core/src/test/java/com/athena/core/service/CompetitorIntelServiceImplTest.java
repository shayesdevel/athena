package com.athena.core.service;

import com.athena.core.dto.*;
import com.athena.core.entity.CompetitorIntel;
import com.athena.core.repository.CompetitorIntelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompetitorIntelServiceImplTest {
    @Mock private CompetitorIntelRepository competitorIntelRepository;
    @InjectMocks private CompetitorIntelServiceImpl competitorIntelService;
    private CompetitorIntel testIntel;
    private UUID testId, testOrgId, testOppId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testOrgId = UUID.randomUUID();
        testOppId = UUID.randomUUID();
        testIntel = new CompetitorIntel(testOrgId, testOppId, "industry_report");
        testIntel.setId(testId);
    }

    @Test void create_ShouldCreateIntel() {
        when(competitorIntelRepository.save(any(CompetitorIntel.class))).thenReturn(testIntel);
        CompetitorIntelResponseDTO result = competitorIntelService.create(new CompetitorIntelCreateDTO(testOrgId, testOppId, "high", null, null, "industry_report"));
        assertThat(result).isNotNull();
    }

    @Test void findById_ShouldReturnIntel() {
        when(competitorIntelRepository.findById(testId)).thenReturn(Optional.of(testIntel));
        Optional<CompetitorIntelResponseDTO> result = competitorIntelService.findById(testId);
        assertThat(result).isPresent();
    }

    @Test void findByOpportunityId_ShouldReturnIntel() {
        when(competitorIntelRepository.findByOpportunityId(testOppId)).thenReturn(Arrays.asList(testIntel));
        List<CompetitorIntelResponseDTO> result = competitorIntelService.findByOpportunityId(testOppId);
        assertThat(result).hasSize(1);
    }

    @Test void delete_ShouldDeleteIntel() {
        when(competitorIntelRepository.existsById(testId)).thenReturn(true);
        competitorIntelService.delete(testId);
        verify(competitorIntelRepository).deleteById(testId);
    }
}
