package gabs.personas.domain.exception;

public class InvalidPersonaDataException extends RuntimeException {
    
    public InvalidPersonaDataException(String message) {
        super(message);
    }
    
    public InvalidPersonaDataException(String field, String value) {
        super("Dato inválido para el campo '" + field + "': " + value);
    }
} 