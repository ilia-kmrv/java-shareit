package ru.practicum.shareit.exception;

public class ResourceValidationException extends RuntimeException {
    public ResourceValidationException() {

    }

    public ResourceValidationException(final String message) {
        super(message);
    }

    public ResourceValidationException(final String message, Throwable cause) {
        super(message, cause);
    }
}
