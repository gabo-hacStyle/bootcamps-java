package gabs.tecnologias.application.service;

import gabs.tecnologias.domain.model.CapacidadTecnologia;
import gabs.tecnologias.domain.model.Tecnologia;
import gabs.tecnologias.domain.port.CapacidadTecnologiaRepositoryPort;
import gabs.tecnologias.domain.port.TecnologiaRepositoryPort;
import gabs.tecnologias.dto.CapacidadTecnologiaResponse;

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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CapacidadTecnologiaServiceTest {

    @Mock
    private CapacidadTecnologiaRepositoryPort repository;

    @Mock
    private TecnologiaRepositoryPort tecnologiaRepository;

    @InjectMocks
    private CapacidadTecnologiaService service;

    private CapacidadTecnologia capacidadTecnologia1;
    private CapacidadTecnologia capacidadTecnologia2;
    private Tecnologia tecnologia1;
    private Tecnologia tecnologia2;

    @BeforeEach
    void setUp() {
        capacidadTecnologia1 = new CapacidadTecnologia();
        capacidadTecnologia1.setId(1L);
        capacidadTecnologia1.setCapacidadId(1L);
        capacidadTecnologia1.setTecnologiaId(1L);

        capacidadTecnologia2 = new CapacidadTecnologia();
        capacidadTecnologia2.setId(2L);
        capacidadTecnologia2.setCapacidadId(1L);
        capacidadTecnologia2.setTecnologiaId(2L);

        tecnologia1 = new Tecnologia();
        tecnologia1.setId(1L);
        tecnologia1.setNombre("Java");
        tecnologia1.setDescripcion("Lenguaje de programación orientado a objetos");

        tecnologia2 = new Tecnologia();
        tecnologia2.setId(2L);
        tecnologia2.setNombre("Spring Boot");
        tecnologia2.setDescripcion("Framework para desarrollo de aplicaciones Java");
    }

    @Test
    void getTechnologiesListByCapacidad_ShouldReturnTechnologiesList() {
        // Given
        Long capacidadId = 1L;
        List<CapacidadTecnologia> capacidades = Arrays.asList(capacidadTecnologia1, capacidadTecnologia2);

        when(repository.findByCapacidadId(capacidadId))
                .thenReturn(Flux.fromIterable(capacidades));
        when(tecnologiaRepository.findById(1L))
                .thenReturn(Mono.just(tecnologia1));
        when(tecnologiaRepository.findById(2L))
                .thenReturn(Mono.just(tecnologia2));

        // When & Then
        StepVerifier.create(service.getTechnologiesListByCapacidad(capacidadId))
                .expectNextMatches(response -> 
                    response.getId().equals(1L) && 
                    response.getNombre().equals("Java"))
                .expectNextMatches(response -> 
                    response.getId().equals(2L) && 
                    response.getNombre().equals("Spring Boot"))
                .verifyComplete();
    }

    @Test
    void getTechnologiesListByCapacidad_WhenNoTechnologies_ShouldReturnEmpty() {
        // Given
        Long capacidadId = 999L;
        when(repository.findByCapacidadId(capacidadId))
                .thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(service.getTechnologiesListByCapacidad(capacidadId))
                .verifyComplete();
    }

    @Test
    void register_ShouldRegisterTechnologiesForCapacidad() {
        // Given
        Long capacidadId = 1L;
        List<Long> tecnologiaIds = Arrays.asList(1L, 2L);

        CapacidadTecnologia expected1 = new CapacidadTecnologia();
        expected1.setCapacidadId(capacidadId);
        expected1.setTecnologiaId(1L);

        CapacidadTecnologia expected2 = new CapacidadTecnologia();
        expected2.setCapacidadId(capacidadId);
        expected2.setTecnologiaId(2L);

        when(repository.save(any(CapacidadTecnologia.class)))
                .thenReturn(Mono.just(expected1))
                .thenReturn(Mono.just(expected2));

        // When & Then
        StepVerifier.create(service.register(capacidadId, tecnologiaIds))
                .expectNext(expected1)
                .expectNext(expected2)
                .verifyComplete();
    }

    @Test
    void register_WithEmptyList_ShouldReturnEmpty() {
        // Given
        Long capacidadId = 1L;
        List<Long> tecnologiaIds = Arrays.asList();

        // When & Then
        StepVerifier.create(service.register(capacidadId, tecnologiaIds))
                .verifyComplete();
    }

    @Test
    void register_WithSingleTechnology_ShouldRegisterOneTechnology() {
        // Given
        Long capacidadId = 1L;
        List<Long> tecnologiaIds = Arrays.asList(1L);

        CapacidadTecnologia expected = new CapacidadTecnologia();
        expected.setCapacidadId(capacidadId);
        expected.setTecnologiaId(1L);

        when(repository.save(any(CapacidadTecnologia.class)))
                .thenReturn(Mono.just(expected));

        // When & Then
        StepVerifier.create(service.register(capacidadId, tecnologiaIds))
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void deleteCapacidadesByCapacidadesIds_ShouldDeleteTechnologiesAndCapacidades() {
        // Given
        List<Long> capacidadesIds = Arrays.asList(1L, 2L);
        List<Long> tecnologiaIds = Arrays.asList(1L, 2L);

        when(repository.findExclusiveTechsByCapacidadesIds(capacidadesIds))
                .thenReturn(Flux.fromIterable(tecnologiaIds));
        when(tecnologiaRepository.deleteAllById(tecnologiaIds))
                .thenReturn(Mono.empty());
        when(repository.deleteByCapacidadesIds(capacidadesIds))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.deleteCapacidadesByCapacidadesIds(capacidadesIds))
                .verifyComplete();
    }

    @Test
    void deleteCapacidadesByCapacidadesIds_WithEmptyCapacidadesIds_ShouldComplete() {
        // Given
        List<Long> capacidadesIds = Arrays.asList();

        when(repository.findExclusiveTechsByCapacidadesIds(capacidadesIds))
                .thenReturn(Flux.empty());
        when(tecnologiaRepository.deleteAllById(anyList()))
                .thenReturn(Mono.empty());
        when(repository.deleteByCapacidadesIds(capacidadesIds))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.deleteCapacidadesByCapacidadesIds(capacidadesIds))
                .verifyComplete();
    }

    @Test
    void deleteCapacidadesByCapacidadesIds_WithNoExclusiveTechnologies_ShouldOnlyDeleteCapacidades() {
        // Given
        List<Long> capacidadesIds = Arrays.asList(1L, 2L);

        when(repository.findExclusiveTechsByCapacidadesIds(capacidadesIds))
                .thenReturn(Flux.empty());
        when(tecnologiaRepository.deleteAllById(anyList()))
                .thenReturn(Mono.empty());
        when(repository.deleteByCapacidadesIds(capacidadesIds))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.deleteCapacidadesByCapacidadesIds(capacidadesIds))
                .verifyComplete();
    }
} 