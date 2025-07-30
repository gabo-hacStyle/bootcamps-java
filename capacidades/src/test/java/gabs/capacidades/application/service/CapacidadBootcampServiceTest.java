package gabs.capacidades.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gabs.capacidades.application.service.CapacidadBootcampService;
import gabs.capacidades.domain.exception.BootcampNotFoundException;
import gabs.capacidades.domain.exception.CapacidadNotFoundException;
import gabs.capacidades.domain.exception.ValidationException;
import gabs.capacidades.domain.model.Capacidad;
import gabs.capacidades.domain.model.CapacidadBootcamp;
import gabs.capacidades.domain.port.CapacidadBootcampRepositoryPort;
import gabs.capacidades.domain.port.CapacidadRepositoryPort;
import gabs.capacidades.dto.Tecnologias;
import gabs.capacidades.infraestructure.adapter.in.TecnologiaClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CapacidadBootcampServiceTest {

    @Mock
    private CapacidadBootcampRepositoryPort repository;

    @Mock
    private CapacidadRepositoryPort capacidadRepository;

    @Mock
    private TecnologiaClient tecnologiaClient;

    private CapacidadBootcampService service;

    @BeforeEach
    void setUp() {
        service = new CapacidadBootcampService(repository, capacidadRepository, tecnologiaClient);
    }

    // ========== TESTS PARA getAllByBootcamp ==========

    @Test
    void getAllByBootcamp_WhenBootcampExists_ShouldReturnCapacidadesWithTecnologias() {
        // Given
        Long bootcampId = 1L;
        
        CapacidadBootcamp capacidadBootcamp = new CapacidadBootcamp();
        capacidadBootcamp.setBootcampId(bootcampId);
        capacidadBootcamp.setCapacidadId(1L);

        Capacidad capacidad = new Capacidad();
        capacidad.setId(1L);
        capacidad.setNombre("Test Capacidad");
        capacidad.setDescripcion("Test Description");

        Tecnologias tecnologia = new Tecnologias(1L, "Java");

        when(repository.findByBootcampId(bootcampId)).thenReturn(Flux.just(capacidadBootcamp));
        when(capacidadRepository.findById(1L)).thenReturn(Mono.just(capacidad));
        when(tecnologiaClient.getTecnologiasByCapacidadId(1L))
                .thenReturn(Flux.just(tecnologia));

        // When & Then
        StepVerifier.create(service.getAllByBootcamp(bootcampId))
                .expectNextMatches(response -> 
                    response.getId().equals(1L) && 
                    response.getNombre().equals("Test Capacidad") &&
                    response.getTecnologias().size() == 1 &&
                    response.getTecnologias().get(0).getNombre().equals("Java"))
                .verifyComplete();
    }

    @Test
    void getAllByBootcamp_WhenBootcampNotFound_ShouldThrowBootcampNotFoundException() {
        // Given
        Long bootcampId = 999L;

        when(repository.findByBootcampId(bootcampId)).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(service.getAllByBootcamp(bootcampId))
                .expectError(BootcampNotFoundException.class)
                .verify();
    }

    @Test
    void getAllByBootcamp_WhenCapacidadNotFound_ShouldThrowCapacidadNotFoundException() {
        // Given
        Long bootcampId = 1L;
        
        CapacidadBootcamp capacidadBootcamp = new CapacidadBootcamp();
        capacidadBootcamp.setBootcampId(bootcampId);
        capacidadBootcamp.setCapacidadId(999L); // Capacidad inexistente

        when(repository.findByBootcampId(bootcampId)).thenReturn(Flux.just(capacidadBootcamp));
        when(capacidadRepository.findById(999L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.getAllByBootcamp(bootcampId))
                .expectError(CapacidadNotFoundException.class)
                .verify();
    }

    @Test
    void getAllByBootcamp_WhenMultipleCapacidades_ShouldReturnAll() {
        // Given
        Long bootcampId = 1L;
        
        CapacidadBootcamp capacidadBootcamp1 = new CapacidadBootcamp();
        capacidadBootcamp1.setBootcampId(bootcampId);
        capacidadBootcamp1.setCapacidadId(1L);

        CapacidadBootcamp capacidadBootcamp2 = new CapacidadBootcamp();
        capacidadBootcamp2.setBootcampId(bootcampId);
        capacidadBootcamp2.setCapacidadId(2L);

        Capacidad capacidad1 = new Capacidad();
        capacidad1.setId(1L);
        capacidad1.setNombre("Capacidad 1");

        Capacidad capacidad2 = new Capacidad();
        capacidad2.setId(2L);
        capacidad2.setNombre("Capacidad 2");

        Tecnologias tecnologia = new Tecnologias(1L, "Java");

        when(repository.findByBootcampId(bootcampId))
                .thenReturn(Flux.just(capacidadBootcamp1, capacidadBootcamp2));
        when(capacidadRepository.findById(1L)).thenReturn(Mono.just(capacidad1));
        when(capacidadRepository.findById(2L)).thenReturn(Mono.just(capacidad2));
        when(tecnologiaClient.getTecnologiasByCapacidadId(anyLong()))
                .thenReturn(Flux.just(tecnologia));

        // When & Then
        StepVerifier.create(service.getAllByBootcamp(bootcampId))
                .expectNextCount(2)
                .verifyComplete();
    }

    // ========== TESTS PARA saveCapacidadBootcamp ==========

    @Test
    void saveCapacidadBootcamp_WhenValidRequest_ShouldSaveAllCapacidades() {
        // Given
        Long bootcampId = 1L;
        List<Long> capacidadesIds = List.of(1L, 2L, 3L);

        Capacidad capacidad1 = new Capacidad();
        capacidad1.setId(1L);

        Capacidad capacidad2 = new Capacidad();
        capacidad2.setId(2L);

        Capacidad capacidad3 = new Capacidad();
        capacidad3.setId(3L);

        CapacidadBootcamp savedCapacidadBootcamp = new CapacidadBootcamp();
        savedCapacidadBootcamp.setBootcampId(bootcampId);
        savedCapacidadBootcamp.setCapacidadId(1L);

        when(capacidadRepository.findById(1L)).thenReturn(Mono.just(capacidad1));
        when(capacidadRepository.findById(2L)).thenReturn(Mono.just(capacidad2));
        when(capacidadRepository.findById(3L)).thenReturn(Mono.just(capacidad3));
        when(repository.save(any(CapacidadBootcamp.class))).thenReturn(Mono.just(savedCapacidadBootcamp));

        // When & Then
        StepVerifier.create(service.saveCapacidadBootcamp(bootcampId, capacidadesIds))
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void saveCapacidadBootcamp_WhenEmptyList_ShouldThrowValidationException() {
        // Given
        Long bootcampId = 1L;
        List<Long> capacidadesIds = List.of();

        // When & Then
        StepVerifier.create(service.saveCapacidadBootcamp(bootcampId, capacidadesIds))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void saveCapacidadBootcamp_WhenNullList_ShouldThrowValidationException() {
        // Given
        Long bootcampId = 1L;
        List<Long> capacidadesIds = null;

        // When & Then
        StepVerifier.create(service.saveCapacidadBootcamp(bootcampId, capacidadesIds))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void saveCapacidadBootcamp_WhenCapacidadNotFound_ShouldThrowCapacidadNotFoundException() {
        // Given
        Long bootcampId = 1L;
        List<Long> capacidadesIds = List.of(999L); // Capacidad inexistente

        when(capacidadRepository.findById(999L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.saveCapacidadBootcamp(bootcampId, capacidadesIds))
                .expectError(CapacidadNotFoundException.class)
                .verify();
    }

    @Test
    void saveCapacidadBootcamp_WhenSomeCapacidadesNotFound_ShouldThrowCapacidadNotFoundException() {
        // Given
        Long bootcampId = 1L;
        List<Long> capacidadesIds = List.of(1L, 999L); // Una existe, otra no

        Capacidad capacidad1 = new Capacidad();
        capacidad1.setId(1L);

        when(capacidadRepository.findById(1L)).thenReturn(Mono.just(capacidad1));
        when(capacidadRepository.findById(999L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.saveCapacidadBootcamp(bootcampId, capacidadesIds))
                .expectError(CapacidadNotFoundException.class)
                .verify();
    }

    // ========== TESTS PARA deleteCapacidadesByBootcampId ==========

    @Test
    void deleteCapacidadesByBootcampId_WhenBootcampExists_ShouldDeleteSuccessfully() {
        // Given
        Long bootcampId = 1L;
        
        CapacidadBootcamp capacidadBootcamp = new CapacidadBootcamp();
        capacidadBootcamp.setBootcampId(bootcampId);
        capacidadBootcamp.setCapacidadId(1L);

        List<Long> exclusiveCapacidadesIds = List.of(1L, 2L);

        when(repository.findByBootcampId(bootcampId)).thenReturn(Flux.just(capacidadBootcamp));
        when(repository.findExclusiveCapacidadesOfBootcamp(bootcampId))
                .thenReturn(Flux.fromIterable(exclusiveCapacidadesIds));
        when(tecnologiaClient.deleteTechnologiasByCapacidadDeleted(exclusiveCapacidadesIds))
                .thenReturn(Mono.empty());
        when(capacidadRepository.deleteById(1L)).thenReturn(Mono.empty());
        when(capacidadRepository.deleteById(2L)).thenReturn(Mono.empty());
        when(repository.deleteByBootcampId(bootcampId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.deleteCapacidadesByBootcampId(bootcampId))
                .verifyComplete();
    }

    @Test
    void deleteCapacidadesByBootcampId_WhenBootcampNotFound_ShouldThrowBootcampNotFoundException() {
        // Given
        Long bootcampId = 999L;

        when(repository.findByBootcampId(bootcampId)).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(service.deleteCapacidadesByBootcampId(bootcampId))
                .expectError(BootcampNotFoundException.class)
                .verify();
    }

    @Test
    void deleteCapacidadesByBootcampId_WhenNoExclusiveCapacidades_ShouldDeleteOnlyBootcamp() {
        // Given
        Long bootcampId = 1L;
        
        CapacidadBootcamp capacidadBootcamp = new CapacidadBootcamp();
        capacidadBootcamp.setBootcampId(bootcampId);
        capacidadBootcamp.setCapacidadId(1L);

        when(repository.findByBootcampId(bootcampId)).thenReturn(Flux.just(capacidadBootcamp));
        when(repository.findExclusiveCapacidadesOfBootcamp(bootcampId))
                .thenReturn(Flux.empty());
        when(repository.deleteByBootcampId(bootcampId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.deleteCapacidadesByBootcampId(bootcampId))
                .verifyComplete();
    }

    @Test
    void deleteCapacidadesByBootcampId_WhenTecnologiaClientFails_ShouldPropagateError() {
        // Given
        Long bootcampId = 1L;
        
        CapacidadBootcamp capacidadBootcamp = new CapacidadBootcamp();
        capacidadBootcamp.setBootcampId(bootcampId);
        capacidadBootcamp.setCapacidadId(1L);

        List<Long> exclusiveCapacidadesIds = List.of(1L);

        when(repository.findByBootcampId(bootcampId)).thenReturn(Flux.just(capacidadBootcamp));
        when(repository.findExclusiveCapacidadesOfBootcamp(bootcampId))
                .thenReturn(Flux.fromIterable(exclusiveCapacidadesIds));
        when(tecnologiaClient.deleteTechnologiasByCapacidadDeleted(exclusiveCapacidadesIds))
                .thenReturn(Mono.error(new RuntimeException("Error de red")));

        // When & Then
        StepVerifier.create(service.deleteCapacidadesByBootcampId(bootcampId))
                .expectError(RuntimeException.class)
                .verify();
    }
} 