package gabs.reports.application.service;

import gabs.reports.domain.exception.PersonaNotFoundException;
import gabs.reports.domain.exception.ValidationException;
import gabs.reports.domain.model.Persona;
import gabs.reports.domain.port.PersonaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonaServiceTest {

    @Mock
    private PersonaRepositoryPort repository;

    @InjectMocks
    private PersonaService personaService;

    private Persona validPersona;

    @BeforeEach
    void setUp() {
        validPersona = new Persona();
        validPersona.setPersonaId(1L);
        validPersona.setNombre("Juan Pérez");
        validPersona.setCorreo("juan.perez@email.com");
        validPersona.setEdad(25);
    }

    @Test
    void save_WithValidPersona_ShouldReturnSavedPersona() {
        // Given
        when(repository.save(any(Persona.class))).thenReturn(Mono.just(validPersona));

        // When & Then
        StepVerifier.create(personaService.save(validPersona))
                .expectNextMatches(persona -> 
                    persona.getPersonaId().equals(1L) &&
                    persona.getNombre().equals("Juan Pérez") &&
                    persona.getCorreo().equals("juan.perez@email.com") &&
                    persona.getEdad() == 25
                )
                .verifyComplete();

        verify(repository).save(validPersona);
    }

    @Test
    void save_WithNullPersona_ShouldThrowValidationException() {
        // When & Then
        StepVerifier.create(personaService.save(null))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void save_WithEmptyNombre_ShouldThrowValidationException() {
        // Given
        validPersona.setNombre("");

        // When & Then
        StepVerifier.create(personaService.save(validPersona))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void save_WithNullCorreo_ShouldThrowValidationException() {
        // Given
        validPersona.setCorreo(null);

        // When & Then
        StepVerifier.create(personaService.save(validPersona))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void save_WithEmptyCorreo_ShouldThrowValidationException() {
        // Given
        validPersona.setCorreo("");

        // When & Then
        StepVerifier.create(personaService.save(validPersona))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void save_WithInvalidEdad_ShouldThrowValidationException() {
        // Given
        validPersona.setEdad(0);

        // When & Then
        StepVerifier.create(personaService.save(validPersona))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void findById_WithValidId_ShouldReturnPersona() {
        // Given
        Long personaId = 1L;
        when(repository.findByPersonaId(personaId)).thenReturn(Mono.just(validPersona));

        // When & Then
        StepVerifier.create(personaService.findById(personaId))
                .expectNextMatches(persona -> 
                    persona.getPersonaId().equals(1L) &&
                    persona.getNombre().equals("Juan Pérez")
                )
                .verifyComplete();

        verify(repository).findByPersonaId(personaId);
    }

    @Test
    void findById_WithNullId_ShouldThrowValidationException() {
        // When & Then
        StepVerifier.create(personaService.findById(null))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void findById_WithNonExistentId_ShouldThrowPersonaNotFoundException() {
        // Given
        Long personaId = 999L;
        when(repository.findByPersonaId(personaId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(personaService.findById(personaId))
                .expectError(PersonaNotFoundException.class)
                .verify();
    }

    @Test
    void findAll_ShouldReturnAllPersonas() {
        // Given
        Persona persona1 = new Persona();
        persona1.setPersonaId(1L);
        persona1.setNombre("Juan Pérez");
        persona1.setCorreo("juan.perez@email.com");
        persona1.setEdad(25);

        Persona persona2 = new Persona();
        persona2.setPersonaId(2L);
        persona2.setNombre("María García");
        persona2.setCorreo("maria.garcia@email.com");
        persona2.setEdad(30);

        when(repository.findAll()).thenReturn(Flux.just(persona1, persona2));

        // When & Then
        StepVerifier.create(personaService.findAll())
                .expectNextCount(2)
                .verifyComplete();

        verify(repository).findAll();
    }
} 