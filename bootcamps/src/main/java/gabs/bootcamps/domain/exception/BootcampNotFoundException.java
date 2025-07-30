package gabs.bootcamps.domain.exception;

import org.springframework.http.HttpStatus;

public class BootcampNotFoundException extends BootcampException {
    
    public BootcampNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "BOOTCAMP_NOT_FOUND");
    }
    
    public BootcampNotFoundException(Long id) {
        super("Bootcamp con ID " + id + " no encontrado", HttpStatus.NOT_FOUND, "BOOTCAMP_NOT_FOUND");
    }
} 