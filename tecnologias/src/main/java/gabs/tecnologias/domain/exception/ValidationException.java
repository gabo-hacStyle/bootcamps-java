package gabs.tecnologias.domain.exception;

/**
 * Excepción lanzada cuando hay errores de validación
 */
public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String field, String message) {
        super("Error de validación en el campo '" + field + "': " + message);
    }
} 