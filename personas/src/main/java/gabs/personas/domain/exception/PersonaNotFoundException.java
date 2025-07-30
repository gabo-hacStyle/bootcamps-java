package gabs.personas.domain.exception;

public class PersonaNotFoundException extends RuntimeException {
    
    public PersonaNotFoundException(String message) {
        super(message);
    }
    
    public PersonaNotFoundException(Long id) {
        super("Persona con ID " + id + " no encontrada");
    }
} 