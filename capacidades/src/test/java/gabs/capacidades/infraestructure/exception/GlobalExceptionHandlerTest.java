package gabs.capacidades.infraestructure.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import gabs.capacidades.domain.exception.BootcampNotFoundException;
import gabs.capacidades.domain.exception.CapacidadNotFoundException;
import gabs.capacidades.domain.exception.ValidationException;
import gabs.capacidades.infraestructure.config.GlobalExceptionHandler;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private ServerRequest request;

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleCapacidadNotFound_ShouldReturnNotFoundResponse() {
        // Given
        Long id = 999L;
        CapacidadNotFoundException exception = new CapacidadNotFoundException(id);

        // When
        Mono<ServerResponse> response = handler.handleCapacidadNotFound(exception, request);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> {
                    assertEquals(HttpStatus.NOT_FOUND, serverResponse.statusCode());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void handleBootcampNotFound_ShouldReturnNotFoundResponse() {
        // Given
        Long id = 999L;
        BootcampNotFoundException exception = new BootcampNotFoundException(id);

        // When
        Mono<ServerResponse> response = handler.handleBootcampNotFound(exception, request);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> {
                    assertEquals(HttpStatus.NOT_FOUND, serverResponse.statusCode());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void handleValidationException_ShouldReturnBadRequestResponse() {
        // Given
        ValidationException exception = new ValidationException("Error de validación");

        // When
        Mono<ServerResponse> response = handler.handleValidationException(exception, request);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> {
                    assertEquals(HttpStatus.BAD_REQUEST, serverResponse.statusCode());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequestResponse() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Argumento ilegal");

        // When
        Mono<ServerResponse> response = handler.handleIllegalArgumentException(exception, request);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> {
                    assertEquals(HttpStatus.BAD_REQUEST, serverResponse.statusCode());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerErrorResponse() {
        // Given
        RuntimeException exception = new RuntimeException("Error interno");

        // When
        Mono<ServerResponse> response = handler.handleGenericException(exception, request);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, serverResponse.statusCode());
                    return true;
                })
                .verifyComplete();
    }
} 