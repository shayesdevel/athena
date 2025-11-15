package com.athena.core.exception;

import java.util.UUID;

/**
 * Exception thrown when a requested entity is not found.
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entityName, UUID id) {
        super(String.format("%s not found with id: %s", entityName, id));
    }

    public EntityNotFoundException(String entityName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: %s", entityName, fieldName, fieldValue));
    }
}
