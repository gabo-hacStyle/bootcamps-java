package gabs.bootcamps.infraestructure.config;

import gabs.bootcamps.domain.exception.BootcampException;
import gabs.bootcamps.domain.exception.BootcampNotFoundException;
import gabs.bootcamps.domain.exception.BootcampValidationException;
import gabs.bootcamps.domain.exception.ExternalServiceException;
import gabs.bootcamps.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
public class GlobalExceptionHandler {

    public Mono<ServerResponse> handleBootcampException(BootcampException ex, ServerRequest request) {
        log.error("BootcampException: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getErrorCode(),
            ex.getMessage(),
            request.path()
        );
        
        return ServerResponse.status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    public Mono<ServerResponse> handleBootcampNotFoundException(BootcampNotFoundException ex, ServerRequest request) {
        log.warn("BootcampNotFoundException: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getErrorCode(),
            ex.getMessage(),
            request.path()
        );
        
        return ServerResponse.status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    public Mono<ServerResponse> handleBootcampValidationException(BootcampValidationException ex, ServerRequest request) {
        log.warn("BootcampValidationException: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getErrorCode(),
            ex.getMessage(),
            request.path()
        );
        
        return ServerResponse.status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    public Mono<ServerResponse> handleExternalServiceException(ExternalServiceException ex, ServerRequest request) {
        log.error("ExternalServiceException: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getErrorCode(),
            ex.getMessage(),
            request.path()
        );
        
        return ServerResponse.status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    public Mono<ServerResponse> handleIllegalArgumentException(IllegalArgumentException ex, ServerRequest request) {
        log.warn("IllegalArgumentException: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "VALIDATION_ERROR",
            ex.getMessage(),
            request.path()
        );
        
        return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    public Mono<ServerResponse> handleGenericException(Throwable ex, ServerRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "Error interno del servidor",
            request.path()
        );
        
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }
} 