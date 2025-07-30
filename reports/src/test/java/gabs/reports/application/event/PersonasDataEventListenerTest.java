package gabs.reports.application.event;

import gabs.reports.domain.event.PersonasDataRequestEvent;
import gabs.reports.domain.model.Persona;
import gabs.reports.domain.port.PersonaRepositoryPort;
import gabs.reports.dto.PersonaInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonasDataEventListenerTest {

    @Mock
    private PersonaRepositoryPort personaRepository;

    @InjectMocks
    private PersonasDataEventListener eventListener;

    private Persona persona1;
    private Persona persona2;
    private List<Long> personaIds;
    private CompletableFuture<List<PersonaInfo>> future;

    @BeforeEach
    void setUp() {
        persona1 = new Persona();
        persona1.setPersonaId(1L);
        persona1.setNombre("Juan Pérez");
        persona1.setCorreo("juan.perez@email.com");
        persona1.setEdad(25);

        persona2 = new Persona();
        persona2.setPersonaId(2L);
        persona2.setNombre("María García");
        persona2.setCorreo("maria.garcia@email.com");
        persona2.setEdad(30);

        personaIds = Arrays.asList(1L, 2L);
        future = new CompletableFuture<>();
    }

    @Test
    void handlePersonasDataRequest_WithValidPersonas_ShouldCompleteFutureWithPersonaInfo() {
        // Given
        when(personaRepository.findByPersonaId(1L)).thenReturn(reactor.core.publisher.Mono.just(persona1));
        when(personaRepository.findByPersonaId(2L)).thenReturn(reactor.core.publisher.Mono.just(persona2));

        PersonasDataRequestEvent event = new PersonasDataRequestEvent(personaIds, future);

        // When
        eventListener.handlePersonasDataRequest(event);

        // Then
        try {
            List<PersonaInfo> result = future.get();
            assert result.size() == 2;
            assert result.get(0).getNombre().equals("Juan Pérez");
            assert result.get(0).getCorreo().equals("juan.perez@email.com");
            assert result.get(1).getNombre().equals("María García");
            assert result.get(1).getCorreo().equals("maria.garcia@email.com");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(personaRepository).findByPersonaId(1L);
        verify(personaRepository).findByPersonaId(2L);
    }

    @Test
    void handlePersonasDataRequest_WithEmptyPersonaIds_ShouldCompleteFutureWithEmptyList() {
        // Given
        List<Long> emptyPersonaIds = Arrays.asList();
        CompletableFuture<List<PersonaInfo>> emptyFuture = new CompletableFuture<>();
        PersonasDataRequestEvent event = new PersonasDataRequestEvent(emptyPersonaIds, emptyFuture);

        // When
        eventListener.handlePersonasDataRequest(event);

        // Then
        try {
            List<PersonaInfo> result = emptyFuture.get();
            assert result.isEmpty();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(personaRepository, never()).findByPersonaId(any());
    }

    @Test
    void handlePersonasDataRequest_WithNonExistentPersonas_ShouldCompleteFutureWithEmptyList() {
        // Given
        when(personaRepository.findByPersonaId(1L)).thenReturn(reactor.core.publisher.Mono.empty());
        when(personaRepository.findByPersonaId(2L)).thenReturn(reactor.core.publisher.Mono.empty());

        PersonasDataRequestEvent event = new PersonasDataRequestEvent(personaIds, future);

        // When
        eventListener.handlePersonasDataRequest(event);

        // Then
        try {
            List<PersonaInfo> result = future.get();
            assert result.isEmpty();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(personaRepository).findByPersonaId(1L);
        verify(personaRepository).findByPersonaId(2L);
    }
} 