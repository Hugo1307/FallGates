package io.github.hugo1307.fallgates.exceptions;

public class SchematicReadException extends RuntimeException {
    public SchematicReadException(String message) {
        super(message);
    }

    public SchematicReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
