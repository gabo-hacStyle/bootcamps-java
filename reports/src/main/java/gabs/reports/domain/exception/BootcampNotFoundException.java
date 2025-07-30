package gabs.reports.domain.exception;

public class BootcampNotFoundException extends RuntimeException {
    
    public BootcampNotFoundException(String message) {
        super(message);
    }
    
    public BootcampNotFoundException(Long bootcampId) {
        super("Bootcamp no encontrado con ID: " + bootcampId);
    }
} 