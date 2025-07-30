package gabs.capacidades.domain.exception;

public class BootcampNotFoundException extends RuntimeException {
    
    public BootcampNotFoundException(String message) {
        super(message);
    }
    
    public BootcampNotFoundException(Long id) {
        super("Bootcamp con ID " + id + " no encontrado");
    }
} 