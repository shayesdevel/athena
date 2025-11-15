package com.athena.api.controller;

import com.athena.core.dto.UserCreateDTO;
import com.athena.core.dto.UserResponseDTO;
import com.athena.core.dto.UserUpdateDTO;
import com.athena.core.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for User entity operations.
 * Handles authentication credentials and user profile management.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve paginated list of all users")
    public ResponseEntity<Page<UserResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by UUID")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable UUID id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create user", description = "Create a new user account")
    public ResponseEntity<UserResponseDTO> create(@RequestBody @Valid UserCreateDTO createDTO) {
        UserResponseDTO created = userService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update an existing user account")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid UserUpdateDTO updateDTO) {
        UserResponseDTO updated = userService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Soft delete a user (sets isActive to false)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Find user by email", description = "Retrieve user by email address")
    public ResponseEntity<UserResponseDTO> findByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Find user by username", description = "Retrieve user by username")
    public ResponseEntity<UserResponseDTO> findByUsername(@PathVariable String username) {
        return userService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
