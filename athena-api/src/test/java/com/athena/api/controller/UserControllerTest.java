package com.athena.api.controller;

import com.athena.api.AbstractControllerTest;
import com.athena.core.dto.UserCreateDTO;
import com.athena.core.dto.UserResponseDTO;
import com.athena.core.dto.UserUpdateDTO;
import com.athena.core.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UserController.
 * Tests all 7 endpoints with MockMvc.
 */
@WebMvcTest(
    controllers = UserController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
    }
)
class UserControllerTest extends AbstractControllerTest {

    @MockBean
    private UserService userService;

    @Test
    void findAll_ShouldReturnPageOfUsers() throws Exception {
        // Given
        Instant now = Instant.now();
        UserResponseDTO user1 = new UserResponseDTO(
                UUID.randomUUID(), "user1@example.com", "user1",
                "John", "Doe", true, false, now, now, now);
        UserResponseDTO user2 = new UserResponseDTO(
                UUID.randomUUID(), "user2@example.com", "user2",
                "Jane", "Smith", true, true, now, now, now);
        Page<UserResponseDTO> page = new PageImpl<>(List.of(user1, user2), PageRequest.of(0, 10), 2);
        when(userService.findAll(any(Pageable.class))).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].email").value("user1@example.com"))
                .andExpect(jsonPath("$.content[1].email").value("user2@example.com"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void findById_WhenUserExists_ShouldReturnUser() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        Instant now = Instant.now();
        UserResponseDTO user = new UserResponseDTO(
                userId, "user@example.com", "testuser",
                "John", "Doe", true, false, now, now, now);
        when(userService.findById(userId)).thenReturn(Optional.of(user));

        // When/Then
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void findById_WhenUserNotFound_ShouldReturn404() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        when(userService.findById(userId)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_WithValidData_ShouldReturnCreatedUser() throws Exception {
        // Given
        UserCreateDTO createDTO = new UserCreateDTO(
                "newuser@example.com", "newuser", "password123",
                "John", "Doe", false);
        Instant now = Instant.now();
        UserResponseDTO createdUser = new UserResponseDTO(
                UUID.randomUUID(), "newuser@example.com", "newuser",
                "John", "Doe", true, false, null, now, now);
        when(userService.create(any(UserCreateDTO.class))).thenReturn(createdUser);

        // When/Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    void create_WithInvalidEmail_ShouldReturn400() throws Exception {
        // Given - invalid email
        UserCreateDTO invalidDTO = new UserCreateDTO(
                "invalid-email", "username", "password123",
                "John", "Doe", false);

        // When/Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithShortPassword_ShouldReturn400() throws Exception {
        // Given - password too short
        UserCreateDTO invalidDTO = new UserCreateDTO(
                "user@example.com", "username", "short",
                "John", "Doe", false);

        // When/Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_WithValidData_ShouldReturnUpdatedUser() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        UserUpdateDTO updateDTO = new UserUpdateDTO("user@example.com", "testuser", "John", "Updated", true, false);
        Instant now = Instant.now();
        UserResponseDTO updatedUser = new UserResponseDTO(
                userId, "user@example.com", "testuser",
                "John", "Updated", true, false, now, now, now);
        when(userService.update(eq(userId), any(UserUpdateDTO.class))).thenReturn(updatedUser);

        // When/Then
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lastName").value("Updated"));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();

        // When/Then
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService).delete(userId);
    }

    @Test
    void findByEmail_WhenUserExists_ShouldReturnUser() throws Exception {
        // Given
        String email = "test@example.com";
        Instant now = Instant.now();
        UserResponseDTO user = new UserResponseDTO(
                UUID.randomUUID(), email, "testuser",
                "John", "Doe", true, false, now, now, now);
        when(userService.findByEmail(email)).thenReturn(Optional.of(user));

        // When/Then
        mockMvc.perform(get("/api/users/email/{email}", email))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void findByEmail_WhenUserNotFound_ShouldReturn404() throws Exception {
        // Given
        String email = "notfound@example.com";
        when(userService.findByEmail(email)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/users/email/{email}", email))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() throws Exception {
        // Given
        String username = "testuser";
        Instant now = Instant.now();
        UserResponseDTO user = new UserResponseDTO(
                UUID.randomUUID(), "user@example.com", username,
                "John", "Doe", true, false, now, now, now);
        when(userService.findByUsername(username)).thenReturn(Optional.of(user));

        // When/Then
        mockMvc.perform(get("/api/users/username/{username}", username))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    void findByUsername_WhenUserNotFound_ShouldReturn404() throws Exception {
        // Given
        String username = "notfound";
        when(userService.findByUsername(username)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/users/username/{username}", username))
                .andExpect(status().isNotFound());
    }
}
