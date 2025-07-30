package gabs.personas.infraestructure.config;

import gabs.personas.domain.exception.*;
import gabs.personas.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private ServerRequest request;

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        when(request.path()).thenReturn("/api/personas");
    }

    @Test
    void handlePersonaNotFoundException_ShouldReturnNotFoundResponse() {
        // Given
        PersonaNotFoundException ex = new PersonaNotFoundException(999L);

        // When & Then
        StepVerifier.create(handler.handlePersonaNotFoundException(ex, request))
                .expectNextMatches(response -> {
                    return response.statusCode() == HttpStatus.NOT_FOUND;
                })
                .verifyComplete();
    }

    @Test
    void handlePersonaAlreadyExistsException_ShouldReturnConflictResponse() {
        // Given
        PersonaAlreadyExistsException ex = new PersonaAlreadyExistsException("test@example.com");

        // When & Then
        StepVerifier.create(handler.handlePersonaAlreadyExistsException(ex, request))
                .expectNextMatches(response -> {
                    return response.statusCode() == HttpStatus.CONFLICT;
                })
                .verifyComplete();
    }

    @Test
    void handleInvalidPersonaDataException_ShouldReturnBadRequestResponse() {
        // Given
        InvalidPersonaDataException ex = new InvalidPersonaDataException("nombre", "valor inválido");

        // When & Then
        StepVerifier.create(handler.handleInvalidPersonaDataException(ex, request))
                .expectNextMatches(response -> {
                    return response.statusCode() == HttpStatus.BAD_REQUEST;
                })
                .verifyComplete();
    }

    @Test
    void handleExternalServiceException_ShouldReturnServiceUnavailableResponse() {
        // Given
        ExternalServiceException ex = new ExternalServiceException("ReportClient", "Service unavailable");

        // When & Then
        StepVerifier.create(handler.handleExternalServiceException(ex, request))
                .expectNextMatches(response -> {
                    return response.statusCode() == HttpStatus.SERVICE_UNAVAILABLE;
                })
                .verifyComplete();
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequestResponse() {
        // Given
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        // When & Then
        StepVerifier.create(handler.handleIllegalArgumentException(ex, request))
                .expectNextMatches(response -> {
                    return response.statusCode() == HttpStatus.BAD_REQUEST;
                })
                .verifyComplete();
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerErrorResponse() {
        // Given
        RuntimeException ex = new RuntimeException("Unexpected error");

        // When & Then
        StepVerifier.create(handler.handleGenericException(ex, request))
                .expectNextMatches(response -> {
                    return response.statusCode() == HttpStatus.INTERNAL_SERVER_ERROR;
                })
                .verifyComplete();
    }
} 