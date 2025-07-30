package gabs.bootcamps.application.service;

import gabs.bootcamps.application.port.BootcampUseCases;
import gabs.bootcamps.domain.event.BootcampCreatedEvent;
import gabs.bootcamps.domain.exception.BootcampNotFoundException;
import gabs.bootcamps.domain.exception.BootcampValidationException;
import gabs.bootcamps.domain.exception.ExternalServiceException;
import gabs.bootcamps.domain.model.Bootcamp;
import gabs.bootcamps.domain.port.BootcampRepositoryPort;
import gabs.bootcamps.dto.*;
import gabs.bootcamps.infraestructure.adapter.out.CapacidadesClient;
import gabs.bootcamps.infraestructure.adapter.out.ReportsClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BootcampServiceTest {

    @Mock
    private BootcampRepositoryPort repository;
    
    @Mock
    private CapacidadesClient capacidadesClient;
    
    @Mock
    private ReportsClient reportsClient;
    
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private BootcampService bootcampService;

    @BeforeEach
    void setUp() {
        bootcampService = new BootcampService(repository, capacidadesClient, reportsClient, eventPublisher);
    }

    // ========== TESTS PARA findById ==========

    @Test
    void findById_Success() {
        // Arrange
        Long bootcampId = 1L;
        Bootcamp bootcamp = createTestBootcamp(bootcampId);
        List<CapacidadDTO> capacidades = Arrays.asList(
            new CapacidadDTO(1L, "Java", Arrays.asList(new TecnologiaDTO(1L, "Java"))),
            new CapacidadDTO(2L, "Spring", Arrays.asList(new TecnologiaDTO(2L, "Spring")))
        );

        when(repository.findById(bootcampId)).thenReturn(Mono.just(bootcamp));
        when(capacidadesClient.getById(bootcampId)).thenReturn(Flux.fromIterable(capacidades));

        // Act & Assert
        StepVerifier.create(bootcampService.findById(bootcampId))
                .expectNextMatches(response -> 
                    response.getId().equals(bootcampId) &&
                    response.getNombre().equals("Test Bootcamp") &&
                    response.getCapacidades().size() == 2
                )
                .verifyComplete();

        verify(repository).findById(bootcampId);
        verify(capacidadesClient).getById(bootcampId);
    }

    @Test
    void findById_NotFound() {
        // Arrange
        Long bootcampId = 999L;
        when(repository.findById(bootcampId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(bootcampService.findById(bootcampId))
                .expectError(BootcampNotFoundException.class)
                .verify();

        verify(repository).findById(bootcampId);
        verify(capacidadesClient, never()).getById(any());
    }

    @Test
    void findById_ExternalServiceError() {
        // Arrange
        Long bootcampId = 1L;
        Bootcamp bootcamp = createTestBootcamp(bootcampId);
        
        when(repository.findById(bootcampId)).thenReturn(Mono.just(bootcamp));
        when(capacidadesClient.getById(bootcampId)).thenReturn(Flux.error(new RuntimeException("Service unavailable")));

        // Act & Assert
        StepVerifier.create(bootcampService.findById(bootcampId))
                .expectError(ExternalServiceException.class)
                .verify();

        verify(repository).findById(bootcampId);
        verify(capacidadesClient).getById(bootcampId);
    }

    // ========== TESTS PARA register ==========

    @Test
    void register_Success() {
        // Arrange
        BootcampRequest request = createValidBootcampRequest();
        Bootcamp savedBootcamp = createTestBootcamp(1L);
        
        when(capacidadesClient.existsCapsById(1L)).thenReturn(Mono.just(true));
        when(capacidadesClient.existsCapsById(2L)).thenReturn(Mono.just(true));
        when(repository.save(any(Bootcamp.class))).thenReturn(Mono.just(savedBootcamp));
        when(capacidadesClient.postCapacidadesByBootcampId(anyLong(), anyList())).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(bootcampService.register(request))
                .expectNextMatches(bootcamp -> 
                    bootcamp.getId().equals(1L) &&
                    bootcamp.getNombre().equals("Test Bootcamp")
                )
                .verifyComplete();

        verify(repository).save(any(Bootcamp.class));
        verify(capacidadesClient).postCapacidadesByBootcampId(1L, Arrays.asList(1L, 2L));
        verify(eventPublisher).publishEvent(any(BootcampCreatedEvent.class));
    }

    @Test
    void register_InvalidCapacidadesQuantity() {
        // Arrange
        BootcampRequest request = createValidBootcampRequest();
        request.setCapacidades(Arrays.asList(1L, 2L, 3L, 4L, 5L)); // Más de 4 capacidades

        // Act & Assert
        StepVerifier.create(bootcampService.register(request))
                .expectError(BootcampValidationException.class)
                .verify();

        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void register_DuplicateCapacidades() {
        // Arrange
        BootcampRequest request = createValidBootcampRequest();
        request.setCapacidades(Arrays.asList(1L, 1L, 2L)); // Capacidades duplicadas

        // Act & Assert
        StepVerifier.create(bootcampService.register(request))
                .expectError(BootcampValidationException.class)
                .verify();

        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void register_CapacidadesNotFound() {
        // Arrange
        BootcampRequest request = createValidBootcampRequest();
        
        when(capacidadesClient.existsCapsById(1L)).thenReturn(Mono.just(false));
        when(capacidadesClient.existsCapsById(2L)).thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(bootcampService.register(request))
                .expectError(BootcampValidationException.class)
                .verify();

        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void register_ExternalServiceError() {
        // Arrange
        BootcampRequest request = createValidBootcampRequest();
        
        when(capacidadesClient.existsCapsById(anyLong())).thenReturn(Mono.error(new RuntimeException("Service error")));

        // Act & Assert
        StepVerifier.create(bootcampService.register(request))
                .expectError(ExternalServiceException.class)
                .verify();

        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    // ========== TESTS PARA delete ==========

    @Test
    void delete_Success() {
        // Arrange
        Long bootcampId = 1L;
        Bootcamp bootcamp = createTestBootcamp(bootcampId);
        
        when(repository.findById(bootcampId)).thenReturn(Mono.just(bootcamp));
        when(capacidadesClient.delete(bootcampId)).thenReturn(Mono.empty());
        when(repository.deleteById(bootcampId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(bootcampService.delete(bootcampId))
                .verifyComplete();

        verify(repository).findById(bootcampId);
        verify(capacidadesClient).delete(bootcampId);
        verify(repository).deleteById(bootcampId);
    }

    @Test
    void delete_BootcampNotFound() {
        // Arrange
        Long bootcampId = 999L;
        when(repository.findById(bootcampId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(bootcampService.delete(bootcampId))
                .expectError(BootcampNotFoundException.class)
                .verify();

        verify(repository).findById(bootcampId);
        verify(capacidadesClient, never()).delete(any());
        verify(repository, never()).deleteById(any());
    }

    @Test
    void delete_ExternalServiceError() {
        // Arrange
        Long bootcampId = 1L;
        Bootcamp bootcamp = createTestBootcamp(bootcampId);
        
        when(repository.findById(bootcampId)).thenReturn(Mono.just(bootcamp));
        when(capacidadesClient.delete(bootcampId)).thenReturn(Mono.error(new RuntimeException("Service error")));

        // Act & Assert
        StepVerifier.create(bootcampService.delete(bootcampId))
                .expectError(ExternalServiceException.class)
                .verify();

        verify(repository).findById(bootcampId);
        verify(capacidadesClient).delete(bootcampId);
        verify(repository, never()).deleteById(any());
    }

    // ========== TESTS PARA findAll ==========

    @Test
    void findAll_Success() {
        // Arrange
        PageAndQuery consult = new PageAndQuery(0, 10, "nombre", "asc");
        List<Bootcamp> bootcamps = Arrays.asList(
            createTestBootcamp(1L),
            createTestBootcamp(2L)
        );
        List<CapacidadDTO> capacidades = Arrays.asList(
            new CapacidadDTO(1L, "Java", Arrays.asList(new TecnologiaDTO(1L, "Java")))
        );

        when(repository.findPagedByNombreAsc(10, 0)).thenReturn(Flux.fromIterable(bootcamps));
        when(capacidadesClient.getById(anyLong())).thenReturn(Flux.fromIterable(capacidades));

        // Act & Assert
        StepVerifier.create(bootcampService.findAll(consult))
                .expectNextCount(2)
                .verifyComplete();

        verify(repository).findPagedByNombreAsc(10, 0);
        verify(capacidadesClient, times(2)).getById(anyLong());
    }

    // ========== TESTS PARA findByIdSimpleResponse ==========

    @Test
    void findByIdSimpleResponse_Success() {
        // Arrange
        List<Long> ids = Arrays.asList(1L, 2L);
        List<Bootcamp> bootcamps = Arrays.asList(
            createTestBootcamp(1L),
            createTestBootcamp(2L)
        );

        when(repository.findByIds(ids)).thenReturn(Flux.fromIterable(bootcamps));

        // Act & Assert
        StepVerifier.create(bootcampService.findByIdSimpleResponse(ids))
                .expectNextCount(2)
                .verifyComplete();

        verify(repository).findByIds(ids);
    }

    // ========== MÉTODOS AUXILIARES ==========

    private Bootcamp createTestBootcamp(Long id) {
        Bootcamp bootcamp = new Bootcamp();
        bootcamp.setId(id);
        bootcamp.setNombre("Test Bootcamp");
        bootcamp.setDescripcion("Test Description");
        bootcamp.setDuracion(30);
        bootcamp.setFechaLanzamiento(LocalDate.now());
        bootcamp.setFechaFinalizacion(LocalDate.now().plusDays(30));
        return bootcamp;
    }

    private BootcampRequest createValidBootcampRequest() {
        BootcampRequest request = new BootcampRequest();
        request.setNombre("Test Bootcamp");
        request.setDescripcion("Test Description");
        request.setDuracion(30);
        request.setFechaLanzamiento(LocalDate.now());
        request.setCapacidades(Arrays.asList(1L, 2L));
        return request;
    }
} 