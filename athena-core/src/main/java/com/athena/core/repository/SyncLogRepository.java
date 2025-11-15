package com.athena.core.repository;

import com.athena.core.entity.SyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for SyncLog entity.
 * Provides CRUD operations and custom queries for sync operation tracking.
 */
@Repository
public interface SyncLogRepository extends JpaRepository<SyncLog, UUID> {

    /**
     * Find sync logs by sync type.
     *
     * @param syncType the sync type
     * @return list of sync logs
     */
    List<SyncLog> findBySyncType(String syncType);

    /**
     * Find sync logs by status.
     *
     * @param status the sync status
     * @return list of sync logs
     */
    List<SyncLog> findByStatus(String status);

    /**
     * Find sync logs by sync type and status.
     *
     * @param syncType the sync type
     * @param status the sync status
     * @return list of sync logs
     */
    List<SyncLog> findBySyncTypeAndStatus(String syncType, String status);

    /**
     * Find the most recent sync log for a specific type.
     *
     * @param syncType the sync type
     * @return optional sync log
     */
    @Query("SELECT sl FROM SyncLog sl WHERE sl.syncType = :syncType " +
           "ORDER BY sl.startedAt DESC LIMIT 1")
    Optional<SyncLog> findLatestBySyncType(@Param("syncType") String syncType);

    /**
     * Find sync logs within a time range.
     *
     * @param startTime the start time
     * @param endTime the end time
     * @return list of sync logs
     */
    @Query("SELECT sl FROM SyncLog sl WHERE sl.startedAt BETWEEN :startTime AND :endTime " +
           "ORDER BY sl.startedAt DESC")
    List<SyncLog> findByTimeRange(
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime);

    /**
     * Check if any successful syncs exist for a type.
     *
     * @param syncType the sync type
     * @return true if successful syncs exist, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(sl) > 0 THEN true ELSE false END FROM SyncLog sl " +
           "WHERE sl.syncType = :syncType AND sl.status = 'SUCCESS'")
    boolean existsSuccessfulSyncBySyncType(@Param("syncType") String syncType);
}
