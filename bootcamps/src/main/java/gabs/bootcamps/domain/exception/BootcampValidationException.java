package gabs.bootcamps.domain.exception;

import org.springframework.http.HttpStatus;

public class BootcampValidationException extends BootcampException {
    
    public BootcampValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "BOOTCAMP_VALIDATION_ERROR");
    }
    
    public static BootcampValidationException invalidCapacidadesQuantity() {
        return new BootcampValidationException("Debe asociar entre 1 y 4 capacidades.");
    }
    
    public static BootcampValidationException duplicateCapacidades() {
        return new BootcampValidationException("No se permiten tecnologías repetidas.");
    }
    
    public static BootcampValidationException capacidadesNotFound(String capacidadesIds) {
        return new BootcampValidationException("Las siguientes capacidades no existen: " + capacidadesIds);
    }
} 