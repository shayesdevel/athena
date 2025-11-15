package com.athena.core.repository;

import com.athena.core.TestContainersConfiguration;
import com.athena.core.entity.SetAside;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for SetAsideRepository using Testcontainers.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainersConfiguration.class)
@org.springframework.test.context.ActiveProfiles("test")
class SetAsideRepositoryTest {

    @Autowired
    private SetAsideRepository setAsideRepository;

    @Test
    void shouldSaveAndRetrieveSetAside() {
        // Given
        SetAside setAside = new SetAside("SBA", "Small Business Set-Aside");
        setAside.setDescription("Set-aside for small business concerns");
        setAside.setEligibilityCriteria("SBA certified");

        // When
        SetAside saved = setAsideRepository.save(setAside);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCode()).isEqualTo("SBA");
        assertThat(saved.getName()).isEqualTo("Small Business Set-Aside");
        assertThat(saved.getDescription()).isEqualTo("Set-aside for small business concerns");
        assertThat(saved.getEligibilityCriteria()).isEqualTo("SBA certified");
        assertThat(saved.getIsActive()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindSetAsideByCode() {
        // Given
        SetAside setAside = new SetAside("8A", "8(a) Business Development");
        setAsideRepository.save(setAside);

        // When
        Optional<SetAside> found = setAsideRepository.findByCode("8A");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("8A");
        assertThat(found.get().getName()).isEqualTo("8(a) Business Development");
    }

    @Test
    void shouldFindActiveSetAsides() {
        // Given
        SetAside active1 = new SetAside("HUBZ", "HUBZone Set-Aside");
        SetAside active2 = new SetAside("WOSB", "Woman-Owned Small Business");
        SetAside inactive = new SetAside("OLD", "Obsolete Set-Aside");
        inactive.setIsActive(false);

        setAsideRepository.save(active1);
        setAsideRepository.save(active2);
        setAsideRepository.save(inactive);

        // When
        List<SetAside> activeSetAsides = setAsideRepository.findByIsActiveTrue();

        // Then
        assertThat(activeSetAsides).hasSize(2);
        assertThat(activeSetAsides).extracting(SetAside::getCode)
            .containsExactlyInAnyOrder("HUBZ", "WOSB");
    }

    @Test
    void shouldCheckIfCodeExists() {
        // Given
        SetAside setAside = new SetAside("VOSB", "Veteran-Owned Small Business");
        setAsideRepository.save(setAside);

        // When
        boolean exists = setAsideRepository.existsByCode("VOSB");
        boolean notExists = setAsideRepository.existsByCode("NONEXISTENT");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
