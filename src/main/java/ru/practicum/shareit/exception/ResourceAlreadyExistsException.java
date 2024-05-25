package ru.practicum.shareit.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException() {

    }

    public ResourceAlreadyExistsException(final String message) {
        super(message);
    }

    public ResourceAlreadyExistsException(final String message, Throwable cause) {
        super(message, cause);
    }
}
