package com.athena.core.repository;

import com.athena.core.AbstractIntegrationTest;
import com.athena.core.entity.SyncLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SyncLogRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private SyncLogRepository syncLogRepository;

    @Test
    void shouldSaveAndRetrieveSyncLog() {
        // Arrange
        SyncLog log = new SyncLog("opportunities", "running");
        log.setStartedAt(Instant.now());
        log.setRecordsProcessed(150);
        log.setErrorCount(3);
        log.setErrorLog("Error details here");

        // Act
        SyncLog saved = syncLogRepository.save(log);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSyncType()).isEqualTo("opportunities");
        assertThat(saved.getStatus()).isEqualTo("running");
        assertThat(saved.getStartedAt()).isNotNull();
        assertThat(saved.getRecordsProcessed()).isEqualTo(150);
        assertThat(saved.getErrorCount()).isEqualTo(3);
        assertThat(saved.getErrorLog()).isEqualTo("Error details here");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFindBySyncType() {
        // Arrange
        syncLogRepository.save(new SyncLog("awards", "success"));
        syncLogRepository.save(new SyncLog("awards", "failed"));

        // Act
        List<SyncLog> awardsSyncs = syncLogRepository.findBySyncType("awards");

        // Assert
        assertThat(awardsSyncs).hasSizeGreaterThanOrEqualTo(2);
        assertThat(awardsSyncs).extracting(SyncLog::getSyncType)
                .containsOnly("awards");
    }

    @Test
    void shouldFindByStatus() {
        // Arrange
        syncLogRepository.save(new SyncLog("opportunities", "success"));
        syncLogRepository.save(new SyncLog("awards", "success"));

        // Act
        List<SyncLog> successfulSyncs = syncLogRepository.findByStatus("success");

        // Assert
        assertThat(successfulSyncs).hasSizeGreaterThanOrEqualTo(2);
        assertThat(successfulSyncs).extracting(SyncLog::getStatus)
                .containsOnly("success");
    }

    @Test
    void shouldFindBySyncTypeAndStatus() {
        // Arrange
        syncLogRepository.save(new SyncLog("organizations", "success"));
        syncLogRepository.save(new SyncLog("organizations", "failed"));

        // Act
        List<SyncLog> successfulOrgSyncs = syncLogRepository.findBySyncTypeAndStatus("organizations", "success");

        // Assert
        assertThat(successfulOrgSyncs).hasSizeGreaterThanOrEqualTo(1);
        assertThat(successfulOrgSyncs).allMatch(log ->
                log.getSyncType().equals("organizations") && log.getStatus().equals("success"));
    }

    @Test
    void shouldFindLatestBySyncType() throws InterruptedException {
        // Arrange
        SyncLog older = new SyncLog("full", "success");
        older.setStartedAt(Instant.now().minus(1, ChronoUnit.HOURS));
        syncLogRepository.save(older);

        Thread.sleep(10); // Ensure different timestamps

        SyncLog newer = new SyncLog("full", "running");
        newer.setStartedAt(Instant.now());
        syncLogRepository.save(newer);

        // Act
        var latest = syncLogRepository.findLatestBySyncType("full");

        // Assert
        assertThat(latest).isPresent();
        assertThat(latest.get().getStatus()).isEqualTo("running");
    }

    @Test
    void shouldFindByTimeRange() {
        // Arrange
        Instant now = Instant.now();
        Instant oneHourAgo = now.minus(1, ChronoUnit.HOURS);
        Instant twoHoursAgo = now.minus(2, ChronoUnit.HOURS);

        SyncLog log1 = new SyncLog("opportunities", "success");
        log1.setStartedAt(oneHourAgo);
        syncLogRepository.save(log1);

        SyncLog log2 = new SyncLog("awards", "success");
        log2.setStartedAt(twoHoursAgo);
        syncLogRepository.save(log2);

        // Act
        List<SyncLog> logsInRange = syncLogRepository.findByTimeRange(twoHoursAgo.minus(1, ChronoUnit.MINUTES), now);

        // Assert
        assertThat(logsInRange).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldCheckIfSuccessfulSyncExists() {
        // Arrange
        SyncLog successLog = new SyncLog("opportunities", "SUCCESS");
        syncLogRepository.save(successLog);

        // Act
        boolean exists = syncLogRepository.existsSuccessfulSyncBySyncType("opportunities");
        boolean notExists = syncLogRepository.existsSuccessfulSyncBySyncType("nonexistent_type");

        // Assert
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void shouldUpdateCompletedAtAndStatus() {
        // Arrange
        SyncLog log = new SyncLog("awards", "running");
        log.setStartedAt(Instant.now());
        SyncLog saved = syncLogRepository.save(log);

        assertThat(saved.getCompletedAt()).isNull();

        // Act
        saved.setStatus("success");
        saved.setCompletedAt(Instant.now());
        saved.setRecordsProcessed(500);
        SyncLog updated = syncLogRepository.save(saved);

        // Assert
        assertThat(updated.getStatus()).isEqualTo("success");
        assertThat(updated.getCompletedAt()).isNotNull();
        assertThat(updated.getRecordsProcessed()).isEqualTo(500);
    }
}
