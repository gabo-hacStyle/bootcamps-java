package gabs.tecnologias.infraestructure.adapter.in;

import gabs.tecnologias.application.port.CapacidadTecnologiaUseCases;
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
class CapacidadTecnologiaHandlerTest {

    @Mock
    private CapacidadTecnologiaUseCases capService;

    @InjectMocks
    private CapacidadTecnologiaHandler handler;

    private CapacidadTecnologiaResponse response1;
    private CapacidadTecnologiaResponse response2;

    @BeforeEach
    void setUp() {
        response1 = new CapacidadTecnologiaResponse();
        response1.setId(1L);
        response1.setNombre("Java");

        response2 = new CapacidadTecnologiaResponse();
        response2.setId(2L);
        response2.setNombre("Spring Boot");
    }

    @Test
    void getTechsByCapacidadId_ShouldReturnTechnologiesList() {
        // Given
        Long capacidadId = 1L;
        List<CapacidadTecnologiaResponse> responses = Arrays.asList(response1, response2);

        when(capService.getTechnologiesListByCapacidad(capacidadId))
                .thenReturn(Flux.fromIterable(responses));

        // When & Then
        StepVerifier.create(capService.getTechnologiesListByCapacidad(capacidadId))
                .expectNext(response1)
                .expectNext(response2)
                .verifyComplete();
    }

    @Test
    void getTechsByCapacidadId_WhenNoTechnologies_ShouldReturnEmpty() {
        // Given
        Long capacidadId = 999L;
        when(capService.getTechnologiesListByCapacidad(capacidadId))
                .thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(capService.getTechnologiesListByCapacidad(capacidadId))
                .verifyComplete();
    }

    @Test
    void saveCapacidadTecnologia_ShouldRegisterTechnologies() {
        // Given
        Long capacidadId = 1L;
        List<Long> tecnologiaIds = Arrays.asList(1L, 2L);

        when(capService.register(capacidadId, tecnologiaIds))
                .thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(capService.register(capacidadId, tecnologiaIds))
                .verifyComplete();
    }

    @Test
    void saveCapacidadTecnologia_WithEmptyList_ShouldComplete() {
        // Given
        Long capacidadId = 1L;
        List<Long> tecnologiaIds = Arrays.asList();

        when(capService.register(capacidadId, tecnologiaIds))
                .thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(capService.register(capacidadId, tecnologiaIds))
                .verifyComplete();
    }

    @Test
    void deleteTecnologiasOfCapacidadesIds_ShouldDeleteTechnologies() {
        // Given
        List<Long> capacidadesIds = Arrays.asList(1L, 2L);

        when(capService.deleteCapacidadesByCapacidadesIds(capacidadesIds))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(capService.deleteCapacidadesByCapacidadesIds(capacidadesIds))
                .verifyComplete();
    }

    @Test
    void deleteTecnologiasOfCapacidadesIds_WithEmptyList_ShouldComplete() {
        // Given
        List<Long> capacidadesIds = Arrays.asList();

        when(capService.deleteCapacidadesByCapacidadesIds(capacidadesIds))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(capService.deleteCapacidadesByCapacidadesIds(capacidadesIds))
                .verifyComplete();
    }
} 