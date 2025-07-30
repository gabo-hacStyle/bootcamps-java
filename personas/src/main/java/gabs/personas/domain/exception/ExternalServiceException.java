package gabs.personas.domain.exception;

public class ExternalServiceException extends RuntimeException {
    
    public ExternalServiceException(String message) {
        super(message);
    }
    
    public ExternalServiceException(String service, String message) {
        super("Error en el servicio " + service + ": " + message);
    }
    
    public ExternalServiceException(String service, Throwable cause) {
        super("Error en el servicio " + service + ": " + cause.getMessage(), cause);
    }
} 