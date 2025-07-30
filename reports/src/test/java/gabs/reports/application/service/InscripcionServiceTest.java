package gabs.reports.application.service;

import gabs.reports.domain.exception.InscripcionNotFoundException;
import gabs.reports.domain.exception.ValidationException;
import gabs.reports.domain.model.Inscripcion;
import gabs.reports.domain.port.InscripcionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InscripcionServiceTest {

    @Mock
    private InscripcionRepositoryPort repository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private InscripcionService inscripcionService;

    private Inscripcion validInscripcion;

    @BeforeEach
    void setUp() {
        validInscripcion = new Inscripcion();
        validInscripcion.setId(1L);
        validInscripcion.setPersonaId(1L);
        validInscripcion.setBootcampId(1L);
        validInscripcion.setNombrePersona("Juan Pérez");
        validInscripcion.setCorreoPersona("juan.perez@email.com");
        validInscripcion.setNombreBootcamp("Java Bootcamp");
    }

    @Test
    void save_WithValidInscripcion_ShouldReturnSavedInscripcion() {
        // Given
        when(repository.save(any(Inscripcion.class))).thenReturn(Mono.just(validInscripcion));
        doNothing().when(eventPublisher).publishEvent(any());

        // When & Then
        StepVerifier.create(inscripcionService.save(validInscripcion))
                .expectNextMatches(inscripcion -> 
                    inscripcion.getId().equals(1L) &&
                    inscripcion.getPersonaId().equals(1L) &&
                    inscripcion.getBootcampId().equals(1L) &&
                    inscripcion.getNombrePersona().equals("Juan Pérez")
                )
                .verifyComplete();

        verify(repository).save(validInscripcion);
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void save_WithNullInscripcion_ShouldThrowValidationException() {
        // When & Then
        StepVerifier.create(inscripcionService.save(null))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void save_WithNullPersonaId_ShouldThrowValidationException() {
        // Given
        validInscripcion.setPersonaId(null);

        // When & Then
        StepVerifier.create(inscripcionService.save(validInscripcion))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void save_WithNullBootcampId_ShouldThrowValidationException() {
        // Given
        validInscripcion.setBootcampId(null);

        // When & Then
        StepVerifier.create(inscripcionService.save(validInscripcion))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void save_WithEmptyNombrePersona_ShouldThrowValidationException() {
        // Given
        validInscripcion.setNombrePersona("");

        // When & Then
        StepVerifier.create(inscripcionService.save(validInscripcion))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void save_WithEmptyCorreoPersona_ShouldThrowValidationException() {
        // Given
        validInscripcion.setCorreoPersona("");

        // When & Then
        StepVerifier.create(inscripcionService.save(validInscripcion))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void save_WithEmptyNombreBootcamp_ShouldThrowValidationException() {
        // Given
        validInscripcion.setNombreBootcamp("");

        // When & Then
        StepVerifier.create(inscripcionService.save(validInscripcion))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void findById_WithValidId_ShouldReturnInscripcion() {
        // Given
        Long inscripcionId = 1L;
        when(repository.findById(inscripcionId)).thenReturn(Mono.just(validInscripcion));

        // When & Then
        StepVerifier.create(inscripcionService.findById(inscripcionId))
                .expectNextMatches(inscripcion -> 
                    inscripcion.getId().equals(1L) &&
                    inscripcion.getNombrePersona().equals("Juan Pérez")
                )
                .verifyComplete();

        verify(repository).findById(inscripcionId);
    }

    @Test
    void findById_WithNullId_ShouldThrowValidationException() {
        // When & Then
        StepVerifier.create(inscripcionService.findById(null))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void findById_WithNonExistentId_ShouldThrowInscripcionNotFoundException() {
        // Given
        Long inscripcionId = 999L;
        when(repository.findById(inscripcionId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(inscripcionService.findById(inscripcionId))
                .expectError(InscripcionNotFoundException.class)
                .verify();
    }

    @Test
    void findAll_ShouldReturnAllInscripciones() {
        // Given
        Inscripcion inscripcion1 = new Inscripcion();
        inscripcion1.setId(1L);
        inscripcion1.setNombrePersona("Juan Pérez");
        inscripcion1.setNombreBootcamp("Java Bootcamp");

        Inscripcion inscripcion2 = new Inscripcion();
        inscripcion2.setId(2L);
        inscripcion2.setNombrePersona("María García");
        inscripcion2.setNombreBootcamp("Python Bootcamp");

        when(repository.findAll()).thenReturn(Flux.just(inscripcion1, inscripcion2));

        // When & Then
        StepVerifier.create(inscripcionService.findAll())
                .expectNextCount(2)
                .verifyComplete();

        verify(repository).findAll();
    }

    @Test
    void deleteById_WithValidId_ShouldDeleteInscripcion() {
        // Given
        Long inscripcionId = 1L;
        when(repository.findById(inscripcionId)).thenReturn(Mono.just(validInscripcion));
        when(repository.deleteById(inscripcionId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(inscripcionService.deleteById(inscripcionId))
                .verifyComplete();

        verify(repository).findById(inscripcionId);
        verify(repository).deleteById(inscripcionId);
    }

    @Test
    void deleteById_WithNullId_ShouldThrowValidationException() {
        // When & Then
        StepVerifier.create(inscripcionService.deleteById(null))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void deleteById_WithNonExistentId_ShouldThrowInscripcionNotFoundException() {
        // Given
        Long inscripcionId = 999L;
        when(repository.findById(inscripcionId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(inscripcionService.deleteById(inscripcionId))
                .expectError(InscripcionNotFoundException.class)
                .verify();
    }

    @Test
    void findByBootcampId_WithValidId_ShouldReturnInscripciones() {
        // Given
        Long bootcampId = 1L;
        Inscripcion inscripcion1 = new Inscripcion();
        inscripcion1.setId(1L);
        inscripcion1.setBootcampId(bootcampId);
        inscripcion1.setNombrePersona("Juan Pérez");

        Inscripcion inscripcion2 = new Inscripcion();
        inscripcion2.setId(2L);
        inscripcion2.setBootcampId(bootcampId);
        inscripcion2.setNombrePersona("María García");

        when(repository.findByBootcampId(bootcampId)).thenReturn(Flux.just(inscripcion1, inscripcion2));

        // When & Then
        StepVerifier.create(inscripcionService.findByBootcampId(bootcampId))
                .expectNextCount(2)
                .verifyComplete();

        verify(repository).findByBootcampId(bootcampId);
    }

    @Test
    void findByBootcampId_WithNullId_ShouldThrowValidationException() {
        // When & Then
        StepVerifier.create(inscripcionService.findByBootcampId(null))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void findByPersonaId_WithValidId_ShouldReturnInscripciones() {
        // Given
        Long personaId = 1L;
        Inscripcion inscripcion1 = new Inscripcion();
        inscripcion1.setId(1L);
        inscripcion1.setPersonaId(personaId);
        inscripcion1.setNombreBootcamp("Java Bootcamp");

        Inscripcion inscripcion2 = new Inscripcion();
        inscripcion2.setId(2L);
        inscripcion2.setPersonaId(personaId);
        inscripcion2.setNombreBootcamp("Python Bootcamp");

        when(repository.findByPersonaId(personaId)).thenReturn(Flux.just(inscripcion1, inscripcion2));

        // When & Then
        StepVerifier.create(inscripcionService.findByPersonaId(personaId))
                .expectNextCount(2)
                .verifyComplete();

        verify(repository).findByPersonaId(personaId);
    }

    @Test
    void findByPersonaId_WithNullId_ShouldThrowValidationException() {
        // When & Then
        StepVerifier.create(inscripcionService.findByPersonaId(null))
                .expectError(ValidationException.class)
                .verify();
    }
} 