package com.athena.core.repository;

import com.athena.core.entity.NoticeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for NoticeType entity operations.
 * Provides CRUD operations and custom queries for notice type management.
 */
@Repository
public interface NoticeTypeRepository extends JpaRepository<NoticeType, UUID> {

    /**
     * Find notice type by code.
     *
     * @param code the notice type code to search for
     * @return Optional containing the notice type if found
     */
    Optional<NoticeType> findByCode(String code);

    /**
     * Find all active notice types.
     *
     * @return list of active notice types
     */
    List<NoticeType> findByIsActiveTrue();

    /**
     * Find notice types by category.
     *
     * @param category the category to filter by
     * @return list of notice types in the specified category
     */
    List<NoticeType> findByCategory(String category);

    /**
     * Check if notice type code exists.
     *
     * @param code the code to check
     * @return true if code exists, false otherwise
     */
    boolean existsByCode(String code);
}
