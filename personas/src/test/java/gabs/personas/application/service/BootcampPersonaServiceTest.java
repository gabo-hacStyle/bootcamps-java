package gabs.personas.application.service;


import gabs.personas.domain.model.BootcampPersona;
import gabs.personas.domain.model.Persona;
import gabs.personas.domain.port.BootcampPersonaRepositoryPort;
import gabs.personas.domain.port.PersonaRepositoryPort;
import gabs.personas.dto.BootcampSimpleResponse;
import gabs.personas.dto.EnrollRequest;
import gabs.personas.dto.PersonaRegisteredResponse;
import gabs.personas.infraestructure.adapter.out.clients.BootcampClient;
import gabs.personas.infraestructure.adapter.out.clients.ReportClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;

public class BootcampPersonaServiceTest {
    private BootcampPersonaRepositoryPort repository;
    private PersonaRepositoryPort personaRepository;
    private BootcampClient bootcampClient;
    private ReportClient reportClient;
    private BootcampPersonaService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(BootcampPersonaRepositoryPort.class);
        personaRepository = Mockito.mock(PersonaRepositoryPort.class);
        bootcampClient = Mockito.mock(BootcampClient.class);
        reportClient = Mockito.mock(ReportClient.class);
        service = new BootcampPersonaService(repository, personaRepository, bootcampClient, reportClient);
    }

    @Test
    void registerInBootcamp_success() {
        // Arrange
        Long personaId = 1L, bootcampId = 100L;
        EnrollRequest request = new EnrollRequest();
        request.setBootcampId(bootcampId);
        request.setPersonaId(personaId);

        Persona persona = new Persona();
        persona.setId(personaId);
        persona.setNombre("Gabriel");
        persona.setCorreo("gabriel@email.com");

        BootcampPersona existingBp = new BootcampPersona();
        existingBp.setBootcampId(200L);

        BootcampSimpleResponse nuevo = new BootcampSimpleResponse();
        nuevo.setId(bootcampId);
        nuevo.setNombre("Java Bootcamp");
        nuevo.setFechaLanzamiento(LocalDate.of(2025, 1, 1));
        nuevo.setFechaFinalizacion(LocalDate.of(2025, 1, 31));

        BootcampSimpleResponse inscrito = new BootcampSimpleResponse();
        inscrito.setId(200L);
        inscrito.setNombre("Python Bootcamp");
        inscrito.setFechaLanzamiento(LocalDate.of(2024, 1, 1));
        inscrito.setFechaFinalizacion(LocalDate.of(2024, 2, 1));

        Mockito.when(personaRepository.findById(personaId)).thenReturn(Mono.just(persona));
        Mockito.when(repository.findByPersonaId(personaId)).thenReturn(Flux.just(existingBp));
        Mockito.when(bootcampClient.bringSimpleResponseForManyBootcamps(anyString()))
                .thenReturn(Flux.fromIterable(List.of(nuevo, inscrito)));
        Mockito.when(repository.save(any(BootcampPersona.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        Mockito.when(reportClient.postInscriptionReport(any(PersonaRegisteredResponse.class)))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.registerInBootcamp(request))
                .assertNext(result -> {
                    assertThat(result.getNombrePersona()).isEqualTo("Gabriel");
                    assertThat(result.getCorreoPersona()).isEqualTo("gabriel@email.com");
                    assertThat(result.getPersonaId()).isEqualTo(personaId);
                    assertThat(result.getBootcampId()).isEqualTo(bootcampId);
                    assertThat(result.getNombreBootcamp()).isEqualTo("Java Bootcamp");
                })
                .verifyComplete();
    }

    @Test
    void registerInBootcamp_personaNoExiste() {
        Long personaId = 1L, bootcampId = 100L;
        EnrollRequest request = new EnrollRequest();
        request.setBootcampId(bootcampId);
        request.setPersonaId(personaId);

        Mockito.when(personaRepository.findById(personaId)).thenReturn(Mono.empty());

        StepVerifier.create(service.registerInBootcamp(request))
                .expectErrorSatisfies(err -> assertThat(err)
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("La persona no existe"))
                .verify();
    }

    @Test
    void registerInBootcamp_maxBootcampsReached() {
        Long personaId = 1L, bootcampId = 100L;
        EnrollRequest request = new EnrollRequest();
        request.setBootcampId(bootcampId);
        request.setPersonaId(personaId);

        Persona persona = new Persona();
        persona.setId(personaId);

        List<BootcampPersona> inscritos = List.of(
                bp(1L), bp(2L), bp(3L), bp(4L), bp(5L)
        );

        Mockito.when(personaRepository.findById(personaId)).thenReturn(Mono.just(persona));
        Mockito.when(repository.findByPersonaId(personaId)).thenReturn(Flux.fromIterable(inscritos));

        StepVerifier.create(service.registerInBootcamp(request))
                .expectErrorSatisfies(err -> assertThat(err)
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessageContaining("No puedes inscribirte en más de 5 bootcamps"))
                .verify();
    }

    @Test
    void registerInBootcamp_fechaCruzaConOtroBootcamp() {
        Long personaId = 1L, bootcampId = 100L;
        EnrollRequest request = new EnrollRequest();
        request.setBootcampId(bootcampId);
        request.setPersonaId(personaId);

        Persona persona = new Persona();
        persona.setId(personaId);

        BootcampPersona existente = bp(200L);

        BootcampSimpleResponse nuevo = new BootcampSimpleResponse();
        nuevo.setId(bootcampId);
        nuevo.setNombre("Nuevo Bootcamp");
        nuevo.setFechaLanzamiento(LocalDate.of(2025, 1, 10));
        nuevo.setFechaFinalizacion(LocalDate.of(2025, 2, 10));

        BootcampSimpleResponse inscrito = new BootcampSimpleResponse();
        inscrito.setId(200L);
        inscrito.setNombre("Bootcamp Antiguo");
        inscrito.setFechaLanzamiento(LocalDate.of(2025, 1, 1));
        inscrito.setFechaFinalizacion(LocalDate.of(2025, 1, 20));

        Mockito.when(personaRepository.findById(personaId)).thenReturn(Mono.just(persona));
        Mockito.when(repository.findByPersonaId(personaId)).thenReturn(Flux.just(existente));
        Mockito.when(bootcampClient.bringSimpleResponseForManyBootcamps(anyString()))
                .thenReturn(Flux.fromIterable(List.of(nuevo, inscrito)));

        StepVerifier.create(service.registerInBootcamp(request))
                .expectErrorSatisfies(err -> assertThat(err)
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessageContaining("se cruza en fechas"))
                .verify();
    }

    @Test
    void registerInBootcamp_bootcampNoEncontradoEnRespuesta() {
        Long personaId = 1L, bootcampId = 100L;
        EnrollRequest request = new EnrollRequest();
        request.setBootcampId(bootcampId);
        request.setPersonaId(personaId);

        Persona persona = new Persona();
        persona.setId(personaId);

        BootcampPersona existente = bp(200L);

        BootcampSimpleResponse inscrito = new BootcampSimpleResponse();
        inscrito.setId(200L);
        inscrito.setNombre("Bootcamp Existente");
        inscrito.setFechaLanzamiento(LocalDate.of(2025, 1, 1));
        inscrito.setFechaFinalizacion(LocalDate.of(2025, 1, 20));

        Mockito.when(personaRepository.findById(personaId)).thenReturn(Mono.just(persona));
        Mockito.when(repository.findByPersonaId(personaId)).thenReturn(Flux.just(existente));
        Mockito.when(bootcampClient.bringSimpleResponseForManyBootcamps(anyString()))
                .thenReturn(Flux.just(inscrito)); // No incluye el nuevo

        StepVerifier.create(service.registerInBootcamp(request))
                .expectErrorSatisfies(err -> assertThat(err)
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessageContaining("Bootcamp a inscribir no encontrado"))
                .verify();
    }

    // Helper to create BootcampPersona
    private static BootcampPersona bp(Long bootcampId) {
        BootcampPersona bp = new BootcampPersona();
        bp.setPersonaId(1L);
        bp.setBootcampId(bootcampId);
        return bp;
    }
}
