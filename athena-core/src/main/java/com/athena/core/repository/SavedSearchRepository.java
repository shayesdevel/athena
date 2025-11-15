package com.athena.core.repository;

import com.athena.core.entity.SavedSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for SavedSearch entity.
 * Provides CRUD operations and custom queries for saved searches.
 */
@Repository
public interface SavedSearchRepository extends JpaRepository<SavedSearch, UUID> {

    /**
     * Find all saved searches for a specific user.
     *
     * @param userId the user's ID
     * @return list of saved searches
     */
    List<SavedSearch> findByUserId(UUID userId);

    /**
     * Find active saved searches for a specific user.
     *
     * @param userId the user's ID
     * @param isActive the active status
     * @return list of active saved searches
     */
    List<SavedSearch> findByUserIdAndIsActive(UUID userId, Boolean isActive);

    /**
     * Find a saved search by user ID and search name.
     *
     * @param userId the user's ID
     * @param searchName the search name
     * @return optional saved search
     */
    Optional<SavedSearch> findByUserIdAndSearchName(UUID userId, String searchName);

    /**
     * Check if a saved search exists for a user with a specific name.
     *
     * @param userId the user's ID
     * @param searchName the search name
     * @return true if exists, false otherwise
     */
    boolean existsByUserIdAndSearchName(UUID userId, String searchName);
}
