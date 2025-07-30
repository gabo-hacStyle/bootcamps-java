package gabs.reports.infraestructure.adapter.in;

import gabs.reports.application.service.PersonaService;
import gabs.reports.domain.model.Persona;
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
class PersonaHandlerTest {

    @Mock
    private PersonaService personaService;

    @Mock
    private GlobalExceptionHandler exceptionHandler;

    @InjectMocks
    private PersonaHandler personaHandler;

    private Persona validPersona;
    private Persona savedPersona;

    @BeforeEach
    void setUp() {
        validPersona = new Persona();
        validPersona.setPersonaId(1L);
        validPersona.setNombre("Juan Pérez");
        validPersona.setCorreo("juan.perez@email.com");
        validPersona.setEdad(25);

        savedPersona = new Persona();
        savedPersona.setPersonaId(1L);
        savedPersona.setNombre("Juan Pérez");
        savedPersona.setCorreo("juan.perez@email.com");
        savedPersona.setEdad(25);
    }

    @Test
    void crearPersona_WithValidRequest_ShouldReturnOkResponse() {
        // Given
        when(personaService.save(any(Persona.class))).thenReturn(Mono.just(savedPersona));

        // When & Then
        StepVerifier.create(personaHandler.crearPersona(createServerRequest(validPersona)))
                .expectNextMatches(response -> response.statusCode().value() == 200)
                .verifyComplete();

        verify(personaService).save(any(Persona.class));
    }

    @Test
    void crearPersona_WithValidationException_ShouldHandleError() {
        // Given
        when(personaService.save(any(Persona.class)))
                .thenReturn(Mono.error(new gabs.reports.domain.exception.ValidationException("Invalid data")));
        when(exceptionHandler.handleValidationException(any(), any()))
                .thenReturn(Mono.just(ServerResponse.badRequest().build()));

        // When & Then
        StepVerifier.create(personaHandler.crearPersona(createServerRequest(validPersona)))
                .expectNextMatches(response -> response.statusCode().value() == 400)
                .verifyComplete();

        verify(personaService).save(any(Persona.class));
        verify(exceptionHandler).handleValidationException(any(), any());
    }

    @Test
    void crearPersona_WithGenericException_ShouldHandleError() {
        // Given
        when(personaService.save(any(Persona.class)))
                .thenReturn(Mono.error(new RuntimeException("Unexpected error")));
        when(exceptionHandler.handleGenericException(any(), any()))
                .thenReturn(Mono.just(ServerResponse.status(500).build()));

        // When & Then
        StepVerifier.create(personaHandler.crearPersona(createServerRequest(validPersona)))
                .expectNextMatches(response -> response.statusCode().value() == 500)
                .verifyComplete();

        verify(personaService).save(any(Persona.class));
        verify(exceptionHandler).handleGenericException(any(), any());
    }

    private ServerRequest createServerRequest(Object body) {
        // Mock implementation for testing
        return ServerRequest.create(null, null);
    }
} 