package gabs.tecnologias.domain.exception;

/**
 * Excepción lanzada cuando no se encuentra una capacidad de tecnología
 */
public class CapacidadTecnologiaNotFoundException extends RuntimeException {
    
    public CapacidadTecnologiaNotFoundException(String message) {
        super(message);
    }
    
    public CapacidadTecnologiaNotFoundException(Long capacidadId) {
        super("No se encontraron tecnologías para la capacidad con ID " + capacidadId);
    }
} 