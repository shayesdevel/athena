package com.athena.core.repository;

import com.athena.core.entity.SetAside;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for SetAside entity operations.
 * Provides CRUD operations and custom queries for set-aside management.
 */
@Repository
public interface SetAsideRepository extends JpaRepository<SetAside, UUID> {

    /**
     * Find set-aside by code.
     *
     * @param code the set-aside code to search for
     * @return Optional containing the set-aside if found
     */
    Optional<SetAside> findByCode(String code);

    /**
     * Find all active set-asides.
     *
     * @return list of active set-asides
     */
    List<SetAside> findByIsActiveTrue();

    /**
     * Check if set-aside code exists.
     *
     * @param code the code to check
     * @return true if code exists, false otherwise
     */
    boolean existsByCode(String code);
}
