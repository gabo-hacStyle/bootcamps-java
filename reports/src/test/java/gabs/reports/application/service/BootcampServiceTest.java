package gabs.reports.application.service;

import gabs.reports.domain.exception.BootcampNotFoundException;
import gabs.reports.domain.exception.ValidationException;
import gabs.reports.domain.model.Bootcamp;
import gabs.reports.domain.port.BootcampRepositoryPort;
import gabs.reports.dto.BootcampRequest;
import gabs.reports.dto.BootcampResponse;
import gabs.reports.dto.CapacidadDTO;
import gabs.reports.dto.TecnologiaDTO;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BootcampServiceTest {

    @Mock
    private BootcampRepositoryPort repository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private BootcampService bootcampService;

    private BootcampRequest validBootcampRequest;
    private Bootcamp savedBootcamp;

    @BeforeEach
    void setUp() {
        validBootcampRequest = new BootcampRequest();
        validBootcampRequest.setId(1L);
        validBootcampRequest.setNombre("Java Bootcamp");
        validBootcampRequest.setDescripcion("Bootcamp de Java");
        validBootcampRequest.setFechaLanzamiento(LocalDate.now());
        validBootcampRequest.setDuracion(12);
        validBootcampRequest.setFechaFinalizacion(LocalDate.now().plusMonths(3));

        CapacidadDTO capacidad = new CapacidadDTO(1L, "Programación", Arrays.asList(new TecnologiaDTO(1L, "Java")));
        validBootcampRequest.setCapacidades(Arrays.asList(capacidad));

        savedBootcamp = new Bootcamp();
        savedBootcamp.setBootcampId(1L);
        savedBootcamp.setNombre("Java Bootcamp");
        savedBootcamp.setDescripcion("Bootcamp de Java");
        savedBootcamp.setFechaLanzamiento(LocalDate.now());
        savedBootcamp.setDuracion(12);
        savedBootcamp.setFechaFinalizacion(LocalDate.now().plusMonths(3));
        savedBootcamp.setCapacidades(Arrays.asList("Programación"));
        savedBootcamp.setTecnologias(Arrays.asList("Java"));
        savedBootcamp.setCantidadCapacidades(1);
        savedBootcamp.setCantidadTecnologias(1);
        savedBootcamp.setCantidadPersonasInscritas(0);
        savedBootcamp.setPersonasInscritas(Arrays.asList(1L, 2L));
    }

    @Test
    void findById_WithValidId_ShouldReturnBootcampResponse() {
        // Given
        Long bootcampId = 1L;
        when(repository.findByBootcampId(bootcampId)).thenReturn(Mono.just(savedBootcamp));

        // When & Then
        StepVerifier.create(bootcampService.findById(bootcampId))
                .expectNextMatches(response -> 
                    response.getId().equals(1L) &&
                    response.getNombre().equals("Java Bootcamp") &&
                    response.getCantidadCapacidades() == 1 &&
                    response.getCantidadTecnologias() == 1
                )
                .verifyComplete();

        verify(repository).findByBootcampId(bootcampId);
    }

    @Test
    void findById_WithNullId_ShouldThrowValidationException() {
        // When & Then
        StepVerifier.create(bootcampService.findById(null))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void findById_WithNonExistentId_ShouldThrowBootcampNotFoundException() {
        // Given
        Long bootcampId = 999L;
        when(repository.findByBootcampId(bootcampId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(bootcampService.findById(bootcampId))
                .expectError(BootcampNotFoundException.class)
                .verify();
    }

    @Test
    void register_WithValidRequest_ShouldReturnSavedBootcamp() {
        // Given
        when(repository.save(any(Bootcamp.class))).thenReturn(Mono.just(savedBootcamp));

        // When & Then
        StepVerifier.create(bootcampService.register(validBootcampRequest))
                .expectNextMatches(bootcamp -> 
                    bootcamp.getBootcampId().equals(1L) &&
                    bootcamp.getNombre().equals("Java Bootcamp") &&
                    bootcamp.getCantidadCapacidades() == 1 &&
                    bootcamp.getCantidadTecnologias() == 1
                )
                .verifyComplete();

        verify(repository).save(any(Bootcamp.class));
    }

    @Test
    void register_WithNullRequest_ShouldThrowValidationException() {
        // When & Then
        StepVerifier.create(bootcampService.register(null))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void register_WithEmptyNombre_ShouldThrowValidationException() {
        // Given
        validBootcampRequest.setNombre("");

        // When & Then
        StepVerifier.create(bootcampService.register(validBootcampRequest))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void register_WithNullDescripcion_ShouldThrowValidationException() {
        // Given
        validBootcampRequest.setDescripcion(null);

        // When & Then
        StepVerifier.create(bootcampService.register(validBootcampRequest))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void register_WithNullFechaLanzamiento_ShouldThrowValidationException() {
        // Given
        validBootcampRequest.setFechaLanzamiento(null);

        // When & Then
        StepVerifier.create(bootcampService.register(validBootcampRequest))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void register_WithInvalidDuracion_ShouldThrowValidationException() {
        // Given
        validBootcampRequest.setDuracion(0);

        // When & Then
        StepVerifier.create(bootcampService.register(validBootcampRequest))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void findBootcampConMasInscritos_WithValidData_ShouldReturnBootcampResponse() {
        // Given
        Bootcamp bootcamp1 = new Bootcamp();
        bootcamp1.setBootcampId(1L);
        bootcamp1.setNombre("Bootcamp 1");
        bootcamp1.setCantidadPersonasInscritas(5);
        bootcamp1.setPersonasInscritas(Arrays.asList(1L, 2L, 3L, 4L, 5L));

        Bootcamp bootcamp2 = new Bootcamp();
        bootcamp2.setBootcampId(2L);
        bootcamp2.setNombre("Bootcamp 2");
        bootcamp2.setCantidadPersonasInscritas(3);
        bootcamp2.setPersonasInscritas(Arrays.asList(1L, 2L, 3L));

        when(repository.findAll()).thenReturn(Flux.just(bootcamp1, bootcamp2));
        doNothing().when(eventPublisher).publishEvent(any());

        // When & Then
        StepVerifier.create(bootcampService.findBootcampConMasInscritos())
                .expectNextMatches(response -> 
                    response.getId().equals(1L) &&
                    response.getNombre().equals("Bootcamp 1") &&
                    response.getCantidadPersonasInscritas() == 5
                )
                .verifyComplete();

        verify(repository).findAll();
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void findBootcampConMasInscritos_WithNoBootcamps_ShouldThrowBootcampNotFoundException() {
        // Given
        when(repository.findAll()).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(bootcampService.findBootcampConMasInscritos())
                .expectError(BootcampNotFoundException.class)
                .verify();
    }
} 