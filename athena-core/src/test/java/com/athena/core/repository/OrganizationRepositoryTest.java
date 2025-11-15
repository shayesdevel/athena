package com.athena.core.repository;

import com.athena.core.AbstractIntegrationTest;
import com.athena.core.entity.Organization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for OrganizationRepository using Testcontainers.
 */
class OrganizationRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    void shouldSaveAndRetrieveOrganization() {
        // Given
        Organization org = new Organization("Acme Corporation");
        org.setUei("ABC123456789");
        org.setCageCode("1A2B3");
        org.setPrimaryNaics("541512");
        org.setIsSmallBusiness(true);

        // When
        Organization savedOrg = organizationRepository.save(org);

        // Then
        assertThat(savedOrg.getId()).isNotNull();
        assertThat(savedOrg.getName()).isEqualTo("Acme Corporation");
        assertThat(savedOrg.getUei()).isEqualTo("ABC123456789");
        assertThat(savedOrg.getCageCode()).isEqualTo("1A2B3");
        assertThat(savedOrg.getPrimaryNaics()).isEqualTo("541512");
        assertThat(savedOrg.getIsSmallBusiness()).isTrue();
        assertThat(savedOrg.getCreatedAt()).isNotNull();
        assertThat(savedOrg.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindOrganizationByUei() {
        // Given
        Organization org = new Organization("Test Org");
        org.setUei("UEI12345678");
        organizationRepository.save(org);

        // When
        Optional<Organization> found = organizationRepository.findByUei("UEI12345678");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUei()).isEqualTo("UEI12345678");
    }

    @Test
    void shouldFindOrganizationByCageCode() {
        // Given
        Organization org = new Organization("Cage Test Org");
        org.setCageCode("CAGE1");
        organizationRepository.save(org);

        // When
        Optional<Organization> found = organizationRepository.findByCageCode("CAGE1");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCageCode()).isEqualTo("CAGE1");
    }

    @Test
    void shouldFindOrganizationsByNameContaining() {
        // Given
        organizationRepository.save(new Organization("Tech Solutions Inc"));
        organizationRepository.save(new Organization("Advanced Tech Corp"));
        organizationRepository.save(new Organization("Consulting Services LLC"));

        // When
        List<Organization> techOrgs = organizationRepository.findByNameContainingIgnoreCase("tech");

        // Then
        assertThat(techOrgs).hasSize(2);
        assertThat(techOrgs).extracting(Organization::getName)
                .contains("Tech Solutions Inc", "Advanced Tech Corp");
    }

    @Test
    void shouldFindOrganizationsByPrimaryNaics() {
        // Given
        Organization org1 = new Organization("IT Services 1");
        org1.setPrimaryNaics("541512");
        organizationRepository.save(org1);

        Organization org2 = new Organization("IT Services 2");
        org2.setPrimaryNaics("541512");
        organizationRepository.save(org2);

        // When
        List<Organization> naicsOrgs = organizationRepository.findByPrimaryNaics("541512");

        // Then
        assertThat(naicsOrgs).hasSize(2);
    }

    @Test
    void shouldFindSmallBusinessOrganizations() {
        // Given
        Organization smallBiz = new Organization("Small Business Inc");
        smallBiz.setIsSmallBusiness(true);
        organizationRepository.save(smallBiz);

        Organization largeBiz = new Organization("Large Corporation");
        largeBiz.setIsSmallBusiness(false);
        organizationRepository.save(largeBiz);

        // When
        List<Organization> smallBusinesses = organizationRepository.findByIsSmallBusinessTrue();

        // Then
        assertThat(smallBusinesses).hasSize(1);
        assertThat(smallBusinesses.get(0).getName()).isEqualTo("Small Business Inc");
    }
}
