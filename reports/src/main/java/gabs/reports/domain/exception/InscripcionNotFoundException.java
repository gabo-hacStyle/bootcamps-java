package gabs.reports.domain.exception;

public class InscripcionNotFoundException extends RuntimeException {
    
    public InscripcionNotFoundException(String message) {
        super(message);
    }
    
    public InscripcionNotFoundException(Long inscripcionId) {
        super("Inscripción no encontrada con ID: " + inscripcionId);
    }
} 