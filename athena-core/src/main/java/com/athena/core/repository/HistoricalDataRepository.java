package com.athena.core.repository;

import com.athena.core.entity.HistoricalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for HistoricalData entity.
 * Provides CRUD operations and custom queries for historical snapshots.
 */
@Repository
public interface HistoricalDataRepository extends JpaRepository<HistoricalData, UUID> {

    /**
     * Find all historical data for a specific entity.
     *
     * @param entityType the entity type
     * @param entityId the entity's ID
     * @return list of historical data records
     */
    List<HistoricalData> findByEntityTypeAndEntityId(String entityType, UUID entityId);

    /**
     * Find historical data by entity type, entity ID, and data type.
     *
     * @param entityType the entity type
     * @param entityId the entity's ID
     * @param dataType the data type
     * @return list of historical data records
     */
    List<HistoricalData> findByEntityTypeAndEntityIdAndDataType(String entityType, UUID entityId, String dataType);

    /**
     * Find historical data within a time range.
     *
     * @param entityType the entity type
     * @param entityId the entity's ID
     * @param startTime the start time
     * @param endTime the end time
     * @return list of historical data records
     */
    @Query("SELECT hd FROM HistoricalData hd WHERE hd.entityType = :entityType " +
           "AND hd.entityId = :entityId AND hd.capturedAt BETWEEN :startTime AND :endTime " +
           "ORDER BY hd.capturedAt ASC")
    List<HistoricalData> findByEntityAndTimeRange(
            @Param("entityType") String entityType,
            @Param("entityId") UUID entityId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime);

    /**
     * Check if historical data exists for an entity.
     *
     * @param entityType the entity type
     * @param entityId the entity's ID
     * @return true if historical data exists, false otherwise
     */
    boolean existsByEntityTypeAndEntityId(String entityType, UUID entityId);
}
