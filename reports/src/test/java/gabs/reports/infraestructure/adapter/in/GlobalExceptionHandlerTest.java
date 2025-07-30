package gabs.reports.infraestructure.adapter.in;

import gabs.reports.domain.exception.BootcampNotFoundException;
import gabs.reports.domain.exception.DuplicateResourceException;
import gabs.reports.domain.exception.InscripcionNotFoundException;
import gabs.reports.domain.exception.PersonaNotFoundException;
import gabs.reports.domain.exception.ValidationException;
import gabs.reports.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationException_ShouldReturnBadRequest() {
        // Given
        ValidationException exception = new ValidationException("Invalid input data");

        // When
        Mono<ServerResponse> result = handler.handleValidationException(exception, "/test");

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.statusCode());
                    assertNotNull(response);
                })
                .verifyComplete();
    }

    @Test
    void handleBootcampNotFoundException_ShouldReturnNotFound() {
        // Given
        BootcampNotFoundException exception = new BootcampNotFoundException("Bootcamp with id 123 not found");

        // When
        Mono<ServerResponse> result = handler.handleBootcampNotFoundException(exception, "/test");

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.NOT_FOUND, response.statusCode());
                    assertNotNull(response);
                })
                .verifyComplete();
    }

    @Test
    void handlePersonaNotFoundException_ShouldReturnNotFound() {
        // Given
        PersonaNotFoundException exception = new PersonaNotFoundException("Persona with id 456 not found");

        // When
        Mono<ServerResponse> result = handler.handlePersonaNotFoundException(exception, "/test");

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.NOT_FOUND, response.statusCode());
                    assertNotNull(response);
                })
                .verifyComplete();
    }

    @Test
    void handleInscripcionNotFoundException_ShouldReturnNotFound() {
        // Given
        InscripcionNotFoundException exception = new InscripcionNotFoundException("Inscripcion with id 789 not found");

        // When
        Mono<ServerResponse> result = handler.handleInscripcionNotFoundException(exception, "/test");

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.NOT_FOUND, response.statusCode());
                    assertNotNull(response);
                })
                .verifyComplete();
    }

    @Test
    void handleDuplicateResourceException_ShouldReturnConflict() {
        // Given
        DuplicateResourceException exception = new DuplicateResourceException("Resource already exists");

        // When
        Mono<ServerResponse> result = handler.handleDuplicateResourceException(exception, "/test");

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.CONFLICT, response.statusCode());
                    assertNotNull(response);
                })
                .verifyComplete();
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        // Given
        Exception exception = new RuntimeException("Unexpected error occurred");

        // When
        Mono<ServerResponse> result = handler.handleGenericException(exception, "/test");

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode());
                    assertNotNull(response);
                })
                .verifyComplete();
    }
} 