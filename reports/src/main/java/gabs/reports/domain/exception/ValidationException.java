package gabs.reports.domain.exception;

public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String field, String message) {
        super("Error de validación en campo '" + field + "': " + message);
    }
} 