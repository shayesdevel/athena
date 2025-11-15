package com.athena.core.repository;

import com.athena.core.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Organization entity operations.
 * Provides CRUD operations and custom queries for contractor organization management.
 */
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    /**
     * Find organization by UEI (Unique Entity Identifier).
     *
     * @param uei the UEI to search for
     * @return Optional containing the organization if found
     */
    Optional<Organization> findByUei(String uei);

    /**
     * Find organization by CAGE code.
     *
     * @param cageCode the CAGE code to search for
     * @return Optional containing the organization if found
     */
    Optional<Organization> findByCageCode(String cageCode);

    /**
     * Find organizations by name (case-insensitive partial match).
     *
     * @param name the name pattern to search for
     * @return List of matching organizations
     */
    @Query("SELECT o FROM Organization o WHERE LOWER(o.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Organization> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Find organizations by primary NAICS code.
     *
     * @param primaryNaics the NAICS code to search for
     * @return List of organizations with matching NAICS
     */
    List<Organization> findByPrimaryNaics(String primaryNaics);

    /**
     * Find small business organizations.
     *
     * @return List of organizations flagged as small business
     */
    List<Organization> findByIsSmallBusinessTrue();

    /**
     * Check if UEI already exists.
     *
     * @param uei the UEI to check
     * @return true if UEI exists, false otherwise
     */
    boolean existsByUei(String uei);
}
