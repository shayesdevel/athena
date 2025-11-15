package com.athena.core.exception;

/**
 * Exception thrown when attempting to create an entity that already exists.
 */
public class DuplicateEntityException extends RuntimeException {

    public DuplicateEntityException(String message) {
        super(message);
    }

    public DuplicateEntityException(String entityName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: %s", entityName, fieldName, fieldValue));
    }
}
