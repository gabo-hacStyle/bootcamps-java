package gabs.bootcamps.domain.exception;

import org.springframework.http.HttpStatus;

public class ExternalServiceException extends BootcampException {
    
    public ExternalServiceException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE, "EXTERNAL_SERVICE_ERROR");
    }
    
    public ExternalServiceException(String serviceName, String message) {
        super("Error en servicio " + serviceName + ": " + message, HttpStatus.SERVICE_UNAVAILABLE, "EXTERNAL_SERVICE_ERROR");
    }
    
    public static ExternalServiceException capacidadesServiceError(String message) {
        return new ExternalServiceException("Capacidades", message);
    }
    
    public static ExternalServiceException reportsServiceError(String message) {
        return new ExternalServiceException("Reports", message);
    }
} 