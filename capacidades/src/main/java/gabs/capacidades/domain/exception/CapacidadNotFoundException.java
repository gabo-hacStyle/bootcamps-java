package gabs.capacidades.domain.exception;

public class CapacidadNotFoundException extends RuntimeException {
    
    public CapacidadNotFoundException(String message) {
        super(message);
    }
    
    public CapacidadNotFoundException(Long id) {
        super("Capacidad con ID " + id + " no encontrada");
    }
} 