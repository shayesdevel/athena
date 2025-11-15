package com.athena.core.service;

import com.athena.core.dto.*;
import com.athena.core.entity.OpportunityScore;
import com.athena.core.repository.OpportunityScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpportunityScoreServiceImplTest {
    @Mock private OpportunityScoreRepository opportunityScoreRepository;
    @InjectMocks private OpportunityScoreServiceImpl opportunityScoreService;
    private OpportunityScore testScore;
    private UUID testId, testOppId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testOppId = UUID.randomUUID();
        testScore = new OpportunityScore(testOppId, "relevance", new BigDecimal("85.50"));
        testScore.setId(testId);
    }

    @Test void create_ShouldCreateScore() {
        when(opportunityScoreRepository.save(any(OpportunityScore.class))).thenReturn(testScore);
        OpportunityScoreResponseDTO result = opportunityScoreService.create(new OpportunityScoreCreateDTO(testOppId, "relevance", new BigDecimal("85.50"), null, null, null));
        assertThat(result).isNotNull();
    }

    @Test void findById_ShouldReturnScore() {
        when(opportunityScoreRepository.findById(testId)).thenReturn(Optional.of(testScore));
        Optional<OpportunityScoreResponseDTO> result = opportunityScoreService.findById(testId);
        assertThat(result).isPresent();
    }

    @Test void findByOpportunityId_ShouldReturnScores() {
        when(opportunityScoreRepository.findByOpportunityId(testOppId)).thenReturn(Arrays.asList(testScore));
        List<OpportunityScoreResponseDTO> result = opportunityScoreService.findByOpportunityId(testOppId);
        assertThat(result).hasSize(1);
    }

    @Test void delete_ShouldDeleteScore() {
        when(opportunityScoreRepository.existsById(testId)).thenReturn(true);
        opportunityScoreService.delete(testId);
        verify(opportunityScoreRepository).deleteById(testId);
    }
}
