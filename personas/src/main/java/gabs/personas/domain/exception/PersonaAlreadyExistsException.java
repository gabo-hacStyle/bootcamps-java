package gabs.personas.domain.exception;

public class PersonaAlreadyExistsException extends RuntimeException {
    
    public PersonaAlreadyExistsException(String correo) {
        super("Ya existe una persona con el correo: " + correo);
    }
} 