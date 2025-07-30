package gabs.tecnologias.application.service;

import gabs.tecnologias.domain.model.Tecnologia;
import gabs.tecnologias.domain.port.TecnologiaRepositoryPort;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TecnologiaServiceTest {

    @Mock
    private TecnologiaRepositoryPort repository;

    @InjectMocks
    private TecnologiaService service;

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
    void findAll_ShouldReturnAllTechnologies() {
        // Given
        List<Tecnologia> tecnologias = Arrays.asList(tecnologia1, tecnologia2);
        when(repository.findAll()).thenReturn(Flux.fromIterable(tecnologias));

        // When & Then
        StepVerifier.create(service.findAll())
                .expectNext(tecnologia1)
                .expectNext(tecnologia2)
                .verifyComplete();
    }

    @Test
    void findById_WhenTechnologyExists_ShouldReturnTechnology() {
        // Given
        when(repository.findById(1L)).thenReturn(Mono.just(tecnologia1));

        // When & Then
        StepVerifier.create(service.findById(1L))
                .expectNext(tecnologia1)
                .verifyComplete();
    }

    @Test
    void findById_WhenTechnologyDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(repository.findById(999L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.findById(999L))
                .verifyComplete();
    }

    @Test
    void existsById_WhenTechnologyExists_ShouldReturnTrue() {
        // Given
        when(repository.existsById(1L)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(service.existsById(1L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsById_WhenTechnologyDoesNotExist_ShouldReturnFalse() {
        // Given
        when(repository.existsById(999L)).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(service.existsById(999L))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void create_WhenNameDoesNotExist_ShouldCreateTechnology() {
        // Given
        Tecnologia newTecnologia = new Tecnologia();
        newTecnologia.setNombre("Python");
        newTecnologia.setDescripcion("Lenguaje de programación interpretado");

        when(repository.existsByNombre("Python")).thenReturn(Mono.just(false));
        when(repository.save(any(Tecnologia.class))).thenReturn(Mono.just(newTecnologia));

        // When & Then
        StepVerifier.create(service.create(newTecnologia))
                .expectNext(newTecnologia)
                .verifyComplete();
    }

    @Test
    void create_WhenNameAlreadyExists_ShouldThrowException() {
        // Given
        Tecnologia newTecnologia = new Tecnologia();
        newTecnologia.setNombre("Java");
        newTecnologia.setDescripcion("Descripción");

        when(repository.existsByNombre("Java")).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(service.create(newTecnologia))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void updateParcial_WhenTechnologyExists_ShouldUpdateTechnology() {
        // Given
        Tecnologia cambios = new Tecnologia();
        cambios.setNombre("Java Updated");
        cambios.setDescripcion("Nueva descripción");

        Tecnologia original = new Tecnologia();
        original.setId(1L);
        original.setNombre("Java");
        original.setDescripcion("Descripción original");

        Tecnologia expected = new Tecnologia();
        expected.setId(1L);
        expected.setNombre("Java Updated");
        expected.setDescripcion("Nueva descripción");

        when(repository.findById(1L)).thenReturn(Mono.just(original));
        when(repository.save(any(Tecnologia.class))).thenReturn(Mono.just(expected));

        // When & Then
        StepVerifier.create(service.updateParcial(1L, cambios))
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void updateParcial_WhenTechnologyDoesNotExist_ShouldReturnEmpty() {
        // Given
        Tecnologia cambios = new Tecnologia();
        cambios.setNombre("Java Updated");

        when(repository.findById(999L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.updateParcial(999L, cambios))
                .verifyComplete();
    }

    @Test
    void updateParcial_WithPartialChanges_ShouldUpdateOnlyProvidedFields() {
        // Given
        Tecnologia cambios = new Tecnologia();
        cambios.setNombre("Java Updated");
        // descripción no se proporciona

        Tecnologia original = new Tecnologia();
        original.setId(1L);
        original.setNombre("Java");
        original.setDescripcion("Descripción original");

        Tecnologia expected = new Tecnologia();
        expected.setId(1L);
        expected.setNombre("Java Updated");
        expected.setDescripcion("Descripción original"); // No cambia

        when(repository.findById(1L)).thenReturn(Mono.just(original));
        when(repository.save(any(Tecnologia.class))).thenReturn(Mono.just(expected));

        // When & Then
        StepVerifier.create(service.updateParcial(1L, cambios))
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void findByNombre_WhenTechnologyExists_ShouldReturnTechnology() {
        // Given
        when(repository.findByNombre("Java")).thenReturn(Mono.just(tecnologia1));

        // When & Then
        StepVerifier.create(service.findByNombre("Java"))
                .expectNext(tecnologia1)
                .verifyComplete();
    }

    @Test
    void findByNombre_WhenTechnologyDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(repository.findByNombre("Inexistente")).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.findByNombre("Inexistente"))
                .verifyComplete();
    }

    @Test
    void delete_ShouldDeleteTechnology() {
        // Given
        when(repository.deleteById(1L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(service.delete(1L))
                .verifyComplete();
    }
} 