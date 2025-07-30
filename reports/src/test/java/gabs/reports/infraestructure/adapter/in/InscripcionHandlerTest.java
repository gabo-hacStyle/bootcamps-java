package gabs.reports.infraestructure.adapter.in;

import gabs.reports.application.port.InscripcionUseCases;
import gabs.reports.domain.model.Inscripcion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InscripcionHandlerTest {

    @Mock
    private InscripcionUseCases inscripcionService;

    @Mock
    private GlobalExceptionHandler exceptionHandler;

    @InjectMocks
    private InscripcionHandler inscripcionHandler;

    private Inscripcion validInscripcion;
    private Inscripcion savedInscripcion;

    @BeforeEach
    void setUp() {
        validInscripcion = new Inscripcion();
        validInscripcion.setId(1L);
        validInscripcion.setPersonaId(1L);
        validInscripcion.setBootcampId(1L);
        validInscripcion.setNombrePersona("Juan Pérez");
        validInscripcion.setCorreoPersona("juan.perez@email.com");
        validInscripcion.setNombreBootcamp("Java Bootcamp");

        savedInscripcion = new Inscripcion();
        savedInscripcion.setId(1L);
        savedInscripcion.setPersonaId(1L);
        savedInscripcion.setBootcampId(1L);
        savedInscripcion.setNombrePersona("Juan Pérez");
        savedInscripcion.setCorreoPersona("juan.perez@email.com");
        savedInscripcion.setNombreBootcamp("Java Bootcamp");
    }

    @Test
    void registrarInscripcion_WithValidRequest_ShouldReturnOkResponse() {
        // Given
        when(inscripcionService.save(any(Inscripcion.class))).thenReturn(Mono.just(savedInscripcion));

        // When & Then
        StepVerifier.create(inscripcionHandler.registrarInscripcion(createServerRequest(validInscripcion)))
                .expectNextMatches(response -> response.statusCode().value() == 200)
                .verifyComplete();

        verify(inscripcionService).save(any(Inscripcion.class));
    }

    @Test
    void registrarInscripcion_WithValidationException_ShouldHandleError() {
        // Given
        when(inscripcionService.save(any(Inscripcion.class)))
                .thenReturn(Mono.error(new gabs.reports.domain.exception.ValidationException("Invalid data")));
        when(exceptionHandler.handleValidationException(any(), any()))
                .thenReturn(Mono.just(ServerResponse.badRequest().build()));

        // When & Then
        StepVerifier.create(inscripcionHandler.registrarInscripcion(createServerRequest(validInscripcion)))
                .expectNextMatches(response -> response.statusCode().value() == 400)
                .verifyComplete();

        verify(inscripcionService).save(any(Inscripcion.class));
        verify(exceptionHandler).handleValidationException(any(), any());
    }

    @Test
    void registrarInscripcion_WithGenericException_ShouldHandleError() {
        // Given
        when(inscripcionService.save(any(Inscripcion.class)))
                .thenReturn(Mono.error(new RuntimeException("Unexpected error")));
        when(exceptionHandler.handleGenericException(any(), any()))
                .thenReturn(Mono.just(ServerResponse.status(500).build()));

        // When & Then
        StepVerifier.create(inscripcionHandler.registrarInscripcion(createServerRequest(validInscripcion)))
                .expectNextMatches(response -> response.statusCode().value() == 500)
                .verifyComplete();

        verify(inscripcionService).save(any(Inscripcion.class));
        verify(exceptionHandler).handleGenericException(any(), any());
    }

    private ServerRequest createServerRequest(Object body) {
        // Mock implementation for testing
        return ServerRequest.create(null, null);
    }
} 