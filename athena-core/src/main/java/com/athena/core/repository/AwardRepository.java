package com.athena.core.repository;

import com.athena.core.entity.Award;
import com.athena.core.entity.Opportunity;
import com.athena.core.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Award entity operations.
 * Provides CRUD operations and custom queries for award management.
 */
@Repository
public interface AwardRepository extends JpaRepository<Award, UUID> {

    /**
     * Find award by contract number.
     *
     * @param contractNumber the contract number to search for
     * @return Optional containing the award if found
     */
    Optional<Award> findByContractNumber(String contractNumber);

    /**
     * Find awards by opportunity.
     *
     * @param opportunity the opportunity
     * @return list of awards for the opportunity
     */
    List<Award> findByOpportunity(Opportunity opportunity);

    /**
     * Find awards by awardee organization.
     *
     * @param organization the organization
     * @return list of awards won by the organization
     */
    List<Award> findByOrganization(Organization organization);

    /**
     * Find awards by awardee UEI.
     *
     * @param awardeeUei the awardee UEI
     * @return list of awards for the UEI
     */
    List<Award> findByAwardeeUei(String awardeeUei);

    /**
     * Find all active awards.
     *
     * @return list of active awards
     */
    List<Award> findByIsActiveTrue();

    /**
     * Find awards by date range.
     *
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of awards within the date range
     */
    @Query("SELECT a FROM Award a WHERE a.awardDate BETWEEN :startDate AND :endDate")
    List<Award> findByAwardDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find awards by NAICS code.
     *
     * @param naicsCode the NAICS code
     * @return list of awards in the specified NAICS code
     */
    List<Award> findByNaicsCode(String naicsCode);

    /**
     * Find awards by set-aside type.
     *
     * @param setAside the set-aside type
     * @return list of awards with the specified set-aside
     */
    List<Award> findBySetAside(String setAside);

    /**
     * Count awards for a specific organization.
     *
     * @param organization the organization
     * @return number of awards won by the organization
     */
    long countByOrganization(Organization organization);
}
