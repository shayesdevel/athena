package com.athena.core.repository;

import com.athena.core.TestContainersConfiguration;
import com.athena.core.entity.NoticeType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for NoticeTypeRepository using Testcontainers.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainersConfiguration.class)
@org.springframework.test.context.ActiveProfiles("test")
class NoticeTypeRepositoryTest {

    @Autowired
    private NoticeTypeRepository noticeTypeRepository;

    @Test
    void shouldSaveAndRetrieveNoticeType() {
        // Given
        NoticeType noticeType = new NoticeType("PRESOL", "Presolicitation");
        noticeType.setDescription("Notice of intent to solicit");
        noticeType.setCategory("Pre-Award");

        // When
        NoticeType saved = noticeTypeRepository.save(noticeType);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCode()).isEqualTo("PRESOL");
        assertThat(saved.getName()).isEqualTo("Presolicitation");
        assertThat(saved.getDescription()).isEqualTo("Notice of intent to solicit");
        assertThat(saved.getCategory()).isEqualTo("Pre-Award");
        assertThat(saved.getIsActive()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindNoticeTypeByCode() {
        // Given
        NoticeType noticeType = new NoticeType("COMBINE", "Combined Synopsis/Solicitation");
        noticeTypeRepository.save(noticeType);

        // When
        Optional<NoticeType> found = noticeTypeRepository.findByCode("COMBINE");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("COMBINE");
        assertThat(found.get().getName()).isEqualTo("Combined Synopsis/Solicitation");
    }

    @Test
    void shouldFindActiveNoticeTypes() {
        // Given
        NoticeType active1 = new NoticeType("AWARD", "Award Notice");
        NoticeType active2 = new NoticeType("SNOTE", "Special Notice");
        NoticeType inactive = new NoticeType("OLD", "Obsolete Type");
        inactive.setIsActive(false);

        noticeTypeRepository.save(active1);
        noticeTypeRepository.save(active2);
        noticeTypeRepository.save(inactive);

        // When
        List<NoticeType> activeTypes = noticeTypeRepository.findByIsActiveTrue();

        // Then
        assertThat(activeTypes).hasSize(2);
        assertThat(activeTypes).extracting(NoticeType::getCode)
            .containsExactlyInAnyOrder("AWARD", "SNOTE");
    }

    @Test
    void shouldFindNoticeTypesByCategory() {
        // Given
        NoticeType preAward1 = new NoticeType("PRESOL", "Presolicitation");
        preAward1.setCategory("Pre-Award");
        NoticeType preAward2 = new NoticeType("COMBINE", "Combined Synopsis/Solicitation");
        preAward2.setCategory("Pre-Award");
        NoticeType postAward = new NoticeType("AWARD", "Award Notice");
        postAward.setCategory("Post-Award");

        noticeTypeRepository.save(preAward1);
        noticeTypeRepository.save(preAward2);
        noticeTypeRepository.save(postAward);

        // When
        List<NoticeType> preAwardTypes = noticeTypeRepository.findByCategory("Pre-Award");

        // Then
        assertThat(preAwardTypes).hasSize(2);
        assertThat(preAwardTypes).extracting(NoticeType::getCode)
            .containsExactlyInAnyOrder("PRESOL", "COMBINE");
    }

    @Test
    void shouldCheckIfCodeExists() {
        // Given
        NoticeType noticeType = new NoticeType("SNOTE", "Special Notice");
        noticeTypeRepository.save(noticeType);

        // When
        boolean exists = noticeTypeRepository.existsByCode("SNOTE");
        boolean notExists = noticeTypeRepository.existsByCode("NONEXISTENT");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
