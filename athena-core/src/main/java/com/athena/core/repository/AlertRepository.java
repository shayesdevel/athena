package com.athena.core.repository;

import com.athena.core.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Alert entity.
 * Provides CRUD operations and custom queries for user alerts.
 */
@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {

    /**
     * Find all alerts for a specific user.
     *
     * @param userId the user's ID
     * @return list of alerts
     */
    List<Alert> findByUserId(UUID userId);

    /**
     * Find active alerts for a specific user.
     *
     * @param userId the user's ID
     * @param isActive the active status
     * @return list of active alerts
     */
    List<Alert> findByUserIdAndIsActive(UUID userId, Boolean isActive);

    /**
     * Find alerts by alert type.
     *
     * @param alertType the alert type
     * @return list of alerts
     */
    List<Alert> findByAlertType(String alertType);

    /**
     * Find active alerts by type.
     *
     * @param alertType the alert type
     * @param isActive the active status
     * @return list of active alerts
     */
    List<Alert> findByAlertTypeAndIsActive(String alertType, Boolean isActive);

    /**
     * Check if active alerts exist for a user.
     *
     * @param userId the user's ID
     * @param isActive the active status
     * @return true if active alerts exist, false otherwise
     */
    boolean existsByUserIdAndIsActive(UUID userId, Boolean isActive);
}
