package gabs.reports.application.event;

import gabs.reports.domain.event.PersonaInscritaEnBootcampEvent;
import gabs.reports.domain.model.Bootcamp;
import gabs.reports.domain.port.BootcampRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BootcampEventListenerTest {

    @Mock
    private BootcampRepositoryPort bootcampRepository;

    @InjectMocks
    private BootcampEventListener eventListener;

    private Bootcamp bootcamp;
    private PersonaInscritaEnBootcampEvent event;

    @BeforeEach
    void setUp() {
        bootcamp = new Bootcamp();
        bootcamp.setBootcampId(1L);
        bootcamp.setNombre("Java Bootcamp");
        bootcamp.setCantidadPersonasInscritas(2);
        bootcamp.setPersonasInscritas(new ArrayList<>(Arrays.asList(1L, 2L)));

        event = new PersonaInscritaEnBootcampEvent(1L, 3L);
    }

    @Test
    void handlePersonaInscrita_WithNewPersona_ShouldUpdateBootcamp() {
        // Given
        when(bootcampRepository.findByBootcampId(1L)).thenReturn(Mono.just(bootcamp));
        when(bootcampRepository.save(any(Bootcamp.class))).thenReturn(Mono.just(bootcamp));

        // When
        eventListener.handlePersonaInscrita(event);

        // Then
        verify(bootcampRepository).findByBootcampId(1L);
        verify(bootcampRepository).save(any(Bootcamp.class));
    }

    @Test
    void handlePersonaInscrita_WithExistingPersona_ShouldNotUpdateBootcamp() {
        // Given
        bootcamp.setPersonasInscritas(new ArrayList<>(Arrays.asList(1L, 2L, 3L)));
        when(bootcampRepository.findByBootcampId(1L)).thenReturn(Mono.just(bootcamp));
        when(bootcampRepository.save(any(Bootcamp.class))).thenReturn(Mono.just(bootcamp));

        // When
        eventListener.handlePersonaInscrita(event);

        // Then
        verify(bootcampRepository).findByBootcampId(1L);
        verify(bootcampRepository).save(any(Bootcamp.class));
    }

    @Test
    void handlePersonaInscrita_WithNonExistentBootcamp_ShouldNotUpdate() {
        // Given
        when(bootcampRepository.findByBootcampId(1L)).thenReturn(Mono.empty());

        // When
        eventListener.handlePersonaInscrita(event);

        // Then
        verify(bootcampRepository).findByBootcampId(1L);
        verify(bootcampRepository, never()).save(any(Bootcamp.class));
    }
} 