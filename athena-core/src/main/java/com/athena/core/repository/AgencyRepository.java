package com.athena.core.repository;

import com.athena.core.entity.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Agency entity operations.
 * Provides CRUD operations and custom queries for federal agency management.
 */
@Repository
public interface AgencyRepository extends JpaRepository<Agency, UUID> {

    /**
     * Find agency by abbreviation.
     *
     * @param abbreviation the agency abbreviation to search for
     * @return Optional containing the agency if found
     */
    Optional<Agency> findByAbbreviation(String abbreviation);

    /**
     * Find agencies by name (case-insensitive partial match).
     *
     * @param name the name pattern to search for
     * @return List of matching agencies
     */
    @Query("SELECT a FROM Agency a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Agency> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Find all active agencies.
     *
     * @return List of active agencies
     */
    List<Agency> findByIsActiveTrue();

    /**
     * Find sub-agencies by parent agency ID.
     *
     * @param parentAgencyId the parent agency UUID
     * @return List of sub-agencies
     */
    List<Agency> findByParentAgencyId(UUID parentAgencyId);

    /**
     * Find agencies by department.
     *
     * @param department the department name to search for
     * @return List of agencies in the department
     */
    List<Agency> findByDepartment(String department);
}
