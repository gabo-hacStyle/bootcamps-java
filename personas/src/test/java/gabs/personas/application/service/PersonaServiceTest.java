package gabs.personas.application.service;

import gabs.personas.domain.exception.*;
import gabs.personas.domain.model.Persona;
import gabs.personas.domain.port.PersonaRepositoryPort;
import gabs.personas.dto.PersonaReportResponse;
import gabs.personas.infraestructure.adapter.out.clients.ReportClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonaServiceTest {

    @Mock
    private PersonaRepositoryPort repository;

    @Mock
    private ReportClient webClient;

    private PersonaService service;

    @BeforeEach
    void setUp() {
        service = new PersonaService(repository, webClient);
    }

    @Test
    void findAll_ShouldReturnAllPersonas() {
        // Given
        Persona persona1 = new Persona();
        persona1.setId(1L);
        persona1.setNombre("Juan");
        persona1.setCorreo("juan@test.com");

        Persona persona2 = new Persona();
        persona2.setId(2L);
        persona2.setNombre("Maria");
        persona2.setCorreo("maria@test.com");

        when(repository.findAll()).thenReturn(Flux.just(persona1, persona2));

        // When & Then
        StepVerifier.create(service.findAll())
                .expectNext(persona1, persona2)
                .verifyComplete();
    }

    @Test
    void findById_WhenPersonaExists_ShouldReturnPersona() {
        // Given
        Long id = 1L;
        Persona persona = new Persona();
        persona.setId(id);
        persona.setNombre("Juan");
        persona.setCorreo("juan@test.com");

        when(repository.findById(id)).thenReturn(Mono.just(persona));

        // When & Then
        StepVerifier.create(service.findById(id))
                .expectNext(persona)
                .verifyComplete();
    }

    @Test
    void findById_WhenPersonaNotExists_ShouldThrowPersonaNotFoundException() {
        // Given
        Long id = 999L;
        when(repository.findById(id)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.findById(id))
                .expectError(PersonaNotFoundException.class)
                .verify();
    }

    @Test
    void register_WhenValidPersonaAndNotExists_ShouldRegisterSuccessfully() {
        // Given
        Persona persona = new Persona();
        persona.setNombre("Juan");
        persona.setCorreo("juan@test.com");
        persona.setEdad(25);

        Persona savedPersona = new Persona();
        savedPersona.setId(1L);
        savedPersona.setNombre("Juan");
        savedPersona.setCorreo("juan@test.com");
        savedPersona.setEdad(25);

        when(repository.existsByCorreo("juan@test.com")).thenReturn(Mono.just(false));
        when(repository.save(any(Persona.class))).thenReturn(Mono.just(savedPersona));
        when(webClient.postPersonaReport(any(PersonaReportResponse.class))).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.register(persona))
                .expectNext(savedPersona)
                .verifyComplete();

        verify(repository).existsByCorreo("juan@test.com");
        verify(repository).save(any(Persona.class));
        verify(webClient).postPersonaReport(any(PersonaReportResponse.class));
    }

    @Test
    void register_WhenPersonaAlreadyExists_ShouldThrowPersonaAlreadyExistsException() {
        // Given
        Persona persona = new Persona();
        persona.setNombre("Juan");
        persona.setCorreo("juan@test.com");

        when(repository.existsByCorreo("juan@test.com")).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(service.register(persona))
                .expectError(PersonaAlreadyExistsException.class)
                .verify();

        verify(repository).existsByCorreo("juan@test.com");
        verify(repository, never()).save(any(Persona.class));
    }

    @Test
    void register_WhenInvalidPersonaData_ShouldThrowInvalidPersonaDataException() {
        // Given
        Persona persona = new Persona();
        persona.setNombre(""); // Invalid empty name
        persona.setCorreo("juan@test.com");

        // When & Then
        StepVerifier.create(service.register(persona))
                .expectError(InvalidPersonaDataException.class)
                .verify();

        verify(repository, never()).existsByCorreo(anyString());
        verify(repository, never()).save(any(Persona.class));
    }

    @Test
    void register_WhenInvalidEmail_ShouldThrowInvalidPersonaDataException() {
        // Given
        Persona persona = new Persona();
        persona.setNombre("Juan");
        persona.setCorreo("invalid-email"); // Invalid email format

        // When & Then
        StepVerifier.create(service.register(persona))
                .expectError(InvalidPersonaDataException.class)
                .verify();
    }

    @Test
    void register_WhenInvalidAge_ShouldThrowInvalidPersonaDataException() {
        // Given
        Persona persona = new Persona();
        persona.setNombre("Juan");
        persona.setCorreo("juan@test.com");
        persona.setEdad(-5); // Invalid negative age

        // When & Then
        StepVerifier.create(service.register(persona))
                .expectError(InvalidPersonaDataException.class)
                .verify();
    }

    @Test
    void register_WhenExternalServiceFails_ShouldThrowExternalServiceException() {
        // Given
        Persona persona = new Persona();
        persona.setNombre("Juan");
        persona.setCorreo("juan@test.com");

        Persona savedPersona = new Persona();
        savedPersona.setId(1L);
        savedPersona.setNombre("Juan");
        savedPersona.setCorreo("juan@test.com");

        when(repository.existsByCorreo("juan@test.com")).thenReturn(Mono.just(false));
        when(repository.save(any(Persona.class))).thenReturn(Mono.just(savedPersona));
        when(webClient.postPersonaReport(any(PersonaReportResponse.class)))
                .thenReturn(Mono.error(new RuntimeException("External service error")));

        // When & Then
        StepVerifier.create(service.register(persona))
                .expectError(ExternalServiceException.class)
                .verify();
    }

    @Test
    void updateParcial_WhenPersonaExists_ShouldUpdateSuccessfully() {
        // Given
        Long id = 1L;
        Persona original = new Persona();
        original.setId(id);
        original.setNombre("Juan");
        original.setCorreo("juan@test.com");

        Persona changes = new Persona();
        changes.setNombre("Juan Updated");

        Persona updated = new Persona();
        updated.setId(id);
        updated.setNombre("Juan Updated");
        updated.setCorreo("juan@test.com");

        when(repository.findById(id)).thenReturn(Mono.just(original));
        when(repository.save(any(Persona.class))).thenReturn(Mono.just(updated));

        // When & Then
        StepVerifier.create(service.updateParcial(id, changes))
                .expectNext(updated)
                .verifyComplete();
    }

    @Test
    void updateParcial_WhenPersonaNotExists_ShouldThrowPersonaNotFoundException() {
        // Given
        Long id = 999L;
        Persona changes = new Persona();
        changes.setNombre("Juan Updated");

        when(repository.findById(id)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.updateParcial(id, changes))
                .expectError(PersonaNotFoundException.class)
                .verify();
    }

    @Test
    void updateParcial_WhenEmailAlreadyExists_ShouldThrowPersonaAlreadyExistsException() {
        // Given
        Long id = 1L;
        Persona original = new Persona();
        original.setId(id);
        original.setNombre("Juan");
        original.setCorreo("juan@test.com");

        Persona changes = new Persona();
        changes.setCorreo("maria@test.com");

        when(repository.findById(id)).thenReturn(Mono.just(original));
        when(repository.existsByCorreo("maria@test.com")).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(service.updateParcial(id, changes))
                .expectError(PersonaAlreadyExistsException.class)
                .verify();
    }

    @Test
    void delete_WhenPersonaExists_ShouldDeleteSuccessfully() {
        // Given
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(Mono.just(true));
        when(repository.deleteById(id)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.delete(id))
                .verifyComplete();

        verify(repository).existsById(id);
        verify(repository).deleteById(id);
    }

    @Test
    void delete_WhenPersonaNotExists_ShouldThrowPersonaNotFoundException() {
        // Given
        Long id = 999L;
        when(repository.existsById(id)).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(service.delete(id))
                .expectError(PersonaNotFoundException.class)
                .verify();

        verify(repository).existsById(id);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void existsById_ShouldReturnTrue_WhenPersonaExists() {
        // Given
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(service.existsById(id))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsById_ShouldReturnFalse_WhenPersonaNotExists() {
        // Given
        Long id = 999L;
        when(repository.existsById(id)).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(service.existsById(id))
                .expectNext(false)
                .verifyComplete();
    }
} 