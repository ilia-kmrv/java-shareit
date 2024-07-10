package ru.practicum.shareit.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {

    }

    public ResourceNotFoundException(final String message) {
        super(message);
    }

    public ResourceNotFoundException(final String message, Throwable cause) {
        super(message, cause);
    }
}
