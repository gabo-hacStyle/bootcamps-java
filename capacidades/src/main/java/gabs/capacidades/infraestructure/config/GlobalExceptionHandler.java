package gabs.capacidades.infraestructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import gabs.capacidades.domain.exception.BootcampNotFoundException;
import gabs.capacidades.domain.exception.CapacidadNotFoundException;
import gabs.capacidades.domain.exception.ValidationException;
import gabs.capacidades.dto.ErrorResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalExceptionHandler {

    public Mono<ServerResponse> handleCapacidadNotFound(CapacidadNotFoundException ex, ServerRequest request) {
        log.error("Capacidad no encontrada: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "CAPACIDAD_NOT_FOUND",
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value()
        );
        
        return ServerResponse.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    public Mono<ServerResponse> handleBootcampNotFound(BootcampNotFoundException ex, ServerRequest request) {
        log.error("Bootcamp no encontrado: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "BOOTCAMP_NOT_FOUND",
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value()
        );
        
        return ServerResponse.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    public Mono<ServerResponse> handleValidationException(ValidationException ex, ServerRequest request) {
        log.error("Error de validación: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            "VALIDATION_ERROR",
            HttpStatus.BAD_REQUEST.value()
        );
        
        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    public Mono<ServerResponse> handleIllegalArgumentException(IllegalArgumentException ex, ServerRequest request) {
        log.error("Argumento ilegal: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            "INVALID_ARGUMENT",
            HttpStatus.BAD_REQUEST.value()
        );
        
        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    public Mono<ServerResponse> handleGenericException(Throwable ex, ServerRequest request) {
        log.error("Error interno del servidor: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Error interno del servidor",
            "INTERNAL_SERVER_ERROR",
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }
} 