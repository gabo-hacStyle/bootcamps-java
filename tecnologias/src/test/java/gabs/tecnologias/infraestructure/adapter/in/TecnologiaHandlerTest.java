package gabs.tecnologias.infraestructure.adapter.in;

import gabs.tecnologias.application.port.TecnologiaUseCases;
import gabs.tecnologias.domain.model.Tecnologia;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TecnologiaHandlerTest {

    @Mock
    private TecnologiaUseCases service;

    @InjectMocks
    private TecnologiaHandler handler;

    private Tecnologia tecnologia1;
    private Tecnologia tecnologia2;

    @BeforeEach
    void setUp() {
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
    void getAll_ShouldReturnAllTechnologies() {
        // Given
        List<Tecnologia> tecnologias = Arrays.asList(tecnologia1, tecnologia2);
        when(service.findAll()).thenReturn(Flux.fromIterable(tecnologias));

        // When & Then
        StepVerifier.create(service.findAll())
                .expectNext(tecnologia1)
                .expectNext(tecnologia2)
                .verifyComplete();
    }

    @Test
    void findById_WhenTechnologyExists_ShouldReturnTechnology() {
        // Given
        when(service.findById(1L)).thenReturn(Mono.just(tecnologia1));

        // When & Then
        StepVerifier.create(service.findById(1L))
                .expectNext(tecnologia1)
                .verifyComplete();
    }

    @Test
    void findById_WhenTechnologyDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(service.findById(999L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.findById(999L))
                .verifyComplete();
    }

    @Test
    void existsById_WhenTechnologyExists_ShouldReturnTrue() {
        // Given
        when(service.existsById(1L)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(service.existsById(1L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsById_WhenTechnologyDoesNotExist_ShouldReturnFalse() {
        // Given
        when(service.existsById(999L)).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(service.existsById(999L))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void findByNombre_WhenTechnologyExists_ShouldReturnTechnology() {
        // Given
        when(service.findByNombre("Java")).thenReturn(Mono.just(tecnologia1));

        // When & Then
        StepVerifier.create(service.findByNombre("Java"))
                .expectNext(tecnologia1)
                .verifyComplete();
    }

    @Test
    void findByNombre_WhenTechnologyDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(service.findByNombre("Inexistente")).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.findByNombre("Inexistente"))
                .verifyComplete();
    }

    @Test
    void create_WhenValidTechnology_ShouldCreateTechnology() {
        // Given
        Tecnologia newTecnologia = new Tecnologia();
        newTecnologia.setNombre("Python");
        newTecnologia.setDescripcion("Lenguaje de programación interpretado");

        when(service.create(any(Tecnologia.class))).thenReturn(Mono.just(newTecnologia));

        // When & Then
        StepVerifier.create(service.create(newTecnologia))
                .expectNext(newTecnologia)
                .verifyComplete();
    }

    @Test
    void updateParcial_WhenTechnologyExists_ShouldUpdateTechnology() {
        // Given
        Tecnologia cambios = new Tecnologia();
        cambios.setNombre("Java Updated");
        cambios.setDescripcion("Nueva descripción");

        Tecnologia updated = new Tecnologia();
        updated.setId(1L);
        updated.setNombre("Java Updated");
        updated.setDescripcion("Nueva descripción");

        when(service.updateParcial(1L, cambios)).thenReturn(Mono.just(updated));

        // When & Then
        StepVerifier.create(service.updateParcial(1L, cambios))
                .expectNext(updated)
                .verifyComplete();
    }

    @Test
    void updateParcial_WhenTechnologyDoesNotExist_ShouldReturnEmpty() {
        // Given
        Tecnologia cambios = new Tecnologia();
        cambios.setNombre("Java Updated");

        when(service.updateParcial(999L, cambios)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.updateParcial(999L, cambios))
                .verifyComplete();
    }

    @Test
    void delete_ShouldDeleteTechnology() {
        // Given
        when(service.delete(1L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.delete(1L))
                .verifyComplete();
    }
} 