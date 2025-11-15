package com.athena.core.repository;

import com.athena.core.AbstractIntegrationTest;
import com.athena.core.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for UserRepository using Testcontainers.
 */
class UserRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndRetrieveUser() {
        // Given
        User user = new User("test@example.com", "testuser", "hashedpassword123");
        user.setFirstName("Test");
        user.setLastName("User");

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getFirstName()).isEqualTo("Test");
        assertThat(savedUser.getLastName()).isEqualTo("User");
        assertThat(savedUser.getIsActive()).isTrue();
        assertThat(savedUser.getIsAdmin()).isFalse();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindUserByEmail() {
        // Given
        User user = new User("findme@example.com", "finduser", "password");
        userRepository.save(user);

        // When
        Optional<User> found = userRepository.findByEmail("findme@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("findme@example.com");
    }

    @Test
    void shouldFindUserByUsername() {
        // Given
        User user = new User("user@example.com", "uniqueuser", "password");
        userRepository.save(user);

        // When
        Optional<User> found = userRepository.findByUsername("uniqueuser");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("uniqueuser");
    }

    @Test
    void shouldCheckIfEmailExists() {
        // Given
        User user = new User("exists@example.com", "existsuser", "password");
        userRepository.save(user);

        // When
        boolean exists = userRepository.existsByEmail("exists@example.com");
        boolean notExists = userRepository.existsByEmail("notfound@example.com");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void shouldCheckIfUsernameExists() {
        // Given
        User user = new User("user@example.com", "checkuser", "password");
        userRepository.save(user);

        // When
        boolean exists = userRepository.existsByUsername("checkuser");
        boolean notExists = userRepository.existsByUsername("notfounduser");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
