package gabs.reports.infraestructure.adapter.in;

import gabs.reports.domain.exception.BootcampNotFoundException;
import gabs.reports.domain.exception.DuplicateResourceException;
import gabs.reports.domain.exception.InscripcionNotFoundException;
import gabs.reports.domain.exception.PersonaNotFoundException;
import gabs.reports.domain.exception.ValidationException;
import gabs.reports.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalExceptionHandler {

    public Mono<ServerResponse> handleValidationException(ValidationException ex, String path) {
        log.error("Validation error: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                path
        );
        return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    public Mono<ServerResponse> handleBootcampNotFoundException(BootcampNotFoundException ex, String path) {
        log.error("Bootcamp not found: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                path
        );
        return ServerResponse.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    public Mono<ServerResponse> handlePersonaNotFoundException(PersonaNotFoundException ex, String path) {
        log.error("Persona not found: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                path
        );
        return ServerResponse.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    public Mono<ServerResponse> handleInscripcionNotFoundException(InscripcionNotFoundException ex, String path) {
        log.error("Inscripcion not found: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                path
        );
        return ServerResponse.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    public Mono<ServerResponse> handleDuplicateResourceException(DuplicateResourceException ex, String path) {
        log.error("Duplicate resource: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                path
        );
        return ServerResponse.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    public Mono<ServerResponse> handleGenericException(Exception ex, String path) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred",
                path
        );
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }
} 