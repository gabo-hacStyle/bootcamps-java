package gabs.personas.infraestructure.config;

import gabs.personas.domain.exception.*;
import gabs.personas.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@Slf4j
public class GlobalExceptionHandler {

    public Mono<ServerResponse> handlePersonaNotFoundException(PersonaNotFoundException ex, ServerRequest request) {
        log.error("Persona no encontrada: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            "NOT_FOUND",
            HttpStatus.NOT_FOUND.value(),
            request.path()
        );
        
        return ServerResponse.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    public Mono<ServerResponse> handlePersonaAlreadyExistsException(PersonaAlreadyExistsException ex, ServerRequest request) {
        log.error("Persona ya existe: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            "CONFLICT",
            HttpStatus.CONFLICT.value(),
            request.path()
        );
        
        return ServerResponse.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    public Mono<ServerResponse> handleInvalidPersonaDataException(InvalidPersonaDataException ex, ServerRequest request) {
        log.error("Datos de persona inválidos: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            "BAD_REQUEST",
            HttpStatus.BAD_REQUEST.value(),
            request.path()
        );
        
        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    public Mono<ServerResponse> handleExternalServiceException(ExternalServiceException ex, ServerRequest request) {
        log.error("Error en servicio externo: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            "SERVICE_UNAVAILABLE",
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            request.path()
        );
        
        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    public Mono<ServerResponse> handleIllegalArgumentException(IllegalArgumentException ex, ServerRequest request) {
        log.error("Argumento ilegal: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            "BAD_REQUEST",
            HttpStatus.BAD_REQUEST.value(),
            request.path()
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
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.path()
        );
        
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }
} 