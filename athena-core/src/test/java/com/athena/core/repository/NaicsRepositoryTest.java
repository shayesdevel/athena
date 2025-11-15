package com.athena.core.repository;

import com.athena.core.TestContainersConfiguration;
import com.athena.core.entity.Naics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for NaicsRepository using Testcontainers.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainersConfiguration.class)
@org.springframework.test.context.ActiveProfiles("test")
class NaicsRepositoryTest {

    @Autowired
    private NaicsRepository naicsRepository;

    @Test
    void shouldSaveAndRetrieveNaics() {
        // Given
        Naics naics = new Naics("541512", "Computer Systems Design Services");
        naics.setDescription("Custom computer programming services");
        naics.setParentCode("54151");
        naics.setYearVersion("2022");

        // When
        Naics saved = naicsRepository.save(naics);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCode()).isEqualTo("541512");
        assertThat(saved.getTitle()).isEqualTo("Computer Systems Design Services");
        assertThat(saved.getDescription()).isEqualTo("Custom computer programming services");
        assertThat(saved.getParentCode()).isEqualTo("54151");
        assertThat(saved.getLevel()).isEqualTo(6); // Auto-calculated from code length
        assertThat(saved.getYearVersion()).isEqualTo("2022");
        assertThat(saved.getIsActive()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindNaicsByCode() {
        // Given
        Naics naics = new Naics("541511", "Custom Computer Programming Services");
        naicsRepository.save(naics);

        // When
        Optional<Naics> found = naicsRepository.findByCode("541511");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("541511");
        assertThat(found.get().getTitle()).isEqualTo("Custom Computer Programming Services");
    }

    @Test
    void shouldFindNaicsByLevel() {
        // Given
        Naics level2 = new Naics("54", "Professional, Scientific, and Technical Services");
        Naics level4 = new Naics("5415", "Computer Systems Design and Related Services");
        Naics level6a = new Naics("541511", "Custom Computer Programming Services");
        Naics level6b = new Naics("541512", "Computer Systems Design Services");

        naicsRepository.save(level2);
        naicsRepository.save(level4);
        naicsRepository.save(level6a);
        naicsRepository.save(level6b);

        // When
        List<Naics> level6Codes = naicsRepository.findByLevel(6);

        // Then
        assertThat(level6Codes).hasSize(2);
        assertThat(level6Codes).extracting(Naics::getCode)
            .containsExactlyInAnyOrder("541511", "541512");
    }

    @Test
    void shouldFindNaicsByParentCode() {
        // Given
        Naics parent = new Naics("5415", "Computer Systems Design and Related Services");
        Naics child1 = new Naics("54151", "Computer Systems Design and Related Services");
        child1.setParentCode("5415");
        Naics child2 = new Naics("54152", "Computer Facilities Management Services");
        child2.setParentCode("5415");

        naicsRepository.save(parent);
        naicsRepository.save(child1);
        naicsRepository.save(child2);

        // When
        List<Naics> children = naicsRepository.findByParentCode("5415");

        // Then
        assertThat(children).hasSize(2);
        assertThat(children).extracting(Naics::getCode)
            .containsExactlyInAnyOrder("54151", "54152");
    }

    @Test
    void shouldFindActiveNaicsCodes() {
        // Given
        Naics active1 = new Naics("541511", "Custom Computer Programming Services");
        Naics active2 = new Naics("541512", "Computer Systems Design Services");
        Naics inactive = new Naics("999999", "Obsolete Code");
        inactive.setIsActive(false);

        naicsRepository.save(active1);
        naicsRepository.save(active2);
        naicsRepository.save(inactive);

        // When
        List<Naics> activeCodes = naicsRepository.findByIsActiveTrue();

        // Then
        assertThat(activeCodes).hasSize(2);
        assertThat(activeCodes).extracting(Naics::getCode)
            .containsExactlyInAnyOrder("541511", "541512");
    }

    @Test
    void shouldFindNaicsByYearVersion() {
        // Given
        Naics naics2022a = new Naics("541511", "Custom Computer Programming Services");
        naics2022a.setYearVersion("2022");
        Naics naics2022b = new Naics("541512", "Computer Systems Design Services");
        naics2022b.setYearVersion("2022");
        Naics naics2017 = new Naics("999999", "Old Code");
        naics2017.setYearVersion("2017");

        naicsRepository.save(naics2022a);
        naicsRepository.save(naics2022b);
        naicsRepository.save(naics2017);

        // When
        List<Naics> codes2022 = naicsRepository.findByYearVersion("2022");

        // Then
        assertThat(codes2022).hasSize(2);
        assertThat(codes2022).extracting(Naics::getCode)
            .containsExactlyInAnyOrder("541511", "541512");
    }

    @Test
    void shouldCheckIfCodeExists() {
        // Given
        Naics naics = new Naics("541511", "Custom Computer Programming Services");
        naicsRepository.save(naics);

        // When
        boolean exists = naicsRepository.existsByCode("541511");
        boolean notExists = naicsRepository.existsByCode("999999");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
