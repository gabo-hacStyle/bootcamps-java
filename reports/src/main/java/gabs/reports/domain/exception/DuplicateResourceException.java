package gabs.reports.domain.exception;

public class DuplicateResourceException extends RuntimeException {
    
    public DuplicateResourceException(String message) {
        super(message);
    }
    
    public DuplicateResourceException(String resourceType, String identifier) {
        super(resourceType + " ya existe con identificador: " + identifier);
    }
} 