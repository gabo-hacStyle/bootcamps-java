package gabs.tecnologias.infraestructure.config;

import gabs.tecnologias.domain.exception.CapacidadTecnologiaNotFoundException;
import gabs.tecnologias.domain.exception.TecnologiaNotFoundException;
import gabs.tecnologias.domain.exception.ValidationException;
import gabs.tecnologias.dto.ErrorResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private ServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/tecnologias/1")
                .build();
        exchange = MockServerWebExchange.from(request);
    }

    @Test
    void handleTecnologiaNotFoundException_ShouldReturn404() {
        // Given
        TecnologiaNotFoundException exception = new TecnologiaNotFoundException(1L);

        // When & Then
        StepVerifier.create(handler.handle(exchange, exception))
                .verifyComplete();

        assertEquals(HttpStatus.NOT_FOUND, exchange.getResponse().getStatusCode());
    }

    @Test
    void handleCapacidadTecnologiaNotFoundException_ShouldReturn404() {
        // Given
        CapacidadTecnologiaNotFoundException exception = new CapacidadTecnologiaNotFoundException(1L);

        // When & Then
        StepVerifier.create(handler.handle(exchange, exception))
                .verifyComplete();

        assertEquals(HttpStatus.NOT_FOUND, exchange.getResponse().getStatusCode());
    }

    @Test
    void handleValidationException_ShouldReturn400() {
        // Given
        ValidationException exception = new ValidationException("nombre", "El nombre no puede estar vacío");

        // When & Then
        StepVerifier.create(handler.handle(exchange, exception))
                .verifyComplete();

        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());
    }

    @Test
    void handleIllegalArgumentException_ShouldReturn400() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Argumento inválido");

        // When & Then
        StepVerifier.create(handler.handle(exchange, exception))
                .verifyComplete();

        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());
    }

    @Test
    void handleGenericException_ShouldReturn500() {
        // Given
        RuntimeException exception = new RuntimeException("Error interno");

        // When & Then
        StepVerifier.create(handler.handle(exchange, exception))
                .verifyComplete();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getResponse().getStatusCode());
    }

    @Test
    void handleException_ShouldSetContentTypeToJson() {
        // Given
        TecnologiaNotFoundException exception = new TecnologiaNotFoundException(1L);

        // When
        StepVerifier.create(handler.handle(exchange, exception))
                .verifyComplete();

        // Then
        assertEquals("application/json", exchange.getResponse().getHeaders().getContentType().toString());
    }
} 