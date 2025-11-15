package com.athena.core.service;

import com.athena.core.dto.UserCreateDTO;
import com.athena.core.dto.UserResponseDTO;
import com.athena.core.dto.UserUpdateDTO;
import com.athena.core.entity.User;
import com.athena.core.exception.DuplicateEntityException;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = new User("test@example.com", "testuser", "hashedPassword");
        testUser.setId(testUserId);
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setIsActive(true);
        testUser.setIsAdmin(false);
        testUser.setCreatedAt(Instant.now());
        testUser.setUpdatedAt(Instant.now());
    }

    @Test
    void create_ShouldCreateUser_WhenValidData() {
        // Given
        UserCreateDTO dto = new UserCreateDTO(
            "newuser@example.com",
            "newuser",
            "password123",
            "New",
            "User",
            false
        );

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(userRepository.existsByUsername(dto.username())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserResponseDTO result = userService.create(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(testUser.getEmail());
        verify(userRepository).existsByEmail(dto.email());
        verify(userRepository).existsByUsername(dto.username());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void create_ShouldThrowException_WhenEmailExists() {
        // Given
        UserCreateDTO dto = new UserCreateDTO(
            "existing@example.com",
            "newuser",
            "password123",
            null,
            null,
            false
        );

        when(userRepository.existsByEmail(dto.email())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.create(dto))
            .isInstanceOf(DuplicateEntityException.class)
            .hasMessageContaining("email");

        verify(userRepository).existsByEmail(dto.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void create_ShouldThrowException_WhenUsernameExists() {
        // Given
        UserCreateDTO dto = new UserCreateDTO(
            "newuser@example.com",
            "existinguser",
            "password123",
            null,
            null,
            false
        );

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(userRepository.existsByUsername(dto.username())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.create(dto))
            .isInstanceOf(DuplicateEntityException.class)
            .hasMessageContaining("username");

        verify(userRepository).existsByEmail(dto.email());
        verify(userRepository).existsByUsername(dto.username());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findById_ShouldReturnUser_WhenExists() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When
        Optional<UserResponseDTO> result = userService.findById(testUserId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(testUserId);
        verify(userRepository).findById(testUserId);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<UserResponseDTO> result = userService.findById(nonExistentId);

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findById(nonExistentId);
    }

    @Test
    void findAll_ShouldReturnPageOfUsers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(Arrays.asList(testUser));
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // When
        Page<UserResponseDTO> result = userService.findAll(pageable);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).id()).isEqualTo(testUserId);
        verify(userRepository).findAll(pageable);
    }

    @Test
    void update_ShouldUpdateUser_WhenValidData() {
        // Given
        UserUpdateDTO dto = new UserUpdateDTO(
            null,
            null,
            "Updated",
            "Name",
            null,
            null
        );

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserResponseDTO result = userService.update(testUserId, dto);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).findById(testUserId);
        verify(userRepository).save(testUser);
    }

    @Test
    void update_ShouldThrowException_WhenUserNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        UserUpdateDTO dto = new UserUpdateDTO(null, null, "Test", "User", null, null);

        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.update(nonExistentId, dto))
            .isInstanceOf(EntityNotFoundException.class);

        verify(userRepository).findById(nonExistentId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void delete_ShouldSoftDeleteUser_WhenExists() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.delete(testUserId);

        // Then
        verify(userRepository).findById(testUserId);
        verify(userRepository).save(testUser);
        assertThat(testUser.getIsActive()).isFalse();
    }

    @Test
    void delete_ShouldThrowException_WhenUserNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.delete(nonExistentId))
            .isInstanceOf(EntityNotFoundException.class);

        verify(userRepository).findById(nonExistentId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenExists() {
        // Given
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // When
        Optional<UserResponseDTO> result = userService.findByEmail(email);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().email()).isEqualTo(email);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenExists() {
        // Given
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When
        boolean result = userService.existsByEmail(email);

        // Then
        assertThat(result).isTrue();
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void updateLastLogin_ShouldUpdateTimestamp() {
        // Given
        Instant loginTime = Instant.now();
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.updateLastLogin(testUserId, loginTime);

        // Then
        verify(userRepository).findById(testUserId);
        verify(userRepository).save(testUser);
        assertThat(testUser.getLastLoginAt()).isEqualTo(loginTime);
    }
}
