package gabs.personas.infraestructure.adapter.in;

import gabs.personas.application.port.BootcampPersonaUseCases;
import gabs.personas.application.port.PersonaUseCases;
import gabs.personas.domain.exception.*;
import gabs.personas.domain.model.Persona;
import gabs.personas.dto.ErrorResponse;
import gabs.personas.dto.PersonaRegisteredResponse;
import gabs.personas.infraestructure.config.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonaHandlerTest {

    @Mock
    private PersonaUseCases personaService;

    @Mock
    private BootcampPersonaUseCases bootcampPersonaService;

    @Mock
    private ServerRequest serverRequest;

    private GlobalExceptionHandler exceptionHandler;
    private PersonaHandler handler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        handler = new PersonaHandler(personaService, bootcampPersonaService, exceptionHandler);
    }

    @Test
    void getById_WhenPersonaExists_ShouldReturnPersona() {
        // Given
        Long id = 1L;
        Persona persona = new Persona();
        persona.setId(id);
        persona.setNombre("Juan");
        persona.setCorreo("juan@test.com");

        when(personaService.findById(id)).thenReturn(Mono.just(persona));
        when(serverRequest.pathVariable("id")).thenReturn(id.toString());

        StepVerifier.create(handler.getById(serverRequest))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getById_WhenPersonaNotExists_ShouldReturnNotFound() {
        // Given
        Long id = 999L;
        when(personaService.findById(id)).thenReturn(Mono.error(new PersonaNotFoundException(id)));
        when(serverRequest.pathVariable("id")).thenReturn(id.toString());
      

        
        StepVerifier.create(handler.getById(serverRequest))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void save_WhenValidPersona_ShouldReturnCreated() {
        // Given
        Persona persona = new Persona();
        persona.setNombre("Juan");
        persona.setCorreo("juan@test.com");

        Persona savedPersona = new Persona();
        savedPersona.setId(1L);
        savedPersona.setNombre("Juan");
        savedPersona.setCorreo("juan@test.com");

        when(personaService.register(any(Persona.class))).thenReturn(Mono.just(savedPersona));
        when(serverRequest.bodyToMono(Persona.class)).thenReturn(Mono.just(persona));
       
        // When & Then
        StepVerifier.create(handler.save(serverRequest))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void save_WhenPersonaAlreadyExists_ShouldReturnConflict() {
        // Given
        Persona persona = new Persona();
        persona.setNombre("Juan");
        persona.setCorreo("juan@test.com");

        when(personaService.register(any(Persona.class)))
                .thenReturn(Mono.error(new PersonaAlreadyExistsException("juan@test.com")));
        when(serverRequest.bodyToMono(Persona.class)).thenReturn(Mono.just(persona));
     

        // When & Then
        StepVerifier.create(handler.save(serverRequest))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void save_WhenInvalidData_ShouldReturnBadRequest() {
        // Given
        Persona persona = new Persona();
        persona.setNombre("");
        persona.setCorreo("juan@test.com");

        when(personaService.register(any(Persona.class)))
                .thenReturn(Mono.error(new InvalidPersonaDataException("nombre", "valor inválido")));
        when(serverRequest.bodyToMono(Persona.class)).thenReturn(Mono.just(persona));
       
        // When & Then
        StepVerifier.create(handler.save(serverRequest))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void update_WhenPersonaExists_ShouldReturnOk() {
        // Given
        Long id = 1L;
        Persona changes = new Persona();
        changes.setNombre("Juan Updated");

        Persona updatedPersona = new Persona();
        updatedPersona.setId(id);
        updatedPersona.setNombre("Juan Updated");
        updatedPersona.setCorreo("juan@test.com");

        when(personaService.updateParcial(anyLong(), any(Persona.class)))
                .thenReturn(Mono.just(updatedPersona));
        when(serverRequest.pathVariable("id")).thenReturn(id.toString());
        when(serverRequest.bodyToMono(Persona.class)).thenReturn(Mono.just(changes));
       

        // When & Then
        StepVerifier.create(handler.update(serverRequest))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void update_WhenPersonaNotExists_ShouldReturnNotFound() {
        // Given
        Long id = 999L;
        Persona changes = new Persona();
        changes.setNombre("Juan Updated");

        when(personaService.updateParcial(anyLong(), any(Persona.class)))
                .thenReturn(Mono.error(new PersonaNotFoundException(id)));
        when(serverRequest.pathVariable("id")).thenReturn(id.toString());
        when(serverRequest.bodyToMono(Persona.class)).thenReturn(Mono.just(changes));
        when(serverRequest.path()).thenReturn("/api/personas/" + id);

        // When & Then
        StepVerifier.create(handler.update(serverRequest))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void delete_WhenPersonaExists_ShouldReturnOk() {
        // Given
        Long id = 1L;
        when(personaService.delete(id)).thenReturn(Mono.empty());
        when(serverRequest.pathVariable("id")).thenReturn(id.toString());
       

        // When & Then
        StepVerifier.create(handler.delete(serverRequest))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void delete_WhenPersonaNotExists_ShouldReturnNotFound() {
        // Given
        Long id = 999L;
        when(personaService.delete(id))
                .thenReturn(Mono.error(new PersonaNotFoundException(id)));
        when(serverRequest.pathVariable("id")).thenReturn(id.toString());
        

        // When & Then
        StepVerifier.create(handler.delete(serverRequest))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getAll_ShouldReturnAllPersonas() {
        // Given
        Persona persona1 = new Persona();
        persona1.setId(1L);
        persona1.setNombre("Juan");
        persona1.setCorreo("juan@test.com");

        Persona persona2 = new Persona();
        persona2.setId(2L);
        persona2.setNombre("Maria");
        persona2.setCorreo("maria@test.com");

        when(personaService.findAll()).thenReturn(Flux.just(persona1, persona2));
      
        // When & Then
        StepVerifier.create(handler.getAll(serverRequest))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void existsById_ShouldReturnBoolean() {
        // Given
        Long id = 1L;
        when(personaService.existsById(id)).thenReturn(Mono.just(true));
        when(serverRequest.pathVariable("id")).thenReturn(id.toString());

        // When & Then
        StepVerifier.create(handler.existsById(serverRequest))
                .expectNextCount(1)
                .verifyComplete();
    }


} 