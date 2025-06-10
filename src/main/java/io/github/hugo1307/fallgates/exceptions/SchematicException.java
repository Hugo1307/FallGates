package io.github.hugo1307.fallgates.exceptions;

public class SchematicException extends RuntimeException {
    public SchematicException(String message) {
        super(message);
    }

    public SchematicException(String message, Throwable cause) {
        super(message, cause);
    }
}
