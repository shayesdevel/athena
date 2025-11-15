package com.athena.core.repository;

import com.athena.core.AbstractIntegrationTest;
import com.athena.core.entity.ContractVehicle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for ContractVehicleRepository using Testcontainers.
 */




class ContractVehicleRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private ContractVehicleRepository contractVehicleRepository;

    @Test
    void shouldSaveAndRetrieveContractVehicle() {
        // Given
        ContractVehicle vehicle = new ContractVehicle("GSA_SCHED", "GSA Schedule");
        vehicle.setDescription("General Services Administration Schedule contracts");
        vehicle.setCategory("Schedule");
        vehicle.setManagingAgency("GSA");
        vehicle.setUrl("https://www.gsa.gov/schedules");

        // When
        ContractVehicle saved = contractVehicleRepository.save(vehicle);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCode()).isEqualTo("GSA_SCHED");
        assertThat(saved.getName()).isEqualTo("GSA Schedule");
        assertThat(saved.getDescription()).isEqualTo("General Services Administration Schedule contracts");
        assertThat(saved.getCategory()).isEqualTo("Schedule");
        assertThat(saved.getManagingAgency()).isEqualTo("GSA");
        assertThat(saved.getUrl()).isEqualTo("https://www.gsa.gov/schedules");
        assertThat(saved.getIsActive()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindContractVehicleByCode() {
        // Given
        ContractVehicle vehicle = new ContractVehicle("IDIQ", "Indefinite Delivery/Indefinite Quantity");
        contractVehicleRepository.save(vehicle);

        // When
        Optional<ContractVehicle> found = contractVehicleRepository.findByCode("IDIQ");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("IDIQ");
        assertThat(found.get().getName()).isEqualTo("Indefinite Delivery/Indefinite Quantity");
    }

    @Test
    void shouldFindContractVehiclesByCategory() {
        // Given
        ContractVehicle schedule1 = new ContractVehicle("GSA_SCHED", "GSA Schedule");
        schedule1.setCategory("Schedule");
        ContractVehicle schedule2 = new ContractVehicle("VA_SCHED", "VA Schedule");
        schedule2.setCategory("Schedule");
        ContractVehicle idiq = new ContractVehicle("IDIQ", "IDIQ");
        idiq.setCategory("IDIQ");

        contractVehicleRepository.save(schedule1);
        contractVehicleRepository.save(schedule2);
        contractVehicleRepository.save(idiq);

        // When
        List<ContractVehicle> schedules = contractVehicleRepository.findByCategory("Schedule");

        // Then
        assertThat(schedules).hasSize(2);
        assertThat(schedules).extracting(ContractVehicle::getCode)
            .containsExactlyInAnyOrder("GSA_SCHED", "VA_SCHED");
    }

    @Test
    void shouldFindContractVehiclesByManagingAgency() {
        // Given
        ContractVehicle gsa1 = new ContractVehicle("GSA_SCHED", "GSA Schedule");
        gsa1.setManagingAgency("GSA");
        ContractVehicle gsa2 = new ContractVehicle("GSA_GWAC", "GSA GWAC");
        gsa2.setManagingAgency("GSA");
        ContractVehicle va = new ContractVehicle("VA_IDIQ", "VA IDIQ");
        va.setManagingAgency("VA");

        contractVehicleRepository.save(gsa1);
        contractVehicleRepository.save(gsa2);
        contractVehicleRepository.save(va);

        // When
        List<ContractVehicle> gsaVehicles = contractVehicleRepository.findByManagingAgency("GSA");

        // Then
        assertThat(gsaVehicles).hasSize(2);
        assertThat(gsaVehicles).extracting(ContractVehicle::getCode)
            .containsExactlyInAnyOrder("GSA_SCHED", "GSA_GWAC");
    }

    @Test
    void shouldFindActiveContractVehicles() {
        // Given
        ContractVehicle active1 = new ContractVehicle("GSA_SCHED", "GSA Schedule");
        ContractVehicle active2 = new ContractVehicle("IDIQ", "IDIQ");
        ContractVehicle inactive = new ContractVehicle("OLD", "Obsolete Vehicle");
        inactive.setIsActive(false);

        contractVehicleRepository.save(active1);
        contractVehicleRepository.save(active2);
        contractVehicleRepository.save(inactive);

        // When
        List<ContractVehicle> activeVehicles = contractVehicleRepository.findByIsActiveTrue();

        // Then
        assertThat(activeVehicles).hasSize(2);
        assertThat(activeVehicles).extracting(ContractVehicle::getCode)
            .containsExactlyInAnyOrder("GSA_SCHED", "IDIQ");
    }

    @Test
    void shouldCheckIfCodeExists() {
        // Given
        ContractVehicle vehicle = new ContractVehicle("BPA", "Blanket Purchase Agreement");
        contractVehicleRepository.save(vehicle);

        // When
        boolean exists = contractVehicleRepository.existsByCode("BPA");
        boolean notExists = contractVehicleRepository.existsByCode("NONEXISTENT");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
