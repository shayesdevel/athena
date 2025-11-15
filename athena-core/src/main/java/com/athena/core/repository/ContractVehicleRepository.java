package com.athena.core.repository;

import com.athena.core.entity.ContractVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ContractVehicle entity operations.
 * Provides CRUD operations and custom queries for contract vehicle management.
 */
@Repository
public interface ContractVehicleRepository extends JpaRepository<ContractVehicle, UUID> {

    /**
     * Find contract vehicle by code.
     *
     * @param code the contract vehicle code to search for
     * @return Optional containing the contract vehicle if found
     */
    Optional<ContractVehicle> findByCode(String code);

    /**
     * Find contract vehicles by category.
     *
     * @param category the category to filter by (e.g., "Schedule", "IDIQ")
     * @return list of contract vehicles in the specified category
     */
    List<ContractVehicle> findByCategory(String category);

    /**
     * Find contract vehicles by managing agency.
     *
     * @param managingAgency the managing agency (e.g., "GSA", "VA")
     * @return list of contract vehicles managed by the specified agency
     */
    List<ContractVehicle> findByManagingAgency(String managingAgency);

    /**
     * Find all active contract vehicles.
     *
     * @return list of active contract vehicles
     */
    List<ContractVehicle> findByIsActiveTrue();

    /**
     * Check if contract vehicle code exists.
     *
     * @param code the code to check
     * @return true if code exists, false otherwise
     */
    boolean existsByCode(String code);
}
