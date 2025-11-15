package com.athena.core.repository;

import com.athena.core.entity.Naics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for NAICS entity operations.
 * Provides CRUD operations and custom queries for NAICS code management.
 */
@Repository
public interface NaicsRepository extends JpaRepository<Naics, UUID> {

    /**
     * Find NAICS by code.
     *
     * @param code the NAICS code to search for (e.g., "541512")
     * @return Optional containing the NAICS if found
     */
    Optional<Naics> findByCode(String code);

    /**
     * Find all NAICS codes by level.
     *
     * @param level the hierarchy level (2, 3, 4, 5, or 6)
     * @return list of NAICS codes at the specified level
     */
    List<Naics> findByLevel(Integer level);

    /**
     * Find all child NAICS codes under a parent code.
     *
     * @param parentCode the parent NAICS code
     * @return list of child NAICS codes
     */
    List<Naics> findByParentCode(String parentCode);

    /**
     * Find all active NAICS codes.
     *
     * @return list of active NAICS codes
     */
    List<Naics> findByIsActiveTrue();

    /**
     * Find NAICS codes by year version.
     *
     * @param yearVersion the NAICS version year (e.g., "2022")
     * @return list of NAICS codes from the specified version
     */
    List<Naics> findByYearVersion(String yearVersion);

    /**
     * Check if NAICS code exists.
     *
     * @param code the code to check
     * @return true if code exists, false otherwise
     */
    boolean existsByCode(String code);
}
