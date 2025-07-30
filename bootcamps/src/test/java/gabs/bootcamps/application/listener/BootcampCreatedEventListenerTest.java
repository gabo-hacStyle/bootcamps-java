package gabs.bootcamps.application.listener;

import gabs.bootcamps.application.port.BootcampUseCases;
import gabs.bootcamps.domain.event.BootcampCreatedEvent;
import gabs.bootcamps.dto.BootcampResponse;
import gabs.bootcamps.dto.CapacidadDTO;
import gabs.bootcamps.dto.TecnologiaDTO;
import gabs.bootcamps.infraestructure.adapter.out.ReportsClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Arrays;


import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BootcampCreatedEventListenerTest {

    @Mock
    private BootcampUseCases bootcampUseCases;
    
    @Mock
    private ReportsClient reportsClient;

    private BootcampCreatedEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new BootcampCreatedEventListener(bootcampUseCases, reportsClient);
    }

    @Test
    void handleBootcampCreatedEvent_Success() {
        // Arrange
        Long bootcampId = 1L;
        BootcampCreatedEvent event = new BootcampCreatedEvent(bootcampId);
        BootcampResponse bootcampResponse = createTestBootcampResponse(bootcampId);

        when(bootcampUseCases.findById(bootcampId)).thenReturn(Mono.just(bootcampResponse));
        when(reportsClient.postBootcampReport(bootcampResponse)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(Mono.fromRunnable(() -> listener.handleBootcampCreatedEvent(event)))
                .verifyComplete();

        verify(bootcampUseCases).findById(bootcampId);
        verify(reportsClient).postBootcampReport(bootcampResponse);
    }

    @Test
    void handleBootcampCreatedEvent_BootcampNotFound() {
        // Arrange
        Long bootcampId = 999L;
        BootcampCreatedEvent event = new BootcampCreatedEvent(bootcampId);

        when(bootcampUseCases.findById(bootcampId)).thenReturn(Mono.error(new RuntimeException("Bootcamp not found")));

        // Act & Assert
        StepVerifier.create(Mono.fromRunnable(() -> listener.handleBootcampCreatedEvent(event)))
                .verifyComplete();

        verify(bootcampUseCases).findById(bootcampId);
        verify(reportsClient, never()).postBootcampReport(any());
    }

    @Test
    void handleBootcampCreatedEvent_ReportsServiceError() {
        // Arrange
        Long bootcampId = 1L;
        BootcampCreatedEvent event = new BootcampCreatedEvent(bootcampId);
        BootcampResponse bootcampResponse = createTestBootcampResponse(bootcampId);

        when(bootcampUseCases.findById(bootcampId)).thenReturn(Mono.just(bootcampResponse));
        when(reportsClient.postBootcampReport(bootcampResponse)).thenReturn(Mono.error(new RuntimeException("Reports service error")));

        // Act & Assert
        StepVerifier.create(Mono.fromRunnable(() -> listener.handleBootcampCreatedEvent(event)))
                .verifyComplete();

        verify(bootcampUseCases).findById(bootcampId);
        verify(reportsClient).postBootcampReport(bootcampResponse);
    }

    // ========== MÉTODOS AUXILIARES ==========

    private BootcampResponse createTestBootcampResponse(Long id) {
        BootcampResponse response = new BootcampResponse();
        response.setId(id);
        response.setNombre("Test Bootcamp " + id);
        response.setDescripcion("Test Description");
        response.setDuracion(30);
        response.setFechaLanzamiento(LocalDate.now());
        response.setFechaFinalizacion(LocalDate.now().plusDays(30));
        response.setCapacidades(Arrays.asList(
            new CapacidadDTO(1L, "Java", Arrays.asList(new TecnologiaDTO(1L, "Java")))
        ));
        return response;
    }
} 