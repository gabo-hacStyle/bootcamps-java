package gabs.tecnologias.domain.exception;

/**
 * Excepción lanzada cuando no se encuentra una tecnología
 */
public class TecnologiaNotFoundException extends RuntimeException {
    
    public TecnologiaNotFoundException(String message) {
        super(message);
    }
    
    public TecnologiaNotFoundException(Long id) {
        super("Tecnología con ID " + id + " no encontrada");
    }
} 