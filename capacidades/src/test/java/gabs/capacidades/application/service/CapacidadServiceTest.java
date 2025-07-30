package gabs.capacidades.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gabs.capacidades.application.service.CapacidadService;
import gabs.capacidades.domain.exception.CapacidadNotFoundException;
import gabs.capacidades.domain.exception.ValidationException;
import gabs.capacidades.domain.model.Capacidad;
import gabs.capacidades.domain.port.CapacidadRepositoryPort;
import gabs.capacidades.dto.CapacidadRequest;
import gabs.capacidades.dto.CapacidadResponse;
import gabs.capacidades.dto.PageAndQuery;
import gabs.capacidades.dto.Tecnologias;
import gabs.capacidades.infraestructure.adapter.in.TecnologiaClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CapacidadServiceTest {

    @Mock
    private CapacidadRepositoryPort repository;

    @Mock
    private TecnologiaClient tecnologiaClient;

    private CapacidadService service;

    @BeforeEach
    void setUp() {
        service = new CapacidadService(repository, tecnologiaClient);
    }

    // ========== TESTS PARA findById ==========

    @Test
    void findById_WhenCapacidadExists_ShouldReturnCapacidadWithTecnologias() {
        // Given
        Long id = 1L;
        Capacidad capacidad = new Capacidad();
        capacidad.setId(id);
        capacidad.setNombre("Test Capacidad");
        capacidad.setDescripcion("Test Description");

        Tecnologias tecnologia = new Tecnologias(1L, "Java");

        when(repository.findById(id)).thenReturn(Mono.just(capacidad));
        when(tecnologiaClient.getTecnologiasByCapacidadId(id))
                .thenReturn(Flux.just(tecnologia));

        // When & Then
        StepVerifier.create(service.findById(id))
                .expectNextMatches(response -> 
                    response.getId().equals(id) && 
                    response.getNombre().equals("Test Capacidad") &&
                    response.getDescripcion().equals("Test Description") &&
                    response.getTecnologiasList().size() == 1 &&
                    response.getTecnologiasList().get(0).getNombre().equals("Java"))
                .verifyComplete();
    }

    @Test
    void findById_WhenCapacidadNotFound_ShouldThrowCapacidadNotFoundException() {
        // Given
        Long id = 999L;

        when(repository.findById(id)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.findById(id))
                .expectError(CapacidadNotFoundException.class)
                .verify();
    }

    @Test
    void findById_WhenTecnologiaClientFails_ShouldPropagateError() {
        // Given
        Long id = 1L;
        Capacidad capacidad = new Capacidad();
        capacidad.setId(id);

        when(repository.findById(id)).thenReturn(Mono.just(capacidad));
        when(tecnologiaClient.getTecnologiasByCapacidadId(id))
                .thenReturn(Flux.error(new RuntimeException("Error de red")));

        // When & Then
        StepVerifier.create(service.findById(id))
                .expectError(RuntimeException.class)
                .verify();
    }

    // ========== TESTS PARA register ==========

    @Test
    void register_WhenValidRequest_ShouldReturnSavedCapacidad() {
        // Given
        CapacidadRequest request = new CapacidadRequest();
        request.setNombre("Test");
        request.setDescripcion("Test Description");
        request.setTecnologias(List.of(1L, 2L, 3L));

        Capacidad savedCapacidad = new Capacidad();
        savedCapacidad.setId(1L);
        savedCapacidad.setNombre("Test");

        when(tecnologiaClient.existsTechById(anyLong())).thenReturn(Mono.just(true));
        when(repository.save(any(Capacidad.class))).thenReturn(Mono.just(savedCapacidad));
        when(tecnologiaClient.postTecnologiasByCapacidadId(anyLong(), any()))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.register(request))
                .expectNextMatches(capacidad -> 
                    capacidad.getId().equals(1L) && 
                    capacidad.getNombre().equals("Test"))
                .verifyComplete();
    }

    @Test
    void register_WhenInvalidTechQuantity_ShouldThrowValidationException() {
        // Given
        CapacidadRequest request = new CapacidadRequest();
        request.setNombre("Test");
        request.setTecnologias(List.of(1L, 2L)); // Solo 2 tecnologías

        // When & Then
        StepVerifier.create(service.register(request))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void register_WhenTooManyTechs_ShouldThrowValidationException() {
        // Given
        CapacidadRequest request = new CapacidadRequest();
        request.setNombre("Test");
        request.setTecnologias(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 
                                       11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L)); // 21 tecnologías

        // When & Then
        StepVerifier.create(service.register(request))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void register_WhenDuplicateTechs_ShouldThrowValidationException() {
        // Given
        CapacidadRequest request = new CapacidadRequest();
        request.setNombre("Test");
        request.setTecnologias(List.of(1L, 1L, 2L)); // Tecnologías duplicadas

        // When & Then
        StepVerifier.create(service.register(request))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void register_WhenTechDoesNotExist_ShouldThrowValidationException() {
        // Given
        CapacidadRequest request = new CapacidadRequest();
        request.setNombre("Test");
        request.setTecnologias(List.of(1L, 2L, 999L)); // Tecnología 999 no existe

        when(tecnologiaClient.existsTechById(1L)).thenReturn(Mono.just(true));
        when(tecnologiaClient.existsTechById(2L)).thenReturn(Mono.just(true));
        when(tecnologiaClient.existsTechById(999L)).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(service.register(request))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void register_WhenNullTecnologias_ShouldThrowValidationException() {
        // Given
        CapacidadRequest request = new CapacidadRequest();
        request.setNombre("Test");
        request.setTecnologias(null);

        // When & Then
        StepVerifier.create(service.register(request))
                .expectError(ValidationException.class)
                .verify();
    }

    // ========== TESTS PARA updateParcial ==========

    @Test
    void updateParcial_WhenCapacidadExists_ShouldReturnUpdatedCapacidad() {
        // Given
        Long id = 1L;
        CapacidadRequest request = new CapacidadRequest();
        request.setNombre("Updated Test");
        request.setDescripcion("Updated Description");

        Capacidad existingCapacidad = new Capacidad();
        existingCapacidad.setId(id);
        existingCapacidad.setNombre("Original");
        existingCapacidad.setDescripcion("Original Description");

        Capacidad updatedCapacidad = new Capacidad();
        updatedCapacidad.setId(id);
        updatedCapacidad.setNombre("Updated Test");
        updatedCapacidad.setDescripcion("Updated Description");

        when(repository.findById(id)).thenReturn(Mono.just(existingCapacidad));
        when(repository.save(any(Capacidad.class))).thenReturn(Mono.just(updatedCapacidad));

        // When & Then
        StepVerifier.create(service.updateParcial(id, request))
                .expectNextMatches(capacidad -> 
                    capacidad.getId().equals(id) && 
                    capacidad.getNombre().equals("Updated Test") &&
                    capacidad.getDescripcion().equals("Updated Description"))
                .verifyComplete();
    }

    @Test
    void updateParcial_WhenOnlyNombreProvided_ShouldUpdateOnlyNombre() {
        // Given
        Long id = 1L;
        CapacidadRequest request = new CapacidadRequest();
        request.setNombre("Updated Test");
        // No descripción

        Capacidad existingCapacidad = new Capacidad();
        existingCapacidad.setId(id);
        existingCapacidad.setNombre("Original");
        existingCapacidad.setDescripcion("Original Description");

        Capacidad updatedCapacidad = new Capacidad();
        updatedCapacidad.setId(id);
        updatedCapacidad.setNombre("Updated Test");
        updatedCapacidad.setDescripcion("Original Description"); // Sin cambios

        when(repository.findById(id)).thenReturn(Mono.just(existingCapacidad));
        when(repository.save(any(Capacidad.class))).thenReturn(Mono.just(updatedCapacidad));

        // When & Then
        StepVerifier.create(service.updateParcial(id, request))
                .expectNextMatches(capacidad -> 
                    capacidad.getId().equals(id) && 
                    capacidad.getNombre().equals("Updated Test") &&
                    capacidad.getDescripcion().equals("Original Description"))
                .verifyComplete();
    }

    @Test
    void updateParcial_WhenCapacidadNotFound_ShouldThrowException() {
        // Given
        Long id = 999L;
        CapacidadRequest request = new CapacidadRequest();

        when(repository.findById(id)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.updateParcial(id, request))
                .expectError(CapacidadNotFoundException.class)
                .verify();
    }

    // ========== TESTS PARA delete ==========

    @Test
    void delete_WhenCapacidadExists_ShouldCompleteSuccessfully() {
        // Given
        Long id = 1L;
        Capacidad capacidad = new Capacidad();
        capacidad.setId(id);

        when(repository.findById(id)).thenReturn(Mono.just(capacidad));
        when(repository.deleteById(id)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.delete(id))
                .verifyComplete();
    }

    @Test
    void delete_WhenCapacidadNotFound_ShouldThrowException() {
        // Given
        Long id = 999L;

        when(repository.findById(id)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.delete(id))
                .expectError(CapacidadNotFoundException.class)
                .verify();
    }

    // ========== TESTS PARA existsById ==========

    @Test
    void existsById_WhenCapacidadExists_ShouldReturnTrue() {
        // Given
        Long id = 1L;

        when(repository.existsById(id)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(service.existsById(id))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsById_WhenCapacidadNotExists_ShouldReturnFalse() {
        // Given
        Long id = 999L;

        when(repository.existsById(id)).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(service.existsById(id))
                .expectNext(false)
                .verifyComplete();
    }

    // ========== TESTS PARA findAll ==========

    @Test
    void findAll_WhenSortByNombreAsc_ShouldReturnOrderedResults() {
        // Given
        PageAndQuery consult = new PageAndQuery(0, 10, "nombre", "asc");

        Capacidad capacidad1 = new Capacidad();
        capacidad1.setId(1L);
        capacidad1.setNombre("A Capacidad");

        Capacidad capacidad2 = new Capacidad();
        capacidad2.setId(2L);
        capacidad2.setNombre("B Capacidad");

        Tecnologias tecnologia = new Tecnologias(1L, "Java");

        when(repository.findPagedByNombreAsc(10, 0)).thenReturn(Flux.just(capacidad1, capacidad2));
        when(tecnologiaClient.getTecnologiasByCapacidadId(anyLong()))
                .thenReturn(Flux.just(tecnologia));

        // When & Then
        StepVerifier.create(service.findAll(consult))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findAll_WhenSortByNombreDesc_ShouldReturnOrderedResults() {
        // Given
        PageAndQuery consult = new PageAndQuery(0, 10, "nombre", "desc");

        Capacidad capacidad1 = new Capacidad();
        capacidad1.setId(1L);
        capacidad1.setNombre("A Capacidad");

        Capacidad capacidad2 = new Capacidad();
        capacidad2.setId(2L);
        capacidad2.setNombre("B Capacidad");

        Tecnologias tecnologia = new Tecnologias(1L, "Java");

        when(repository.findPagedByNombreDesc(10, 0)).thenReturn(Flux.just(capacidad2, capacidad1));
        when(tecnologiaClient.getTecnologiasByCapacidadId(anyLong()))
                .thenReturn(Flux.just(tecnologia));

        // When & Then
        StepVerifier.create(service.findAll(consult))
                .expectNextCount(2)
                .verifyComplete();
    }
} 