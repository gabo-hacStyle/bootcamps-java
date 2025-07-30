package gabs.reports.domain.exception;

public class PersonaNotFoundException extends RuntimeException {
    
    public PersonaNotFoundException(String message) {
        super(message);
    }
    
    public PersonaNotFoundException(Long personaId) {
        super("Persona no encontrada con ID: " + personaId);
    }
} 