package gabs.bootcamps.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BootcampException extends RuntimeException {
    
    private final HttpStatus statusCode;
    private final String errorCode;
    
    public BootcampException(String message, HttpStatus statusCode, String errorCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }
    
    public BootcampException(String message, HttpStatus statusCode) {
        this(message, statusCode, "BOOTCAMP_ERROR");
    }
} 